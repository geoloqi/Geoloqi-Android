package com.geoloqi.android1;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class GeoloqiHTTPRequest {

	private static GeoloqiHTTPRequest singleton;
	
	public static GeoloqiHTTPRequest singleton() {
		if(singleton == null)
			singleton = new GeoloqiHTTPRequest();

		return singleton;
	}

	public void locationUpdate(Cursor cursor, LQLocationData db) {
		// Loop through the cursor and format a JSON object
		JSONArray json = new JSONArray();
		
		while(cursor.moveToNext()) {
			try {
				JSONObject update = new JSONObject();
				
				update.put("date", "2011-01-01T00:00:00Z");
				JSONObject location = new JSONObject();
					location.put("type", "point");
					JSONObject point = new JSONObject();
						point.put("latitude", cursor.getDouble(cursor.getColumnIndex(LQLocationData.LATITUDE)));
						point.put("longitude", cursor.getDouble(cursor.getColumnIndex(LQLocationData.LONGITUDE)));
						point.put("speed", cursor.getInt(cursor.getColumnIndex(LQLocationData.SPEED)));
						point.put("altitude", cursor.getInt(cursor.getColumnIndex(LQLocationData.ALTITUDE)));
						point.put("horizontal_accuracy", cursor.getInt(cursor.getColumnIndex(LQLocationData.HORIZONTAL_ACCURACY)));
					location.put("position", point);
				update.put("location", location);
				JSONObject raw = new JSONObject();
					raw.put("rate_limit", cursor.getInt(cursor.getColumnIndex(LQLocationData.RATE_LIMIT)));
				update.put("raw", raw);
				JSONObject client = new JSONObject();
					client.put("name", "Geoloqi");
				update.put("client", client);
				
				json.put(update);
				
			} catch(JSONException e) {
				Log.d(Geoloqi.TAG, "Exception building JSON object!");
			}
		}
		
		// Post the JSON object to the API
		Log.d(Geoloqi.TAG, json.toString());
		try {
			
			// TODO: HTTP Request stuff here
			
			
			
			db.clearSentPoints();
		} catch(Exception e) {
			// Don't remove the points from the queue if the HTTP request failed
			db.unmarkPointsForSending();
		}
		
	}
	
}
