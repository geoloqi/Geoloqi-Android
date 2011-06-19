package com.geoloqi.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import com.geoloqi.android2.R;

class GeoloqiLayerCatalog extends Activity {

	private static GeoloqiLayerCatalog layerCatalog;
	@SuppressWarnings("unused")
	private SharedPreferences preferences;
	private WebView webView;

	public static GeoloqiLayerCatalog singleton() {
		if (layerCatalog == null)
			layerCatalog = new GeoloqiLayerCatalog();

		return layerCatalog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layer_catalog);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		webView = (WebView) findViewById(R.id.webView);
		//FIXME		webView.loadUrl("https://a.geoloqi.com/layer/list?oauth_token=" + Util.getToken(this).accessToken);
	}
}
