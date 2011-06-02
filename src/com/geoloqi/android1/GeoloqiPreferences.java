package com.geoloqi.android1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class GeoloqiPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private SharedPreferences preferences;
	public static final String PREF_RATELIMIT_KEY = "rate_limit";
	public static final String PREF_MINTIME_KEY = "min_time";
	public static final String PREF_USERNAME = "username";
	public static final String PREF_ACCESS_TOKEN = "access_token";
	public static final String PREF_REFRESH_TOKEN = "refres_token";
	public static final String PREF_EXPIRES_AT = "expires_at";
	public static final String PREF_SCOPE = "scope";
	private static GeoloqiPreferences staticPreferences;
	
	public static GeoloqiPreferences singleton() {
		if(staticPreferences == null)
			staticPreferences = new GeoloqiPreferences();

		return staticPreferences;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
	    if( key.equals( PREF_RATELIMIT_KEY ) ){
	    	// Reset the sending queue timer
	    	Log.d(Geoloqi.TAG, "New rate limit: " + GeoloqiPreferences.getRateLimit(this));
	    } else if ( key.equals( PREF_MINTIME_KEY) ) {
	    	Log.d(Geoloqi.TAG, "New tracking limit: " + GeoloqiPreferences.getMinTime(this));
	    } else {
	    	Log.d(Geoloqi.TAG, "Unknown preference changed");
	    }
	}
	
	public static int getRateLimit(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		String rateLimit = p.getString(PREF_RATELIMIT_KEY, "120");
		return Integer.parseInt(rateLimit);
	}

	public static int getMinTime(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		String minTime = p.getString(PREF_MINTIME_KEY, "5");
		return Integer.parseInt(minTime) * 1000;
	}

	public static LQToken getToken(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

		if(p.getString(PREF_ACCESS_TOKEN, null) == null)
			return null;

		return new LQToken(
			p.getString(PREF_ACCESS_TOKEN, null),
			p.getString(PREF_REFRESH_TOKEN, null),
			p.getLong(PREF_EXPIRES_AT, 0),
			p.getString(PREF_SCOPE, null)
		);
	}

	public static void setToken(LQToken token, Context context) {
		if(token == null)
			return;
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = p.edit();
		e.putString(PREF_ACCESS_TOKEN, token.accessToken);
		e.putString(PREF_REFRESH_TOKEN, token.refreshToken);
		e.putLong(PREF_EXPIRES_AT, token.expiresAt.getTime() / 1000l);
		e.putString(PREF_SCOPE, token.scope);
		e.commit();
		Log.d(Geoloqi.TAG, "Stored token in shared preferences");
	}
	
	public static void removeToken(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = p.edit();
		e.putString(PREF_ACCESS_TOKEN, null);
		e.putString(PREF_REFRESH_TOKEN, null);
		e.putString(PREF_EXPIRES_AT, null);
		e.putString(PREF_SCOPE, null);
		e.commit();
	}
	
	public static String getUsername(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		return p.getString(PREF_USERNAME, null);
	}

	public static void setUsername(String username, Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = p.edit();
		e.putString(PREF_USERNAME, username);
		e.commit();
	}
}
