package com.geoloqi.android1;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
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
	MediaPlayer player;
	LocationManager locationManager;
	LQLocationData db;
	int distanceFilter = 5;
	float trackingLimit = 10.0f;
	Timer sendingTimer;
	private Handler handler = new Handler();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		
		// player = MediaPlayer.create(this, R.raw.digitalsublime);
		// player.setLooping(false); // Set looping
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		db = new LQLocationData(this);
		
		int rateLimit = GeoloqiPreferences.getRateLimit(this);
		sendingTimer = new Timer();
		sendingTimer.schedule(new LQSendingTimerTask(), 0, rateLimit * 1000);
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		// player.stop();
		sendingTimer.cancel();
		Log.d(TAG, "Points: " + db.numberOfUnsentPoints());
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		// player.start();
		
		String bestProvider = locationManager.getBestProvider(new Criteria(), true);
		locationManager.requestLocationUpdates(bestProvider, distanceFilter, trackingLimit, this);
		
		Log.d(TAG, "Provider: " + bestProvider);
	}

	public void onLocationChanged(Location location) {
		Log.d(TAG, location.toString());
		int rateLimit = GeoloqiPreferences.getRateLimit(this);
		db.addLocation(location, distanceFilter, (int)trackingLimit, rateLimit);
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
	public void restartSendingQueue() {
		int rateLimit = GeoloqiPreferences.getRateLimit(this);
		sendingTimer.cancel();
		sendingTimer = new Timer();
		sendingTimer.schedule(new LQSendingTimerTask(), 0, rateLimit * 1000);
		Log.d(TAG, "Restarting timer task for sending points to server");
	}
	
	// TODO: Is there something better than AsyncTask to use here since this is a background service?
	class LQFlushQueue extends AsyncTask<Void, Void, Void> {

		// Doesn't have access to the UI thread
		@Override
		protected Void doInBackground(Void... v) {
			Log.d(TAG, "Flushing queue...");
			
			// Get all unsent points from the DB
			// Send to the Geoloqi API
			GeoloqiHTTPRequest.singleton().locationUpdate(db);
			
			return null;
		}

		protected void onProgressUpdate() {

		}

		// Runs with the return value of doInBackground
		@Override
		protected void onPostExecute(Void v) {
			Log.d(TAG, "Flush queue completed");
			
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
