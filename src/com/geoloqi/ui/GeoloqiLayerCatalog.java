package com.geoloqi.ui;

import com.geoloqi.android1.R;
import com.geoloqi.android1.R.id;
import com.geoloqi.android1.R.layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.WebView;

public class GeoloqiLayerCatalog extends Activity {
	
	private static GeoloqiLayerCatalog layerCatalog;
	@SuppressWarnings("unused")
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
