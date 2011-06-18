package com.geoloqi.ui;

import java.text.DecimalFormat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.os.RemoteException;
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
import com.geoloqi.rpc.OAuthToken;
import com.geoloqi.rpc.RPCBinder;
import com.geoloqi.service.GeoloqiService;

public class Geoloqi extends Activity implements OnClickListener {

	public static final String TAG = "Geoloqi";
	private static final int LOGIN_DIALOG_ID = 1;
	private static final int SIGNUP_DIALOG_ID = 2;
	private static final int NOTIFICATION_ID = 1024;

	// GUI Elements
	private Button buttonStart, buttonSignup, buttonLayerCatalog, buttonShare;
	private TextView latLabel, lngLabel, altLabel, spdLabel, accLabel, numPointsLabel, accountLabel;
	private Notification notification;
	// End GUI Elements

	@SuppressWarnings("unused")
	private BroadcastReceiver locationUpdateReceiver, messengerUpdateReceiver;

	private String username;
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
		startService(new Intent(this, GeoloqiService.class));
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
		new LogIn().execute();
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
		super.onStop();
	}

	@Override
	public void onRestart() {
		super.onRestart();
	}

	@Override
	public void onDestroy() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
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
		Util.log("onClick!");
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
			Util.log("Sharing!");
			Intent sharing = new Intent(this, GeoloqiSharing.class);
			startActivity(sharing);
			break;
		default:
			Util.log("Couldn't land this click: " + src.getId());
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

		// Initialize the notification.
		CharSequence tickerText;
		if (!Util.isServiceRunning(this, GeoloqiService.class.getName())) {
			ComponentName response = startService(new Intent(this, GeoloqiService.class));
			if (response == null) {
				Util.log("Geoloqi could not start GeoloqiService");
				throw new RuntimeException("Geoloqi could not start GeoloqiService");
			}
			buttonStart.setText("Stop Tracking");
			tickerText = "Geoloqi tracker is running";
		} else {
			buttonStart.setText("Start Tracking");
			tickerText = "Welcome to Geoloqi";
		}

		notification = new Notification(R.drawable.ic_stat_notify, tickerText, System.currentTimeMillis());
		CharSequence contentTitle = "Geoloqi";
		CharSequence contentText = tickerText;
		Intent notificationIntent = new Intent(Geoloqi.this, Geoloqi.class);
		PendingIntent contentIntent = PendingIntent.getActivity(Geoloqi.this, 0, notificationIntent, 0);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(Geoloqi.this, contentTitle, contentText, contentIntent);
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
	}

	private AlertDialog buildLoginDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.login_dialog, (ViewGroup) findViewById(R.id.root));

		final EditText email = (EditText) layout.findViewById(R.id.editTextEmail);
		final EditText pwd = (EditText) layout.findViewById(R.id.editTextPassword);
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

		builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				OAuthToken token = null;
				try {
					token = RPCBinder.singleton().getToken(email.getText().toString(), pwd.getText().toString(), null);
				} catch (RemoteException e) {
				}
				Geoloqi.this.removeDialog(LOGIN_DIALOG_ID);
				if (token == null) {
					Toast.makeText(Geoloqi.this, "Error logging in", Toast.LENGTH_LONG).show();
				} else {
					Util.setToken(token, Geoloqi.this);
					Log.d(Geoloqi.TAG, "Got access token: " + token.toString());
					Toast.makeText(Geoloqi.this, "Logged in!", Toast.LENGTH_LONG).show();
					username = null;
					Util.setUsername(null, Geoloqi.this);
					new LogIn().execute();
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
				OAuthToken token = null;
				try {
					token = RPCBinder.singleton().getToken(email.getText().toString(), name.getText().toString(), null);
				} catch (RemoteException e) {
				}
				Geoloqi.this.removeDialog(SIGNUP_DIALOG_ID);
				if (token == null) {
					Toast.makeText(Geoloqi.this, "Error signing up", Toast.LENGTH_LONG).show();
				} else {
					Util.setToken(token, Geoloqi.this);
					Log.d(Geoloqi.TAG, "Got access token: " + token.toString());
					Toast.makeText(Geoloqi.this, "Success! Check your email!", Toast.LENGTH_LONG).show();
					username = null;
					Util.setUsername(null, Geoloqi.this);
					new LogIn().execute();
				}
			}
		});

		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Geoloqi.this.removeDialog(SIGNUP_DIALOG_ID);
			}
		});

		return builder.create();
	}

	// END GUI BUILDERS

	private class LogIn extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... v) {
			String username = Util.getUsername(Geoloqi.this);
			if (username == null || username.equals("") || username.equals("(anonymous)")) {
				OAuthToken token = Util.getToken(Geoloqi.this);
				if (token == null) {
					username = "(anonymous)";
				} else {
					try {
						username = RPCBinder.singleton().getUsername(token);
					} catch (RemoteException e) {
						Util.log("RPC getUsername failed.  Logging in as anonymous.");
						username = "(anonymous)";
					}
				}
			}
			Util.setUsername(username, Geoloqi.this);
			return username;
		}

		// Runs with the return value of doInBackground, has access to the UI
		// thread
		@Override
		protected void onPostExecute(String newUsername) {
			username = newUsername;
			if (username == null || username.equals("") || username.equals("(anonymous)")) {
				accountLabel.setText("(not logged in)");
				buttonShare.setVisibility(View.GONE);
				buttonLayerCatalog.setVisibility(View.GONE);
				buttonSignup.setVisibility(View.VISIBLE);
			} else {
				accountLabel.setText(username);
				buttonShare.setVisibility(View.VISIBLE);
				buttonLayerCatalog.setVisibility(View.VISIBLE);
				buttonSignup.setVisibility(View.GONE);
			}
		}
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
				CharSequence contentTitle = "Geoloqi";
				CharSequence contentText = "speed: " + spdLabel.getText() + ", " + unsentPointCount + " points";
				Intent notificationIntent = new Intent(Geoloqi.this, Geoloqi.class);
				PendingIntent contentIntent = PendingIntent.getActivity(Geoloqi.this, 0, notificationIntent, 0);
				notification.flags = Notification.FLAG_ONGOING_EVENT;
				notification.setLatestEventInfo(Geoloqi.this, contentTitle, contentText, contentIntent);// FIXME This is deprecated.
				((NotificationManager) Geoloqi.this.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
				// lastUnsentPointCountDebug = unsentPointCount;
			}

			// boolean loggedIn = username != null || username != "(anonymous)";
			// boolean tracking = Util.isServiceRunning(Geoloqi.this,GeoloqiService.class.getName());
			//
			// Util.logMainInterface(lastLocation, lastUnsentPointCountDebug,
			// loggedIn, tracking);

			if (username == null || username.equals("(anonymous)")) {
				accountLabel.setText("(not logged in)");
				buttonLayerCatalog.setVisibility(View.GONE);
				buttonSignup.setVisibility(View.VISIBLE);
			} else {
				accountLabel.setText(username);
				buttonLayerCatalog.setVisibility(View.VISIBLE);
				buttonSignup.setVisibility(View.GONE);
			}

		}
	}
}
