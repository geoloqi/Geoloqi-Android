package com.geoloqi.android1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class GeoloqiPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private SharedPreferences preferences;
	public static final String PREF_RATELIMIT_KEY = "rate_limit";
	private static GeoloqiPreferences staticPreferences;
	
	public static GeoloqiPreferences singleton() {
		if(staticPreferences == null)
			staticPreferences = new GeoloqiPreferences();

		// staticPreferences.preferences = getPreferences(MODE_PRIVATE);

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
	    }
	}
	
	public static int getRateLimit(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		String rateLimit = p.getString(PREF_RATELIMIT_KEY, "300");
		Log.d(Geoloqi.TAG, "Preferences: " + rateLimit);
		return Integer.parseInt(rateLimit);
	}
	
	public void setRateLimit() {
		
	}
}
