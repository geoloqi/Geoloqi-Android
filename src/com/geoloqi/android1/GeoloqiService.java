package com.geoloqi.android1;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class GeoloqiService extends Service implements LocationListener {
	private static final String TAG = "GeoloqiService";
	private static final int NOTIFICATION_ID = 1024;
	MediaPlayer player;
	LocationManager locationManager;
	LQLocationData db;
	Date lastPointReceived;
	Date lastPointSent;
	int distanceFilter = 5;
	float trackingLimit = 10.0f;
	int rateLimit;
	Timer sendingTimer;
	private Handler handler = new Handler();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		// Toast.makeText(this, "Geoloqi Tracker Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		db = new LQLocationData(this);
		
		rateLimit = GeoloqiPreferences.getRateLimit(this);
		sendingTimer = new Timer();
		sendingTimer.schedule(new LQSendingTimerTask(), 0, rateLimit * 1000);
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Geoloqi Tracker Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		
		locationManager.removeUpdates(this);
		sendingTimer.cancel();
		
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(GeoloqiService.NOTIFICATION_ID);
		Log.d(TAG, "Points: " + db.numberOfUnsentPoints());
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "Geoloqi Tracker Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		
		String bestProvider = locationManager.getBestProvider(new Criteria(), true);
		locationManager.requestLocationUpdates(bestProvider, distanceFilter, trackingLimit, this);
		
		// From http://developer.android.com/guide/topics/ui/notifiers/notifications.html
		
		// Get a reference to the notification manager
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Instantiate the notification
		CharSequence tickerText = "Geoloqi tracker is running";
		Notification notification = new Notification(R.drawable.ic_stat_notify, tickerText, System.currentTimeMillis());
		
		// Define the Notification's expanded message and Intent
		Context context = getApplicationContext();
		CharSequence contentTitle = "Geoloqi";
		CharSequence contentText = "GPS tracker is running";
		Intent notificationIntent = new Intent(this, Geoloqi.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);		

		// Pass the Notification to the NotificationManager
		notificationManager.notify(GeoloqiService.NOTIFICATION_ID, notification);
		
		Log.d(TAG, "Provider: " + bestProvider);
	}

	public void onLocationChanged(Location location) {
		Log.d(TAG, location.toString());
		// Ignore points closer together than 1 second
		if(lastPointReceived == null || lastPointReceived.getTime() < System.currentTimeMillis() - 1000)
		{
			lastPointReceived = new Date();
			db.addLocation(location, distanceFilter, (int)trackingLimit, rateLimit);

			// If the user has changed the rate limit, reset the timer
			int newRateLimit = GeoloqiPreferences.getRateLimit(this);
			if(newRateLimit != rateLimit) {
				sendingTimer.cancel();
				sendingTimer = new Timer();
				sendingTimer.schedule(new LQSendingTimerTask(), 0, newRateLimit * 1000);
				rateLimit = newRateLimit;
			}
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}
	
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
			// Log.d(TAG, "Flushing queue...");
			
			// Get all unsent points from the DB and send to the Geoloqi API
			GeoloqiHTTPRequest.singleton().locationUpdate(db);
			
			return null;
		}

		protected void onProgressUpdate() {

		}

		// Runs with the return value of doInBackground
		@Override
		protected void onPostExecute(Void v) {
			// Log.d(TAG, "Flush queue completed");
			
		}		
	}
	
	public class LQSendingTimerTask extends TimerTask {
		private Runnable runnable = new Runnable() {
			public void run() {
				new LQFlushQueue().execute();
			}
		};

		@Override
		public void run() {
			handler.post(runnable);
		}
	}
	
}
