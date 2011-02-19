package com.geoloqi.android1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class GeoloqiHTTPRequest {

	private static GeoloqiHTTPRequest singleton;
	private String urlBase = "https://api.geoloqi.com/1/";
	public Date lastSent;
	public int updateInProgress = 0;
	
	public static GeoloqiHTTPRequest singleton() {
		if(singleton == null) {
			singleton = new GeoloqiHTTPRequest();
		}
		return singleton;
	}

	public void locationUpdate(LQLocationData db, Context context) {
		if(updateInProgress == 1){
			Log.i(Geoloqi.TAG, "HTTP Request in progress, won't update this time");
			return;
		}
		
		updateInProgress = 1;
		
		Cursor cursor = db.getUnsentPoints();

		// Loop through the cursor and format a JSON object
		JSONArray json = new JSONArray();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
		sdf.setTimeZone(Calendar.getInstance().getTimeZone());
		
		if(cursor == null) {
			updateInProgress = 0;
			return;
		}
		
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
				updateInProgress = 0;
			}
		}
		
		cursor.close();
		
		if(json.length() == 0) {
			updateInProgress = 0;
			return;
		}
		
		// Post the JSON object to the API
		// Log.d(Geoloqi.TAG, json.toString());
		try {
	        LQToken token = GeoloqiPreferences.getToken(context);
	        if(token == null) {
				updateInProgress = 0;
	        	throw new Exception("No access token present. Won't attempt to send points.");
	        }
	        
			HttpClient client = new DefaultHttpClient();  
	        String postURL = this.urlBase + "location/update";
	        HttpPost post = new HttpPost(postURL); 
	        post.setHeader("Content-type", "application/json");

	        post.setHeader("Authorization", "OAuth " + token.accessToken);

            StringEntity ent = new StringEntity(json.toString(),HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse responsePOST = client.execute(post);  
            HttpEntity resEntity = responsePOST.getEntity();  
            if (resEntity != null) {    
	        	String responseString = EntityUtils.toString(resEntity);
	            JSONObject response = new JSONObject(responseString);

	            if(response.has("error"))
	            {
	    			db.unmarkPointsForSending();
	    			
	            	// If the error was because of an expired token, refresh the token and try again
	            	if(response.getString("error").equals("expired_token")) {
	    				token = GeoloqiHTTPRequest.singleton().oauthToken(token.refreshToken);
	    				if(token != null) {
	    					GeoloqiPreferences.setToken(token, context);
	    					// Recurse!
	    					updateInProgress = 0;
	    					locationUpdate(db, context);
	    					return;
	    				}
	            	} else {
	        			updateInProgress = 0;
		            	throw new Exception(response.get("error") + " " + response.get("error_description"));
	            	}
	            }

	            Log.d(Geoloqi.TAG, "Completed. Deleting all sent points. " + responseString);
	            this.lastSent = new Date();
				db.clearSentPoints();
				updateInProgress = 0;
            } else {
            	Log.i(Geoloqi.TAG, "Unknown error");
    			db.unmarkPointsForSending();
    			updateInProgress = 0;
            }
		} catch(Exception e) {
			// Don't remove the points from the queue if the HTTP request failed
			Log.i(Geoloqi.TAG, "Error sending points: " + e.getMessage());
			db.unmarkPointsForSending();
			updateInProgress = 0;
		}
	}

	public String accountUsername(Context context) {

		try {
	        LQToken token = GeoloqiPreferences.getToken(context);
	        if(token == null) {
	        	throw new Exception("No access token present.");
	        }
	        
			HttpClient client = new DefaultHttpClient();  
	        String postURL = this.urlBase + "account/username";
	        HttpGet request = new HttpGet(postURL); 
	        request.setHeader("Authorization", "OAuth " + token.accessToken);

            HttpResponse responsePOST = client.execute(request);
            HttpEntity resEntity = responsePOST.getEntity();
            if (resEntity != null) {    
	        	String responseString = EntityUtils.toString(resEntity);
	            JSONObject response = new JSONObject(responseString);

	            if(response.has("error")) {
	            	// If the error was because of an expired token, give up :)
	            	if(response.getString("error").equals("expired_token")) {
    					throw new Exception("Expired token");
	            	} else {
	            		throw new Exception("Unknown error: " + response.getString("error"));
	            	}
	            }
	            else if(response.has("username")) {
	            	return response.getString("username");
	            }
	            else {
	            	throw new Exception("Unknown response: " + responseString);
	            }
            } else {
            	throw new Exception("Empty response");
            }
		} catch(Exception e) {
			return null;
		}
	}
	
	public LQToken oauthToken(String username, String password) {
		try {
			HttpClient client = new DefaultHttpClient();  
	        String postURL = this.urlBase + "oauth/token";
	        HttpPost post = new HttpPost(postURL); 

	        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("grant_type", "password"));
	        params.add(new BasicNameValuePair("client_id", GeoloqiConstants.GEOLOQI_ID));
	        params.add(new BasicNameValuePair("client_secret", GeoloqiConstants.GEOLOQI_SECRET));
	        params.add(new BasicNameValuePair("username", username));
	        params.add(new BasicNameValuePair("password", password));
	        post.setEntity(new UrlEncodedFormEntity(params));

	        HttpResponse responsePOST = client.execute(post);  
	        HttpEntity resEntity = responsePOST.getEntity();  
	        if (resEntity != null) {    
	        	String responseString = EntityUtils.toString(resEntity);
	            JSONObject response = new JSONObject(responseString);

	            if(response.has("error"))
	            {
	            	// Handle friendly user/pass login error messages here
	            	throw new Exception(response.get("error") + " " + response.get("error_description"));
	            }
	            
	            return new LQToken(
	            			response.get("access_token").toString(), 
	            			response.get("refresh_token").toString(), 
	            			response.get("expires_in").toString(), 
	            			response.get("scope").toString());
	        } else {
	        	Log.i(Geoloqi.TAG, "++++ Error getting token");
	        }
		} catch(Exception e) {
			Log.i(Geoloqi.TAG, "Error getting token: " + e.toString());
		}
        return null;
	}
	
	public LQToken oauthToken(String refreshToken) {
		try {
			HttpClient client = new DefaultHttpClient();  
	        String postURL = this.urlBase + "oauth/token";
	        HttpPost post = new HttpPost(postURL); 

	        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
	        params.add(new BasicNameValuePair("client_id", GeoloqiConstants.GEOLOQI_ID));
	        params.add(new BasicNameValuePair("client_secret", GeoloqiConstants.GEOLOQI_SECRET));
	        params.add(new BasicNameValuePair("refresh_token", refreshToken));
	        post.setEntity(new UrlEncodedFormEntity(params));

	        HttpResponse responsePOST = client.execute(post);  
	        HttpEntity resEntity = responsePOST.getEntity();  
	        if (resEntity != null) {    
	        	String responseString = EntityUtils.toString(resEntity);

	            JSONObject response = new JSONObject(responseString);

	            if(response.has("error"))
	            	throw new Exception(response.get("error") + " " + response.get("error_description"));
	            
	            return new LQToken(
	            			response.get("access_token").toString(), 
	            			response.get("refresh_token").toString(), 
	            			response.get("expires_in").toString(), 
	            			response.get("scope").toString());
	        } else {
	        	Log.i(Geoloqi.TAG, "++++ Error getting token");
	        }
		} catch(Exception e) {
			Log.i(Geoloqi.TAG, "Error getting token: " + e.toString());
		}
        return null;
	}
	
}
