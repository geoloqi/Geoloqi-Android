package com.geoloqi.ui;

import java.text.DecimalFormat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geoloqi.GeoloqiPreferences;
import com.geoloqi.GeoloqiReceiver;
import com.geoloqi.Util;
import com.geoloqi.android2.R;
import com.geoloqi.rpc.GeoloqiHTTPClient;
import com.geoloqi.service.GeoloqiService;

public class Geoloqi extends Activity implements OnClickListener {

	public static final String TAG = "Geoloqi";
	private static final int LOGIN_DIALOG_ID = 1;
	private static final int SIGNUP_DIALOG_ID = 2;

	// GUI Elements
	private Button buttonStart, buttonSignup, buttonLayerCatalog, buttonShare;
	private TextView latLabel, lngLabel, altLabel, spdLabel, accLabel, numPointsLabel, accountLabel;

	// End GUI Elements

	private BroadcastReceiver locationUpdateReceiver, messengerUpdateReceiver;
	private Location lastLocation = new Location("Geoloqi");

	// private Integer lastUnsentPointCountDebug = 0;

	// BEGIN LIFECYCLE METHODS
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Util.logo();
		super.onCreate(savedInstanceState);
		buildGUI();
		locationUpdateReceiver = new UIUpdateReceiver();
		messengerUpdateReceiver = new MessengerUpdateReceiver();
		this.runOnUiThread(new UpdateUI(null, null));

		ImageView image = (ImageView) findViewById(R.id.geoloqiLogo);
		image.setImageResource(R.drawable.geoloqi_300x100);

		if (!Util.isServiceRunning(this, GeoloqiService.class.getName())) {
			ComponentName response = startService(new Intent(this, GeoloqiService.class));
			if (response == null) {
				Util.log("Geoloqi could not start GeoloqiService");
				throw new RuntimeException("Geoloqi could not start GeoloqiService");
			}
		} else {
		}
	}

	@Override
	public void onStart() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_EDIT);
		filter.addDataScheme("geo");
		filter.addDataAuthority("geoloqi.com", null);
		filter.addDataPath(".*", PatternMatcher.PATTERN_SIMPLE_GLOB);
		registerReceiver(locationUpdateReceiver, filter);
		IntentFilter filter2 = new IntentFilter(Intent.ACTION_EDIT);
		filter2.addDataScheme("mupdate");
		filter2.addDataAuthority("geoloqi.com", null);
		filter2.addDataPath(".*", PatternMatcher.PATTERN_SIMPLE_GLOB);
		registerReceiver(messengerUpdateReceiver, filter2);
		this.runOnUiThread(new UpdateUI(null, null));
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		unregisterReceiver(locationUpdateReceiver);
		unregisterReceiver(messengerUpdateReceiver);
		super.onStop();
	}

	@Override
	public void onRestart() {
		super.onRestart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "Inflating menu!");
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	// END LIFECYCLE METHODS

	// BEGIN EVENT HANDLERS

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.settings:
			Intent preferences = new Intent(this, GeoloqiPreferences.class);
			startActivity(preferences);
			return true;
		case R.id.login:
			this.showDialog(LOGIN_DIALOG_ID);
			return true;
		case R.id.quit:
			stopService(new Intent(this, GeoloqiService.class));
			System.exit(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View src) {
		switch (src.getId()) {
		case R.id.buttonStart:
			if (!Util.isServiceRunning(this.getApplicationContext(), GeoloqiService.class.getName())) {
				startService(new Intent(this, GeoloqiService.class));
				buttonStart.setText("Stop Tracking");
			} else {
				stopService(new Intent(this, GeoloqiService.class));
				buttonStart.setText("Start Tracking");
			}
			break;
		case R.id.buttonLayerCatalog:
			Intent layerCatalog = new Intent(this, GeoloqiLayerCatalog.class);
			startActivity(layerCatalog);
			break;
		case R.id.buttonSignup:
			this.showDialog(SIGNUP_DIALOG_ID);
			break;
		case R.id.buttonShare:
			Intent sharing = new Intent(this, GeoloqiSharing.class);
			startActivity(sharing);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case LOGIN_DIALOG_ID:
			return buildLoginDialog();
		case SIGNUP_DIALOG_ID:
			return buildSignupDialog();
		default:
			return null;
		}
	}

	// END EVENT HANDLERS

	// BEGIN GUI BUILDERS

	private void buildGUI() {
		setContentView(R.layout.main);

		buttonStart = (Button) findViewById(R.id.buttonStart);
		buttonSignup = (Button) findViewById(R.id.buttonSignup);
		buttonLayerCatalog = (Button) findViewById(R.id.buttonLayerCatalog);
		buttonShare = (Button) findViewById(R.id.buttonShare);
		latLabel = (TextView) findViewById(R.id.textLatitude);
		lngLabel = (TextView) findViewById(R.id.textLongitude);
		altLabel = (TextView) findViewById(R.id.textAltitude);
		spdLabel = (TextView) findViewById(R.id.textSpeed);
		accLabel = (TextView) findViewById(R.id.textAccuracy);
		numPointsLabel = (TextView) findViewById(R.id.textNumPointsInQueue);
		accountLabel = (TextView) findViewById(R.id.textAccount);

		buttonStart.setOnClickListener(this);
		buttonSignup.setOnClickListener(this);
		buttonLayerCatalog.setOnClickListener(this);
		buttonShare.setOnClickListener(this);
	}

	private AlertDialog buildLoginDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.login_dialog, (ViewGroup) findViewById(R.id.root));

		final EditText email = (EditText) layout.findViewById(R.id.editTextEmail);
		final EditText pwd = (EditText) layout.findViewById(R.id.editTextPassword);
		TextView account = (TextView) layout.findViewById(R.id.textAccount);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);

		if (GeoloqiHTTPClient.isLoggedIn(Geoloqi.this)) {
			account.setText("Currently logged in as " + GeoloqiHTTPClient.getUsername(Geoloqi.this));
			builder.setTitle("Change Account");
		} else {
			builder.setTitle("Log In");
		}
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account acct : accounts) {
			if (acct.name.matches(".+@.+..+")) {
				email.setText(acct.name);
			}
		}

		builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (GeoloqiHTTPClient.logIn(Geoloqi.this, email.getText().toString(), pwd.getText().toString())) {
					Toast.makeText(Geoloqi.this, "Logged in!", Toast.LENGTH_LONG).show();
					runOnUiThread(new UpdateUI(null, null));
				} else {
					Toast.makeText(Geoloqi.this, "Error logging in", Toast.LENGTH_LONG).show();
				}
			}
		});

		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Geoloqi.this.removeDialog(LOGIN_DIALOG_ID);
			}
		});

		return builder.create();
	}

	private AlertDialog buildSignupDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.signup_dialog, (ViewGroup) findViewById(R.id.root));

		final EditText email = (EditText) layout.findViewById(R.id.editTextEmail);
		final EditText name = (EditText) layout.findViewById(R.id.editTextName);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);

		builder.setTitle("Create Your Account");

		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {
			if (account.name.matches(".+@.+..+")) {
				email.setText(account.name);
			}
		}

		builder.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (GeoloqiHTTPClient.signUp(Geoloqi.this, email.getText().toString(), name.getText().toString())) {
					Toast.makeText(Geoloqi.this, "Success! Check your email!", Toast.LENGTH_LONG).show();
					runOnUiThread(new UpdateUI(null, null));
				} else {
					Toast.makeText(Geoloqi.this, "Error signing up", Toast.LENGTH_LONG).show();
				}
				Geoloqi.this.removeDialog(SIGNUP_DIALOG_ID);
			}
		});

		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Geoloqi.this.removeDialog(SIGNUP_DIALOG_ID);
			}
		});

		return builder.create();
	}

	private class UIUpdateReceiver extends GeoloqiReceiver {

		UIUpdateReceiver() {
		}

		@Override
		public void onReceive(Context context, Location location) {
			runOnUiThread(new UpdateUI(location, null));
		}
	}

	private class MessengerUpdateReceiver extends BroadcastReceiver {

		MessengerUpdateReceiver() {
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			Integer unsentPointCount = Integer.parseInt(intent.getData().getPathSegments().get(0));
			runOnUiThread(new UpdateUI(null, unsentPointCount));
		}
	}

	private class UpdateUI implements Runnable {

		private Location location;
		private final Integer unsentPointCount;

		UpdateUI(Location location, Integer unsentPointCount) {
			this.location = location;
			this.unsentPointCount = unsentPointCount;
		}

		public void run() {
			// Util.log("Updating UI!");

			if (Util.isServiceRunning(Geoloqi.this, GeoloqiService.class.getName())) {
				buttonStart.setText("Stop Tracking");
			} else {
				buttonStart.setText("Start Tracking");
			}

			if (location != null) {
				latLabel.setText((new DecimalFormat("0.00000").format(location.getLatitude())));
				lngLabel.setText((new DecimalFormat("0.00000").format(location.getLongitude())));
				altLabel.setText(location.getAltitude() + "m");
				spdLabel.setText(location.getSpeed() + " km/h");
				accLabel.setText(location.getAccuracy() + "m");
				lastLocation = location;
			} else {
				location = lastLocation;
			}

			if (unsentPointCount != null) {
				numPointsLabel.setText("" + unsentPointCount);
				// lastUnsentPointCountDebug = unsentPointCount;
			}
			// boolean loggedIn = username != null || username != "(anonymous)";
			// boolean tracking = Util.isServiceRunning(Geoloqi.this,GeoloqiService.class.getName());
			//
			// Util.logMainInterface(lastLocation, lastUnsentPointCountDebug,
			// loggedIn, tracking);

			String username = GeoloqiHTTPClient.getUsername(Geoloqi.this);
			accountLabel.setText(username);

			if (username == "(not logged in)" || username == "(anonymous)") {
				Util.log("User is anonymous or not logged in.");
				buttonLayerCatalog.setVisibility(View.GONE);
				buttonSignup.setVisibility(View.VISIBLE);
				buttonShare.setVisibility(View.GONE);
			} else {
				Util.log("User is logged in.");
				buttonLayerCatalog.setVisibility(View.VISIBLE);
				buttonSignup.setVisibility(View.GONE);
				buttonShare.setVisibility(View.VISIBLE);
			}

		}
	}
}
