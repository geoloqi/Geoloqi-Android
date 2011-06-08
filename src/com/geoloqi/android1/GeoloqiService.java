package com.geoloqi.android1;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PatternMatcher;
import android.util.Log;
import android.widget.Toast;

public class GeoloqiService extends Service implements LocationListener {
	private static final String TAG = "GeoloqiService";
	private static final int NOTIFICATION_ID = 1024;
	private Notification notification;
	private LocationManager locationManager;
	private LQLocationData db;
	private Date lastPointReceived;
	protected Date lastPointSent;
	private final float minDistance = 0.0f;
	private long minTime = 1000l;
	private int rateLimit;
	private Timer sendingTimer;
	private final Handler handler = new Handler();
	private int lastBatteryLevel;
	private BatteryReceiver batteryReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		// Toast.makeText(this, "Geoloqi Tracker Created", Toast.LENGTH_LONG).show();
		getApplicationContext().registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				log("DATA: "+intent.getDataString());
			}
			
		}, new IntentFilter());
		
		Log.d(TAG, "onCreate");
		log("In GeoloqiService.onCreate()");
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		db = new LQLocationData(this);
		
		rateLimit = GeoloqiPreferences.getRateLimit(this);
		sendingTimer = new Timer();
		sendingTimer.schedule(new LQSendingTimerTask(), 0, rateLimit * 1000);

		batteryReceiver = new BatteryReceiver();
		registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		log("Leaving GeoloqiService.onCreate()");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Geoloqi Tracker Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		log("In GeoloqiService.onDestroy()");
		
		locationManager.removeUpdates(this);
		unregisterReceiver(batteryReceiver);
		sendingTimer.cancel();
		
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(GeoloqiService.NOTIFICATION_ID);

		// Flush the queue now
		GeoloqiHTTPRequest.singleton().locationUpdate(db, GeoloqiService.this);
		log("Leaving GeoloqiService.onDestroy()");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "Geoloqi Tracker Started", Toast.LENGTH_LONG).show();
		
		minTime = GeoloqiPreferences.getMinTime(this);

		// String bestProvider = locationManager.getBestProvider(new Criteria(), true);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
		
		// From http://developer.android.com/guide/topics/ui/notifiers/notifications.html
		
		// Get a reference to the notification manager
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Instantiate the notification
		CharSequence tickerText = "Geoloqi tracker is running";
		notification = new Notification(R.drawable.ic_stat_notify, tickerText, System.currentTimeMillis());
		
		// Define the Notification's expanded message and Intent
		Context context = getApplicationContext();
		CharSequence contentTitle = "Geoloqi";
		CharSequence contentText = "GPS tracker is running";
		Intent notificationIntent = new Intent(this, Geoloqi.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);		

		// Pass the Notification to the NotificationManager
		notificationManager.notify(GeoloqiService.NOTIFICATION_ID, notification);
	}

	public void onLocationChanged(Location location) {
		log("In GeoloqiService.onLocationChanged()");
		int newMinTime = GeoloqiPreferences.getMinTime(this);
		Log.d(TAG, location.toString());
		Log.d(TAG, "Min time: " + newMinTime);
		// Ignore points closer together than minTime
		if(lastPointReceived == null || lastPointReceived.getTime() < System.currentTimeMillis() - newMinTime)
		{
			// If accuracy worse than 600 meters, do nothing.  (super rough position appears to be about 1000m)
			if(location.hasAccuracy() && location.getAccuracy() > 600)
				return;
			// Accuracy is now better than 600 meters or the device cannot report the accuracy.
			
			// Record that this new point has been received.
			lastPointReceived = new Date();
			
			//Store the location of the new point.
			db.addLocation(location, minDistance, minTime, rateLimit, lastBatteryLevel);

			// If the user has changed the rate limit, reset the timer
			int newRateLimit = GeoloqiPreferences.getRateLimit(this);			
			if(newRateLimit != rateLimit) {
				Log.i(Geoloqi.TAG, ">>> Restarting sending timer");
				sendingTimer.cancel();
				sendingTimer = new Timer();
				sendingTimer.schedule(new LQSendingTimerTask(), 0, newRateLimit * 1000);
				rateLimit = newRateLimit;
			}
			// Timer is now correct.
			
			// If the user has changed the minimum time, reset the location update request interval.
			if(newMinTime != minTime) {
				Log.i(Geoloqi.TAG, ">>> Re-registering GPS updates");
				minTime = newMinTime;
				locationManager.removeUpdates(this);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
			}
			//  Location update request interval is now correct.
			
			//Get the application context
			Context context = getApplicationContext();
			
			// Broadcast an intent to change the user's location.
			Uri uri = Util.encodeLocation(location);
			Intent updateLocation = new Intent(Intent.ACTION_EDIT, uri);
			
			

			IntentFilter filter = new IntentFilter(Intent.ACTION_EDIT);
			filter.addDataScheme("geo");
			filter.addDataAuthority("geoloqi.com", null);
			filter.addDataPath(".*", PatternMatcher.PATTERN_SIMPLE_GLOB);
			
			String message = "";
			filter.match(updateLocation.getAction(),updateLocation.resolveType(this),updateLocation.getScheme(),updateLocation.getData(),updateLocation.getCategories(),message);
			log(message);
			
			
			log("Broadcasting \""+uri+"\"");
			context.sendBroadcast(updateLocation);
			log("Finished broadcasting.");
			// Intent to change the user's location is now broadcast.
			
			// Notify the user of their new location.
			//// Define the Notification's expanded message and Intent
			CharSequence contentTitle = "Geoloqi";
			CharSequence contentText = "" + new DecimalFormat("#.0").format(location.getSpeed() * 3.6) + " km/h, " + db.numberOfUnsentPoints() + " points";
			Intent notificationIntent = new Intent(this, Geoloqi.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			notification.flags = Notification.FLAG_ONGOING_EVENT;
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);//FIXME This is deprecated.

			//// Get a reference to the notification manager
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			//// Pass the Notification to the NotificationManager
			notificationManager.notify(GeoloqiService.NOTIFICATION_ID, notification);
			// User is now notified of their location.
			log("Leaving GeoloqiService.onLocationChanged()");
		}
	}

	public void onProviderDisabled(String provider) { }

	public void onProviderEnabled(String provider) { }

	public void onStatusChanged(String provider, int status, Bundle extras) { }
	
	/**
	 * Kill and reset the timer for sending the points to the server
	 */
	/*
	public void restartSendingQueue() {
		int rateLimit = GeoloqiPreferences.getRateLimit(this);
		sendingTimer.cancel();
		sendingTimer = new Timer();
		sendingTimer.schedule(new LQSendingTimerTask(), 0, rateLimit * 1000);
		Log.d(TAG, "Restarting timer task for sending points to server");
	}
	*/
	
	// TODO: Is there something better than AsyncTask to use here since this is a background service?
	class LQFlushQueue extends AsyncTask<Void, Void, Void> {

		// Doesn't have access to the UI thread
		@Override
		protected Void doInBackground(Void... v) {
			// Get all unsent points from the DB and send to the Geoloqi API
			GeoloqiHTTPRequest.singleton().locationUpdate(db, GeoloqiService.this);
			return null;
		}

		protected void onProgressUpdate() {
		}

		// Runs with the return value of doInBackground
		@Override
		protected void onPostExecute(Void v) {
			lastPointSent = GeoloqiHTTPRequest.singleton().lastSent;
		}		
	}
	
	public class LQSendingTimerTask extends TimerTask {
		private final Runnable runnable = new Runnable() {
			public void run() {
				new LQFlushQueue().execute();
			}
		};

		@Override
		public void run() {
			handler.post(runnable);
		}
	}
	
	private class BatteryReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			lastBatteryLevel = intent.getIntExtra("level", 0);
		}
	};
	
	public static void log(String message){
		Log.i("bpl29",message);
	}
}
