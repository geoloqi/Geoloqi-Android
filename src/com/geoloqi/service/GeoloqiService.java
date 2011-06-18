package com.geoloqi.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Pair;
import android.widget.Toast;

import com.geoloqi.BatteryReceiver;
import com.geoloqi.Util;
import com.geoloqi.rpc.RPCBinder;

public class GeoloqiService extends Service implements LocationListener {
	private static Date lastUpdate;
	private final GeoloqiMessenger messenger = new GeoloqiMessenger();
	private final BatteryReceiver battery = new BatteryReceiver();
	private final LocationCollection backlog = new LocationCollection(this, "backlog");

	private Intent lastCounterUpdate;
	private Intent lastLocationUpdate;

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
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		onStart(intent, startid);
		return Service.START_REDELIVER_INTENT;
	}

	@SuppressWarnings("unchecked")
	public void onLocationChanged(Location location) {
		if (shouldUpdate(location)) {
			lastUpdate = new Date();
			if (lastLocationUpdate != null) {
				removeStickyBroadcast(lastLocationUpdate);
			}
			backlog.insertLocation(location, new Pair<String, String>("Battery", "" + battery.getBatteryLevel()));
			lastLocationUpdate = new Intent(Intent.ACTION_EDIT, Util.encodeLocation(location));
			sendStickyBroadcast(lastLocationUpdate);
			broadcastUnsentPointCount();
			if (shouldSend()) {
				messenger.rezendezvous.release();
			}
		}
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
		return messenger.rezendezvous.availablePermits() == 0;
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
				if (Util.getToken(GeoloqiService.this) != null) {
					sendData();
				}
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
				try {
					RPCBinder.singleton().postLocationUpdate(Util.getToken(GeoloqiService.this), queue.toList());
					success = true;
					Util.log("Send Succeeded.");
					if (fullMessage)
						regulator.sendSucceeded();
				} catch (RemoteException e) {
					Util.log("Remote Exception in sendData: " + e.getMessage());
				}
			}
		}
	}

}
