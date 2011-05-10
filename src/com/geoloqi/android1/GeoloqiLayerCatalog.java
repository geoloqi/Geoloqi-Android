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
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GeoloqiLayerCatalog extends Activity {
	
	private static GeoloqiLayerCatalog layerCatalog;
	private SharedPreferences preferences;
	private WebView webView;
	
	public static GeoloqiLayerCatalog singleton() {
		if(layerCatalog == null)
			layerCatalog = new GeoloqiLayerCatalog();

		return layerCatalog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layer_catalog);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		webView = (WebView)findViewById(R.id.webView);
		webView.loadUrl("https://a.geoloqi.com/layer/list?oauth_token=" + GeoloqiPreferences.getToken(this).accessToken);
	}
}
