package com.geoloqi.service;

import java.util.Date;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.geoloqi.Util;
import com.geoloqi.messaging.GeoloqiMessenger;

public class GeoloqiService extends Service implements LocationListener {
	private static Date lastUpdate;
	GeoloqiMessenger messenger;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Util.log("=========================GeoloqiService========================");

		messenger = GeoloqiMessenger.singleton(this);
		new Thread(messenger).start();

		long minTime = Util.getMinTime(this);
		float minDistance = Util.getDistanceFilter();
		((LocationManager) getSystemService(LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,minDistance, this);
		
		Util.log("Leaving GeoloqiService.onCreate()");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Geoloqi Tracker Stopped", Toast.LENGTH_LONG).show();
		((LocationManager) getSystemService(LOCATION_SERVICE)).removeUpdates(this);

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Util.NOTIFICATION_ID);
		
		messenger.stop();
	}
	
	@Override 
	public void onStart(Intent intent, int startid){
		Toast.makeText(this, "Geoloqi Tracker Started", Toast.LENGTH_LONG).show();
		if(!messenger.isRunning()){
			new Thread(messenger).start();
		}

		long minTime = Util.getMinTime(this);
		float minDistance = Util.getDistanceFilter();
		((LocationManager) getSystemService(LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,minDistance, this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		onStart(intent, startid);
		return Service.START_REDELIVER_INTENT;
	}

	public void onLocationChanged(Location location) {
		// Util.log("Got a location.");
		if(shouldUpdate(location)) {
			Util.log("Broadcasting new location update.");
			lastUpdate = new Date();
			
			// Broadcast an intent to change the user's location.
			Uri uri = Util.encodeLocation(location);
			Intent updateLocation = new Intent(Intent.ACTION_EDIT, uri);
			getApplicationContext().sendBroadcast(updateLocation);
		}else{
			// Util.log("Ignoring location update from the OS.");
		}
	}
	
	private boolean shouldUpdate(Location location) {
		boolean haveNotUpdated, timeElapsed, isAccurate;
		haveNotUpdated = lastUpdate == null;
		if(haveNotUpdated)
			return true;
		timeElapsed = lastUpdate.getTime() < System.currentTimeMillis() - Util.getMinTime(this);
		isAccurate = !location.hasAccuracy() || location.getAccuracy() < 600;
		return timeElapsed && isAccurate;
	}
	
	public static Date getLastUpdate() {
		return lastUpdate;
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
