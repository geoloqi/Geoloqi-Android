package com.geoloqi;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geoloqi.android2.R;
import com.geoloqi.ui.Geoloqi;

public class GeoloqiPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
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
		
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
	    if( key.equals( PREF_RATELIMIT_KEY ) ){
	    	// Reset the sending queue timer
	    	Log.d(Geoloqi.TAG, "New rate limit: " + Util.getRateLimit(this));
	    } else if ( key.equals( PREF_MINTIME_KEY) ) {
	    	Log.d(Geoloqi.TAG, "New tracking limit: " + Util.getMinTime(this));
	    } else {
	    	Log.d(Geoloqi.TAG, "Unknown preference changed");
	    }
	}
}
