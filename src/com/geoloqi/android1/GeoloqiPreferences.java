package com.geoloqi.android1;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class GeoloqiPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private SharedPreferences preferences;
	private static final String PREF_RATELIMIT_KEY = "rate_limit";
	
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
	    	Log.d(Geoloqi.TAG, "New rate limit: "+getRateLimit());
	    }
	}
	
	public int getRateLimit() {
		return Integer.parseInt(preferences.getString(PREF_RATELIMIT_KEY, "300"));
	}
	
	public void setRateLimit() {
		
	}
}
