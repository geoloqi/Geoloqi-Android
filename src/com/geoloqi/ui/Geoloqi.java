package com.geoloqi.ui;

import java.text.DecimalFormat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.geoloqi.GeoloqiPreferences;
import com.geoloqi.GeoloqiReceiver;
import com.geoloqi.Util;
import com.geoloqi.android1.R;
import com.geoloqi.messaging.GeoloqiHTTPRequest;
import com.geoloqi.messaging.LQToken;
import com.geoloqi.service.GeoloqiService;

public class Geoloqi extends Activity implements OnClickListener {

	// Broadcast Receiver for listening to the Geoloqi Service
	private static UIUpdateReceiver receiver;

	public static final String TAG = "Geoloqi";
	private static final int LOGIN_DIALOG_ID = 1;
	private static final int SIGNUP_DIALOG_ID = 2;
	private Button buttonStart, buttonLayerCatalog, buttonSignup;
	@SuppressWarnings("unused")//FIXME
	private TextView latLabel, lngLabel, numPointsLabel, altLabel, spdLabel,
			accLabel, accountLabel, textNotLoggedIn;
	private String username;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Util.logo();
		super.onCreate(savedInstanceState);
		initializeGUI();
		
		receiver = new UIUpdateReceiver(this);
		
		if (!Util.isServiceRunning(this,GeoloqiService.class.getName())) {
			buttonStart.setText("Stop Tracking");
			ComponentName response = startService(new Intent(this, GeoloqiService.class));
			if(response == null) {
				Util.log("Geoloqi could not start GeoloqiService");
				throw new RuntimeException("Geoloqi could not start GeoloqiService");
			}
		} else {
			buttonStart.setText("Start Tracking");
		}

		ImageView image = (ImageView) findViewById(R.id.geoloqiLogo);
		image.setImageResource(R.drawable.geoloqi_300x100);
		new LQGetUsername().execute();
	}
	
	private void initializeGUI() {
		setContentView(R.layout.main);
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
		textNotLoggedIn = (TextView) findViewById(R.id.textNotLoggedIn);
		textNotLoggedIn.setVisibility(View.INVISIBLE);
		buttonStart.setOnClickListener(this);
		buttonSignup.setOnClickListener(this);
		buttonLayerCatalog.setOnClickListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
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
			if (!Util.isServiceRunning(this.getApplicationContext(),GeoloqiService.class.getName())) {
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
							Toast.makeText(Geoloqi.this, "Error logging in",Toast.LENGTH_LONG).show();
						} else {
							Util.setToken(token, Geoloqi.this);
							Log.d(Geoloqi.TAG,
									"Got access token: " + token.toString());
							Toast.makeText(Geoloqi.this, "Logged in!",
									Toast.LENGTH_LONG).show();
							username = null;
							Util.setUsername(null, Geoloqi.this);
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
							Toast.makeText(Geoloqi.this, "Error signing up",
									Toast.LENGTH_LONG).show();
						} else {
							Util.setToken(token, Geoloqi.this);
							Log.d(Geoloqi.TAG,
									"Got access token: " + token.toString());
							Toast.makeText(Geoloqi.this,
									"Success! Check your email!",
									Toast.LENGTH_LONG).show();
							username = null;
							Util.setUsername(null, Geoloqi.this);
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



	class LQGetUsername extends AsyncTask<Void, Void, String> {
		// Doesn't have access to the UI thread
		@Override
		protected String doInBackground(Void... v) {
			// Attempt to retrieve the username from the preferences
			String storedUsername = Util.getUsername(Geoloqi.this);
			// If it's not there, then make a server call to get the username
			if (storedUsername == null || storedUsername.equals("")
					|| storedUsername.equals("(anonymous)")) {
				storedUsername = GeoloqiHTTPRequest.singleton()
						.accountUsername(Geoloqi.this);
				Util.setUsername(storedUsername, Geoloqi.this);
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
	
	private class UIUpdateReceiver extends GeoloqiReceiver {

		UIUpdateReceiver(Context context) {
			super(context);
		}

		@Override
		public void onReceive(Context context, Location location) {
			runOnUiThread(new UpdateUI(location));
		}
		
		private class UpdateUI implements Runnable {

			private final Location location;

			UpdateUI(Location location) {
				this.location = location;
			}

			public void run() {
				Util.log("Updating UI!");
				latLabel.setText((new DecimalFormat("#.00000").format(location.getLatitude())));
				lngLabel.setText((new DecimalFormat("#.00000").format(location.getLongitude())));
				altLabel.setText("" + location.getAltitude() + "m");
				spdLabel.setText("" + location.getSpeed() + " km/h");
				accLabel.setText("" + location.getAccuracy() + "m");
				//numPointsLabel.setText("" + db.numberOfUnsentPoints());FIXME
				
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
}