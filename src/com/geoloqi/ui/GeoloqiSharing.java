package com.geoloqi.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.geoloqi.Util;
import com.geoloqi.android2.R;
import com.geoloqi.rpc.GeoloqiHTTPClient;
import com.geoloqi.rpc.RPCException;
import com.geoloqi.rpc.SharingLink;

public class GeoloqiSharing extends Activity implements OnClickListener {

	EditText shareMessage;
	Spinner shareSpinner;
	Button shareButton;
	Integer time;
	String message;
	SharingLink link;
	Intent shareIntent = new Intent(Intent.ACTION_SEND);

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

	@SuppressWarnings("unchecked")
	public void onClick(View src) {
		if (src.getId() == R.id.buttonShare) {
			String item = (String) shareSpinner.getSelectedItem();
			message = shareMessage.getText().toString();
			Util.log("Message is: " + message);
			Util.log("Selection is: " + item);
			if (item == "no time limit") {
				time = null;
			} else if (item == "10 minutes") {
				time = 10;
			} else if (item == "20 minutes") {
				time = 20;
			} else if (item == "30 minutes") {
				time = 30;
			} else if (item == "1 hour") {
				time = 60;
			} else if (item == "2 hours") {
				time = 120;
			} else if (item == "4 hours") {
				time = 240;
			} else if (item == "8 hours") {
				time = 480;
			} else if (item == "24 hours") {
				time = 3600;
			} else if (item == "4 days") {
				time = 14400;
			} else if (item == "7 days") {
				time = 25200;
			} else {
				time = 10;
			}

			new MakeLink().execute(new Pair<Integer, String>(time, message));
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

	private class MakeLink extends AsyncTask<Pair<Integer, String>, Void, SharingLink> {

		ProgressDialog progressDialog;

		@Override
		public void onPreExecute() {
			progressDialog = ProgressDialog.show(GeoloqiSharing.this, "Working...", "Creating link");
		}

		@Override
		protected SharingLink doInBackground(Pair<Integer, String>... params) {

			try {
				return GeoloqiHTTPClient.postSharingLink(GeoloqiSharing.this, params[0].first, params[0].second);
			} catch (RPCException e) {
				Util.log("Error in Geoloqi Sharing: " + e.getMessage());
				return null;
			}
		}

		@Override
		public void onPostExecute(SharingLink link) {
			progressDialog.dismiss();
			if (link == null) {
				Toast.makeText(GeoloqiSharing.this, "An error occurred.", Toast.LENGTH_LONG);
			} else {
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, message + " " + link.shortLink);
				startActivity(Intent.createChooser(shareIntent, "Share Location"));
			}
			GeoloqiSharing.this.finish();
		}
	}
}
