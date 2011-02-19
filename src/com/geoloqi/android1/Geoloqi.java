package com.geoloqi.android1;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
	public static final String TAG = "Geoloqi";
	private static final int LOGIN_DIALOG_ID = 1;
	private Button buttonStart; // , buttonStop, buttonUpdate;
	private TextView latLabel, lngLabel, numPointsLabel, altLabel, spdLabel, accLabel, lastSentLabel, accountLabel;
	protected LQLocationData db;
	private Handler handler = new Handler();
	public Context context;
	private String username;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = this;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonStart = (Button) findViewById(R.id.buttonStart);
		latLabel = (TextView) findViewById(R.id.textLatitude);
		lngLabel = (TextView) findViewById(R.id.textLongitude);
		altLabel = (TextView) findViewById(R.id.textAltitude);
		spdLabel = (TextView) findViewById(R.id.textSpeed);
		accLabel = (TextView) findViewById(R.id.textAccuracy);
		numPointsLabel = (TextView) findViewById(R.id.textNumPointsInQueue);
		accountLabel = (TextView) findViewById(R.id.textAccount);
		// lastSentLabel = (TextView) findViewById(R.id.textLastSent);
		
		buttonStart.setOnClickListener(this);
		// buttonStop.setOnClickListener(this);
		// buttonUpdate.setOnClickListener(this);

		db = new LQLocationData(this);
		new Timer().schedule(new MyTimerTask(), 0, 1000);

		if(!isServiceRunning()) {
			buttonStart.setText("Stop Tracking");
			startService(new Intent(this, GeoloqiService.class));
		} else {
			buttonStart.setText("Start Tracking");
		}
		
		ImageView image = (ImageView)findViewById(R.id.geoloqiLogo);
		image.setImageResource(R.drawable.geoloqi_300x100);
		new LQGetUsername().execute();
	}

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
			if(!isServiceRunning()) {
				startService(new Intent(this, GeoloqiService.class));
			} else {
				stopService(new Intent(this, GeoloqiService.class));
			}
			break;
//		case R.id.buttonStop:
//			Log.d(TAG, "onClick: stopping service");
//			stopService(new Intent(this, GeoloqiService.class));
//			break;
//		case R.id.buttonUpdate:
//			Log.d(TAG, "onClick: update");
//			new LQUpdateUI().execute();
//			break;
		}
	}

	private AlertDialog buildLoginDialog() {
    	LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	final View layout = inflater.inflate(R.layout.login_dialog, (ViewGroup)findViewById(R.id.root));
    	
    	final EditText email = (EditText)layout.findViewById(R.id.editTextEmail);
    	final EditText pwd = (EditText)layout.findViewById(R.id.editTextPassword);
    	TextView account = (TextView)layout.findViewById(R.id.textAccount);

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setView(layout);

    	if(username != null) {
    		account.setText("Currently logged in as " + username);
    		builder.setTitle("Change Account");
    	} else {
    		builder.setTitle("Log In");
    	}
    	
    	builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				LQToken token = GeoloqiHTTPRequest.singleton().oauthToken(email.getText().toString(), pwd.getText().toString());
				Geoloqi.this.removeDialog(LOGIN_DIALOG_ID);
				if(token == null) {
					Toast.makeText(context, "Error logging in", Toast.LENGTH_LONG).show();
				} else {
					GeoloqiPreferences.setToken(token, Geoloqi.this);
					Log.d(Geoloqi.TAG, "Got access token: " + token.toString());
					Toast.makeText(context, "Logged in!", Toast.LENGTH_LONG).show();
					new LQGetUsername().execute();
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case LOGIN_DIALOG_ID:
				return buildLoginDialog();
			default:
				return null;
		}
	}
	
	public boolean isServiceRunning() {
        final ActivityManager activityManager = (ActivityManager)getSystemService(Geoloqi.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
	
	    boolean isServiceFound = false;
	
	    for (int i = 0; i < services.size(); i++) {
	        if("com.geoloqi.android1".equals(services.get(i).service.getPackageName())) {
	            if("com.geoloqi.android1.GeoloqiService".equals(services.get(i).service.getClassName())) {
	                isServiceFound = true;
	            }
	        }
	    }
	    return isServiceFound;
	}

	
	class LQGetUsername extends AsyncTask<Void, Void, String> {
		// Doesn't have access to the UI thread
		@Override
		protected String doInBackground(Void... v) {
			// Attempt to retrieve the username from the preferences
			String storedUsername = GeoloqiPreferences.getUsername(context);
			// If it's not there, then make a server call to get the username
			if(storedUsername == null) {
				storedUsername = GeoloqiHTTPRequest.singleton().accountUsername(context);
				GeoloqiPreferences.setUsername(storedUsername, context);
			}
			return storedUsername;
		}

		protected void onProgressUpdate() {

		}

		// Runs with the return value of doInBackground, has access to the UI thread
		@Override
		protected void onPostExecute(String newUsername) {
			username = newUsername;
		}		
	}

	class LQUpdateUI extends AsyncTask<Void, Void, LQPoint> {

		// Doesn't have access to the UI thread
		@Override
		protected LQPoint doInBackground(Void... v) {
			return db.getLastLocation();
		}

		protected void onProgressUpdate() {

		}

		// Runs with the return value of doInBackground, has access to the UI thread
		@Override
		protected void onPostExecute(LQPoint point) {
			if(point == null)
				return;

			latLabel.setText((new DecimalFormat("#.00000").format(point.latitude)));
			lngLabel.setText((new DecimalFormat("#.00000").format(point.longitude)));
			altLabel.setText(""+point.altitude + "m");
			spdLabel.setText(""+point.speed + " km/h");
			accLabel.setText(""+point.horizontalAccuracy + "m");
			numPointsLabel.setText(""+db.numberOfUnsentPoints());

			if(username == null || username == "")
				accountLabel.setText("");
			else
				accountLabel.setText(username);

			// TODO: Talk to the service to find out the date the last point was sent
//			Date lastSent = ???
//			if(lastSent == null)
//				return;
//		
//			lastSentLabel.setText(""+((System.currentTimeMillis()/1000) - lastSent.getTime()) + " seconds");
		}		
	}

	public class MyTimerTask extends TimerTask {
		private Runnable runnable = new Runnable() {
			public void run() {
				new LQUpdateUI().execute();
				if(isServiceRunning()) {
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
}