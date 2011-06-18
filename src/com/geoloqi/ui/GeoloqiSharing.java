package com.geoloqi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.geoloqi.Util;
import com.geoloqi.android2.R;

public class GeoloqiSharing extends Activity implements OnClickListener {

	EditText shareMessage;
	Spinner shareSpinner;
	Button shareButton;

	// BEGIN LIFECYCLE METHODS

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		buildGUI(savedInstanceState.getString("message"));
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
