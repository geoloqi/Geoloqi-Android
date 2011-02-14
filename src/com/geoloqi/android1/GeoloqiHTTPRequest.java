package com.geoloqi.android1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
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

	public void locationUpdate(LQLocationData db) {
		Cursor cursor = db.getUnsentPoints();

		// Loop through the cursor and format a JSON object
		JSONArray json = new JSONArray();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
		sdf.setTimeZone(Calendar.getInstance().getTimeZone());
		
		if(cursor == null)
			return;
		
		while(cursor.moveToNext()) {
			try {
				JSONObject update = new JSONObject();
				
				Date d = new Date(cursor.getLong(cursor.getColumnIndex(LQLocationData.DATE)) * 1000);
				
				update.put("date", sdf.format(d));
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
					raw.put("battery", "");
					raw.put("rate_limit", cursor.getInt(cursor.getColumnIndex(LQLocationData.RATE_LIMIT)));
				update.put("raw", raw);
				JSONObject client = new JSONObject();
					client.put("name", "Geoloqi");
					client.put("version", "11.044");
					client.put("platform", "Android");
					client.put("hardware", "unknown");
				update.put("client", client);
				
				json.put(update);
				
			} catch(JSONException e) {
				Log.d(Geoloqi.TAG, "Exception building JSON object!");
			}
		}
		
		cursor.close();
		
		if(json.length() == 0)
			return;
		
		// Post the JSON object to the API
		Log.d(Geoloqi.TAG, json.toString());
		try {
			
			HttpClient client = new DefaultHttpClient();  
	        String postURL = "https://api.geoloqi.com/1/location/update";
	        HttpPost post = new HttpPost(postURL); 
	        post.setHeader("Content-type", "application/json");
	        // TODO: Replace this with the real OAuth token
	        post.setHeader("Authorization", "OAuth " + GeoloqiConstants.GEOLOQI_TOKEN);

            StringEntity ent = new StringEntity(json.toString(),HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse responsePOST = client.execute(post);  
            HttpEntity resEntity = responsePOST.getEntity();  
            if (resEntity != null) {    
                Log.i(Geoloqi.TAG,EntityUtils.toString(resEntity));
            }
			
			db.clearSentPoints();
		} catch(Exception e) {
			// Don't remove the points from the queue if the HTTP request failed
			Log.i(Geoloqi.TAG, "Error sending points: " + e.getMessage());
			db.unmarkPointsForSending();
		}
		
	}
	
}
