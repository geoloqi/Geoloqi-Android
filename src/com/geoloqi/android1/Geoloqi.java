package com.geoloqi.android1;

import java.text.DecimalFormat;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Geoloqi extends Activity implements OnClickListener {

	// Broadcast Receiver for listening to the Geoloqi Service
	private static LocationBroadcastReceiver receiver;

	public static final String TAG = "Geoloqi";
	private static final int LOGIN_DIALOG_ID = 1;
	private static final int SIGNUP_DIALOG_ID = 2;
	private Button buttonStart, buttonLayerCatalog, buttonSignup; // ,
																	// buttonStop,
																	// buttonUpdate;
	@SuppressWarnings("unused")
	private TextView latLabel, lngLabel, numPointsLabel, altLabel, spdLabel,
			accLabel, lastSentLabel, accountLabel, textNotLoggedIn;
	protected LQLocationData db;
	@SuppressWarnings("unused")
	private final Handler handler = new Handler();
	public Context context;
	private String username;

	public static void log(String message){
		Log.i("bpl29",message);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = this;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Register the receiver
		log("Registering the receiver.");
		receiver = new LocationBroadcastReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_EDIT);
		filter.addDataScheme("geo");
		filter.addDataAuthority("geoloqi.com", null);
		filter.addDataPath(".*", PatternMatcher.PATTERN_SIMPLE_GLOB);
		context.registerReceiver(receiver, filter);

		// Initialize GUI
		buttonStart = (Button) findViewById(R.id.buttonStart);
		buttonSignup = (Button) findViewById(R.id.buttonSignup);
		buttonLayerCatalog = (Button) findViewById(R.id.buttonLayerCatalog);
		buttonLayerCatalog.setVisibility(View.INVISIBLE);
		latLabel = (TextView) findViewById(R.id.textLatitude);
		lngLabel = (TextView) findViewById(R.id.textLongitude);
		altLabel = (TextView) findViewById(R.id.textAltitude);
		spdLabel = (TextView) findViewById(R.id.textSpeed);
		accLabel = (TextView) findViewById(R.id.textAccuracy);
		numPointsLabel = (TextView) findViewById(R.id.textNumPointsInQueue);
		accountLabel = (TextView) findViewById(R.id.textAccount);
		// // lastSentLabel = (TextView) findViewById(R.id.textLastSent);
		textNotLoggedIn = (TextView) findViewById(R.id.textNotLoggedIn);
		textNotLoggedIn.setVisibility(View.INVISIBLE);

		buttonStart.setOnClickListener(this);
		buttonSignup.setOnClickListener(this);
		buttonLayerCatalog.setOnClickListener(this);
		// // buttonStop.setOnClickListener(this);
		// // buttonUpdate.setOnClickListener(this);
		// GUI Initialized.

		// Initialize location database wrapper.
		db = new LQLocationData(this);

		// FIXME: Timer instantiates new thread to start AsyncTask. Non-UI threads should not start AsyncTasks.
		// FIXME: Refer to http://developer.android.com/reference/android/os/AsyncTask.html > Threading Rules
		//new Timer().schedule(new MyTimerTask(), 0, 1000);
		
		if (!isServiceRunning()) {
			buttonStart.setText("Stop Tracking");
			startService(new Intent(this, GeoloqiService.class));
		} else {
			buttonStart.setText("Start Tracking");
		}

		ImageView image = (ImageView) findViewById(R.id.geoloqiLogo);
		image.setImageResource(R.drawable.geoloqi_300x100);
		new LQGetUsername().execute();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		log("Unregistering the receiver.");
		context.unregisterReceiver(receiver);
	}

	@Override
	public void onPause() {
	}
	
	@Override
	public void onResume() {
		Log.i(TAG, "Resuming...");
		super.onResume();
		new LQGetUsername().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "Inflating menu!");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

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
			if (!isServiceRunning()) {
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
		// case R.id.buttonStop:
		// Log.d(TAG, "onClick: stopping service");
		// stopService(new Intent(this, GeoloqiService.class));
		// break;
		// case R.id.buttonUpdate:
		// Log.d(TAG, "onClick: update");
		// new LQUpdateUI().execute();
		// break;
		}
	}

	private AlertDialog buildLoginDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.login_dialog,
				(ViewGroup) findViewById(R.id.root));

		final EditText email = (EditText) layout
				.findViewById(R.id.editTextEmail);
		final EditText pwd = (EditText) layout
				.findViewById(R.id.editTextPassword);
		TextView account = (TextView) layout.findViewById(R.id.textAccount);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);

		if (username != null) {
			account.setText("Currently logged in as " + username);
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

		builder.setPositiveButton("Log In",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LQToken token = GeoloqiHTTPRequest.singleton()
								.oauthToken(email.getText().toString(),
										pwd.getText().toString());
						Geoloqi.this.removeDialog(LOGIN_DIALOG_ID);
						if (token == null) {
							Toast.makeText(context, "Error logging in",
									Toast.LENGTH_LONG).show();
						} else {
							GeoloqiPreferences.setToken(token, Geoloqi.this);
							Log.d(Geoloqi.TAG,
									"Got access token: " + token.toString());
							Toast.makeText(context, "Logged in!",
									Toast.LENGTH_LONG).show();
							username = null;
							GeoloqiPreferences.setUsername(null, Geoloqi.this);
							new LQGetUsername().execute();
						}
					}
				});

		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Geoloqi.this.removeDialog(LOGIN_DIALOG_ID);
					}
				});

		return builder.create();
	}

	private AlertDialog buildSignupDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.signup_dialog,
				(ViewGroup) findViewById(R.id.root));

		final EditText email = (EditText) layout
				.findViewById(R.id.editTextEmail);
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

		builder.setPositiveButton("Sign Up",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LQToken token = GeoloqiHTTPRequest.singleton()
								.createUser(email.getText().toString(),
										name.getText().toString());
						Geoloqi.this.removeDialog(SIGNUP_DIALOG_ID);
						if (token == null) {
							Toast.makeText(context, "Error signing up",
									Toast.LENGTH_LONG).show();
						} else {
							GeoloqiPreferences.setToken(token, Geoloqi.this);
							Log.d(Geoloqi.TAG,
									"Got access token: " + token.toString());
							Toast.makeText(context,
									"Success! Check your email!",
									Toast.LENGTH_LONG).show();
							username = null;
							GeoloqiPreferences.setUsername(null, Geoloqi.this);
							new LQGetUsername().execute();
						}
					}
				});

		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Geoloqi.this.removeDialog(SIGNUP_DIALOG_ID);
					}
				});

		return builder.create();
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

	public boolean isServiceRunning() {
		log("In isServiceRunning");
		final ActivityManager activityManager = (ActivityManager) getSystemService(Geoloqi.ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

		boolean isServiceFound = false;

		for (int i = 0; i < services.size(); i++) {
			if ("com.geoloqi.android1".equals(services.get(i).service.getPackageName())) {
				if ("com.geoloqi.android1.GeoloqiService".equals(services.get(i).service.getClassName())) {
					isServiceFound = true;
				}
			}
		}
		log("Leaving isServiceRunning");
		return isServiceFound;
	}

	class LQGetUsername extends AsyncTask<Void, Void, String> {
		// Doesn't have access to the UI thread
		@Override
		protected String doInBackground(Void... v) {
			// Attempt to retrieve the username from the preferences
			String storedUsername = GeoloqiPreferences.getUsername(context);
			// If it's not there, then make a server call to get the username
			if (storedUsername == null || storedUsername.equals("")
					|| storedUsername.equals("(anonymous)")) {
				storedUsername = GeoloqiHTTPRequest.singleton()
						.accountUsername(context);
				GeoloqiPreferences.setUsername(storedUsername, context);
				Log.i(TAG, ">>> got new username! " + storedUsername);
			}
			return storedUsername;
		}

		protected void onProgressUpdate() {

		}

		// Runs with the return value of doInBackground, has access to the UI
		// thread
		@Override
		protected void onPostExecute(String newUsername) {
			username = newUsername;
			if (username == null || username.equals("(anonymous)")) {
				accountLabel.setText("(not logged in)");
				buttonLayerCatalog.setVisibility(View.INVISIBLE);
				textNotLoggedIn.setVisibility(View.VISIBLE);
				buttonSignup.setVisibility(View.VISIBLE);
			} else {
				accountLabel.setText(username);
				buttonLayerCatalog.setVisibility(View.VISIBLE);
				textNotLoggedIn.setVisibility(View.INVISIBLE);
				buttonSignup.setVisibility(View.INVISIBLE);
			}
		}
	}

/*
	class LQUpdateUI extends AsyncTask<Void, Void, LQPoint> {

		// Doesn't have access to the UI thread
		@Override
		protected LQPoint doInBackground(Void... v) {
			return db.getLastLocation();
		}

		protected void onProgressUpdate() {

		}

		// Runs with the return value of doInBackground, has access to the UI
		// thread
		@Override
		protected void onPostExecute(LQPoint point) {
			if (point == null)
				return;

			latLabel.setText((new DecimalFormat("#.00000")
					.format(point.latitude)));
			lngLabel.setText((new DecimalFormat("#.00000")
					.format(point.longitude)));
			altLabel.setText("" + point.altitude + "m");
			spdLabel.setText("" + point.speed + " km/h");
			accLabel.setText("" + point.horizontalAccuracy + "m");
			numPointsLabel.setText("" + db.numberOfUnsentPoints());

			if (username == null || username.equals("(anonymous)")) {
				accountLabel.setText("(not logged in)");
				buttonLayerCatalog.setVisibility(View.INVISIBLE);
				textNotLoggedIn.setVisibility(View.VISIBLE);
				buttonSignup.setVisibility(View.VISIBLE);
			} else {
				accountLabel.setText(username);
				buttonLayerCatalog.setVisibility(View.VISIBLE);
				textNotLoggedIn.setVisibility(View.INVISIBLE);
				buttonSignup.setVisibility(View.INVISIBLE);
			}

			// TODO: Talk to the service to find out the date the last point was
			// sent
			// Date lastSent = ???
			// if(lastSent == null)
			// return;
			//
			// lastSentLabel.setText(""+((System.currentTimeMillis()/1000) -
			// lastSent.getTime()) + " seconds");
		}
	}

	public class MyTimerTask extends TimerTask {
		private Runnable runnable = new Runnable() {
			public void run() {
				new LQUpdateUI().execute();
				if (isServiceRunning()) {
					buttonStart.setText("Stop Tracking");
				} else {
					buttonStart.setText("Start Tracking");
				}
			}
		};

		@Override
		public void run() {
			handler.post(runnable);
		}
	}
*/
	private class LocationBroadcastReceiver extends BroadcastReceiver {

		LocationBroadcastReceiver() {
			super();
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			runOnUiThread(new UpdateUI(Util.decodeLocation(intent.getData())));
		}

	}

	// WARNING: DO NOT RUN THIS OUTSIDE OF THE UI THREAD.
	private class UpdateUI implements Runnable {

		private final Location location;

		UpdateUI(Location location) {
			this.location = location;
		}

		public void run() {
			log("Updating UI!");
			latLabel.setText((new DecimalFormat("#.00000").format(location.getLatitude())));
			lngLabel.setText((new DecimalFormat("#.00000").format(location.getLongitude())));
			altLabel.setText("" + location.getAltitude() + "m");
			spdLabel.setText("" + location.getSpeed() + " km/h");
			accLabel.setText("" + location.getAccuracy() + "m");
			numPointsLabel.setText("" + db.numberOfUnsentPoints());
			
			if (username == null || username.equals("(anonymous)")) {
				accountLabel.setText("(not logged in)");
				buttonLayerCatalog.setVisibility(View.INVISIBLE);
				textNotLoggedIn.setVisibility(View.VISIBLE);
				buttonSignup.setVisibility(View.VISIBLE);
			} else {
				accountLabel.setText(username);
				buttonLayerCatalog.setVisibility(View.VISIBLE);
				textNotLoggedIn.setVisibility(View.INVISIBLE);
				buttonSignup.setVisibility(View.INVISIBLE);
			}

		}

	}
}