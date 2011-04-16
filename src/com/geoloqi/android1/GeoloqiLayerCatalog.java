package com.geoloqi.android1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class GeoloqiLayerCatalog extends Activity {
	
	private static GeoloqiLayerCatalog layerCatalog;
	private SharedPreferences preferences;
	
	public static GeoloqiLayerCatalog singleton() {
		if(layerCatalog == null)
			layerCatalog = new GeoloqiLayerCatalog();

		return layerCatalog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public static LQToken getToken(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

		if(p.getString(GeoloqiPreferences.PREF_ACCESS_TOKEN, null) == null)
			return null;

		return new LQToken(
			p.getString(GeoloqiPreferences.PREF_ACCESS_TOKEN, null),
			p.getString(GeoloqiPreferences.PREF_REFRESH_TOKEN, null),
			p.getLong(GeoloqiPreferences.PREF_EXPIRES_AT, 0),
			p.getString(GeoloqiPreferences.PREF_SCOPE, null)
		);
	}

}
