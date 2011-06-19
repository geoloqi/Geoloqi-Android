package com.geoloqi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.geoloqi.Util;
import com.geoloqi.android2.R;
import com.geoloqi.rpc.GeoloqiHTTPClient;
import com.geoloqi.rpc.SharingLink;

public class GeoloqiSharing extends Activity implements OnClickListener {

	EditText shareMessage;
	Spinner shareSpinner;
	Button shareButton;

	// BEGIN LIFECYCLE METHODS

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null && savedInstanceState.containsKey("message")) {
			buildGUI(savedInstanceState.getString("message"));
		} else {
			buildGUI("Heading out.  Track me on Geoloqi!");
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// END LIFECYCLE METHODS

	// BEGIN EVENT HANDLERS

	public void onClick(View src) {
		if (src.getId() == R.id.buttonShare) {
			String message = shareMessage.getText().toString();
			Object item = shareSpinner.getSelectedItem();
			Util.log("Message is: " + message);
			Util.log("Selection is: " + item);
			Integer time;
			if (message == "no time limit") {
				time = null;
			} else if (message == "10 minutes") {
				time = 10;
			} else if (message == "20 minutes") {
				time = 20;
			} else if (message == "30 minutes") {
				time = 30;
			} else if (message == "1 hour") {
				time = 60;
			} else if (message == "2 hours") {
				time = 120;
			} else if (message == "4 hours") {
				time = 240;
			} else if (message == "8 hours") {
				time = 480;
			} else if (message == "24 hours") {
				time = 3600;
			} else if (message == "4 days") {
				time = 14400;
			} else if (message == "7 days") {
				time = 25200;
			} else {
				time = 10;
			}

			SharingLink link = GeoloqiHTTPClient.postSharingLink(time, message);
			Intent shareIntent = new Intent("SHARING_LINK", link.shortLink);
			sendBroadcast(shareIntent);
			finish();
		}

	}

	//END EVENT HANDLERS

	private void buildGUI(String message) {
		setContentView(R.layout.share);

		shareMessage = (EditText) findViewById(R.id.shareMessage);
		shareButton = (Button) findViewById(R.id.buttonShare);
		shareSpinner = (Spinner) findViewById(R.id.shareSpinner);

		if (message != null) {
			shareMessage.setText(message);
		}

		shareButton.setOnClickListener(this);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.entryvalues_sharing_time_limit, android.R.layout.simple_spinner_item);
		shareSpinner.setAdapter(adapter);
	}
}
