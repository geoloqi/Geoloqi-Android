package com.geoloqi.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Pair;
import android.widget.Toast;

import com.geoloqi.BatteryReceiver;
import com.geoloqi.Util;
import com.geoloqi.android2.R;
import com.geoloqi.rpc.GeoloqiHTTPClient;
import com.geoloqi.ui.Geoloqi;

public class GeoloqiService extends Service implements LocationListener {
	private static final int NOTIFICATION_ID = 1024;
	private Date lastUpdate;
	private Date lastSend;
	private final GeoloqiMessenger messenger = new GeoloqiMessenger();
	private final BatteryReceiver battery = new BatteryReceiver();
	private final LocationCollection backlog = new LocationCollection(this, "backlog");

	private Intent lastCounterUpdate;
	private Intent lastLocationUpdate;

	private Notification notification;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		broadcastUnsentPointCount();
		registerReceiver(battery, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		((LocationManager) getSystemService(LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, Util.getMinTime(this), Util.getDistanceFilter(), this);
	}

	@Override
	public void onDestroy() {
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
		unregisterReceiver(battery);
		Toast.makeText(this, "Geoloqi Tracker Stopped", Toast.LENGTH_LONG).show();
		((LocationManager) getSystemService(LOCATION_SERVICE)).removeUpdates(this);
		messenger.running = false;
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "Geoloqi Tracker Started", Toast.LENGTH_LONG).show();
		new Thread(messenger).start();
		if (!messenger.running) {
			new Thread(messenger).start();
		}

		long minTime = Util.getMinTime(this);
		float minDistance = Util.getDistanceFilter();
		((LocationManager) getSystemService(LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);

		CharSequence tickerText = "Geoloqi tracker is running";
		notification = new Notification(R.drawable.ic_stat_notify, tickerText, System.currentTimeMillis());
		CharSequence contentTitle = "Geoloqi";
		CharSequence contentText = tickerText;
		Intent notificationIntent = new Intent(this, Geoloqi.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		onStart(intent, startid);
		return Service.START_REDELIVER_INTENT;
	}

	@SuppressWarnings("unchecked")
	public void onLocationChanged(Location location) {
	    // If location is accurate, append to location backlog
	    if (shouldUpdate(location)) {
    		if (lastLocationUpdate != null) {
    			removeStickyBroadcast(lastLocationUpdate);
    		}
    		backlog.insertLocation(location, new Pair<String, String>("Battery", "" + battery.getBatteryLevel()));
    		lastLocationUpdate = new Intent(Intent.ACTION_EDIT, Util.encodeLocation(location));
    		sendStickyBroadcast(lastLocationUpdate);
    		broadcastUnsentPointCount();
    
    		notification.flags = Notification.FLAG_ONGOING_EVENT;
    		CharSequence contentTitle = "Geoloqi";
    		CharSequence contentText = "speed: " + location.getSpeed() + "km/h, " + (backlog.size() + messenger.queue.size()) + " points";
    		Intent notificationIntent = new Intent(this, Geoloqi.class);
    		
    		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);// FIXME This is deprecated.
    		((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
    
            lastUpdate = new Date();

            if (shouldSend()) {
    			messenger.rezendezvous.release();
    			lastSend = new Date();
    		}
	    }
	}
	
	private boolean isAccurate(Location location) {
	    return !location.hasAccuracy() || location.getAccuracy() < 600;
	}

	private boolean shouldUpdate(Location location) {
		boolean haveNotUpdated, timeElapsed, isAccurate;
		haveNotUpdated = lastUpdate == null;
		if (haveNotUpdated)
			return true;
		timeElapsed = lastUpdate.getTime() < System.currentTimeMillis() - Util.getMinTime(this);
		isAccurate = !location.hasAccuracy() || location.getAccuracy() < 600;
		return timeElapsed && isAccurate;
	}
	
	private boolean shouldSend() {
	    boolean okToSend = false;
	    boolean send = false;
	    boolean timeElapsed;
	    
	    okToSend = messenger.rezendezvous.availablePermits() == 0;

        if(okToSend) {
        	if(lastSend == null) {
        		send = true;
        	} else {
	    		timeElapsed = lastSend.getTime() < System.currentTimeMillis() - Util.getRateLimit(this);
	        	if(timeElapsed) {
	        		send = true;
	        	}
        	}
        }
        
        Util.log("Should Send: "+send);
	    return send;
	}

	private void broadcastUnsentPointCount() {
		if (lastCounterUpdate != null) {
			removeStickyBroadcast(lastCounterUpdate);
		}
		Uri uri = new Uri.Builder().scheme("mupdate").authority("geoloqi.com").path("/" + (backlog.size() + messenger.queue.size()) + "/").build();
		lastCounterUpdate = new Intent(Intent.ACTION_EDIT, uri);
		Util.log("Updating messagecount: " + uri);
		sendStickyBroadcast(lastCounterUpdate);
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public class GeoloqiMessenger implements Runnable {

		private final HTTPRegulator regulator = new HTTPRegulator();
		private boolean running;

		private final Semaphore rezendezvous = new Semaphore(0, true);
		private final LocationCollection queue = new LocationCollection(GeoloqiService.this, "queue");

		protected GeoloqiMessenger() {
		}

		public void run() {
			Util.log("messenger is running");
			running = true;
			while (running) {
				rezendezvous.acquireUninterruptibly();
				queue.transfer(backlog, regulator.getWindowSize());
				sendData();
				queue.clear();
				broadcastUnsentPointCount();
			}
			Util.log("messenger is going down");
		}

		private void sendData() {
			boolean success = false;
			List<String> list = queue.toList();
			boolean fullMessage = list.size() == regulator.getWindowSize();
			while (!success && running) {
				GeoloqiHTTPClient.postLocationUpdate(GeoloqiService.this, queue);
				success = true;
				Util.log("Send Succeeded.");
				if (fullMessage)
					regulator.sendSucceeded();
			}
		}
	}

}
