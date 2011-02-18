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

public class Geoloqi extends Activity implements OnClickListener {
	public static final String TAG = "Geoloqi";
	private static final int LOGIN_DIALOG_ID = 1;
	private Button buttonStart; // , buttonStop, buttonUpdate;
	private TextView latLabel, lngLabel, numPointsLabel, altLabel, spdLabel, accLabel, lastSentLabel;
	protected LQLocationData db;
	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonStart = (Button) findViewById(R.id.buttonStart);
		latLabel = (TextView) findViewById(R.id.textLatitude);
		lngLabel = (TextView) findViewById(R.id.textLongitude);
		altLabel = (TextView) findViewById(R.id.textAltitude);
		spdLabel = (TextView) findViewById(R.id.textSpeed);
		accLabel = (TextView) findViewById(R.id.textAccuracy);
		numPointsLabel = (TextView) findViewById(R.id.textNumPointsInQueue);
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
	}

	public void onResume() {
		Log.i(TAG, "Resuming...");
		super.onResume();
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
				Log.d(TAG, "onClick: starting service");
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

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setView(layout);
    	builder.setTitle("Log In");
    	
    	builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				LQToken token = GeoloqiHTTPRequest.singleton().oauthToken(email.getText().toString(), pwd.getText().toString());
				GeoloqiPreferences.setToken(token, Geoloqi.this);
				Log.d(Geoloqi.TAG, token.toString());
				
				Geoloqi.this.removeDialog(LOGIN_DIALOG_ID);
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
	        //Log.d(Global.TAG, "Service Nr. " + i + " :" + services.get(i).service);
	        //Log.d(Global.TAG, "Service Nr. " + i + " package name : " + services.get(i).service.getPackageName());
	        //Log.d(Geoloqi.TAG, "Service Nr. " + i + " class name : " + services.get(i).service.getClassName());
	
	        if("com.geoloqi.android1".equals(services.get(i).service.getPackageName())) {
	            //Log.d(Geoloqi.TAG, "packagename matches");
	            // Log.d(LOG_TAG, "SpotService" + " : " +
	            // services.get(i).service.getClassName());
	
	            if("com.geoloqi.android1.GeoloqiService".equals(services.get(i).service.getClassName())) {
	                // Log.d(Geoloqi.TAG, "getClassName matches");
	                isServiceFound = true;
	            }
	        }
	    }
	    return isServiceFound;
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