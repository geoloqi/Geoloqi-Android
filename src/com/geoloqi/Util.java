package com.geoloqi;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geoloqi.messaging.LQToken;
import com.geoloqi.ui.Geoloqi;

public abstract class Util {
	
	private static float DISTANCE_FILTER = 0.0f;
	private static long TRACKING_LIMIT = 1000l;
	private static String VERSION = "11.132";

	public static final int NOTIFICATION_ID = 1024;

	public static Uri encodeLocation(Location l){
		String path = "/" +
			l.getLatitude() + "/" +
			l.getLongitude() + "/" + 
			l.getAltitude() + "/" +
			l.getBearing() + "/" +
			l.getSpeed() + "/" +
			l.getTime() + "/" +
			l.getAccuracy() + "/";
		
		// (latitude,longitude,altitude,bearing,speed,time,accuracy)
		Uri uri = (new Uri.Builder()).scheme("geo").authority("geoloqi.com").path(path).build();
		decodeLocation(uri);
		return uri;
	}

	public static Location decodeLocation(Uri uri) {
		String[] data = uri.getPath().split("/");
		Location l = new Location("Geoloqi");
		l.setLatitude(Float.parseFloat(data[1]));
		l.setLongitude(Float.parseFloat(data[2]));
		l.setAltitude(Float.parseFloat(data[3]));
		l.setBearing(Float.parseFloat(data[4]));
		l.setSpeed(Float.parseFloat(data[5]));
		l.setTime(Long.parseLong(data[6]));
		l.setAccuracy(Float.parseFloat(data[7]));
		return l;
	}

	public static int getMinTime(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		String minTime = p.getString(GeoloqiPreferences.PREF_MINTIME_KEY, "5");
		return Integer.parseInt(minTime) * 1000;
	}

	public static int getRateLimit(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		String rateLimit = p.getString(GeoloqiPreferences.PREF_RATELIMIT_KEY, "120");
		return Integer.parseInt(rateLimit);
	}

	public static LQToken getToken(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
	
		if(p.getString(GeoloqiPreferences.PREF_ACCESS_TOKEN, null) == null)
			return null;
	
		return new LQToken(
			p.getString(GeoloqiPreferences.PREF_ACCESS_TOKEN, null),
			p.getString(GeoloqiPreferences.PREF_REFRESH_TOKEN, null),
			p.getLong(GeoloqiPreferences.PREF_EXPIRES_AT, 0),
			p.getString(GeoloqiPreferences.PREF_SCOPE, null)
		);
	}

	public static String getUsername(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		return p.getString(GeoloqiPreferences.PREF_USERNAME, null);
	}

	public static void removeToken(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = p.edit();
		e.putString(GeoloqiPreferences.PREF_ACCESS_TOKEN, null);
		e.putString(GeoloqiPreferences.PREF_REFRESH_TOKEN, null);
		e.putString(GeoloqiPreferences.PREF_EXPIRES_AT, null);
		e.putString(GeoloqiPreferences.PREF_SCOPE, null);
		e.commit();
	}

	public static void setToken(LQToken token, Context context) {
		if(token == null)
			return;
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = p.edit();
		e.putString(GeoloqiPreferences.PREF_ACCESS_TOKEN, token.accessToken);
		e.putString(GeoloqiPreferences.PREF_REFRESH_TOKEN, token.refreshToken);
		e.putLong(GeoloqiPreferences.PREF_EXPIRES_AT, token.expiresAt.getTime() / 1000l);
		e.putString(GeoloqiPreferences.PREF_SCOPE, token.scope);
		e.commit();
		Log.d(Geoloqi.TAG, "Stored token in shared preferences");
	}

	public static void setUsername(String username, Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		Editor e = p.edit();
		e.putString(GeoloqiPreferences.PREF_USERNAME, username);
		e.commit();
	}
	
	public static float getDistanceFilter() {
		return DISTANCE_FILTER;
	}
	
	public static long getTrackingLimit() {
		return TRACKING_LIMIT;
	}
	
	public static String getVersion() {
		return VERSION;
	}
	
	public static void log(String message){
		Log.d("Brian.Peter.Ledger",message);
	}
	
	public static boolean isServiceRunning(Context context, String serviceName) {
		final ActivityManager activityManager = (ActivityManager) context.getSystemService(Geoloqi.ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
		for(RunningServiceInfo serviceInfo: services) {
			if(serviceInfo.service.getClassName().equals(serviceName)){
				return true;
			}
		}
		log("Service \'"+serviceName+"\' is not running.");
		return false;
	}
	
	public static void logo() {
		log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++#@@@@@@@@@++++++++++++++++++++++++++++++++++++++++@@@@@#++++++++++");
		log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@@@@@@@@@@+++++++++++++++++++++++++++++++++++++#@@@@@@@@+++++++++");
		log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@;,,,,,@@@+++++++++++++++++++++++++++++++++++++@@@@'+@@@@++++++++");
		log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@,,,,,,@@@++++++++++++++++++++++++++++++++++++@@@,,,,,@@@++++++++");
		log("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++#@@,,,,,,@@#++++++++++++++++++++++++++++++++++++@@#,,,,,,@@@+++++++");
		log("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@@,,,,,,@@#++++++++++++++++++++++++++++++++++++@@:,,,,,,@@@+++++++");
		log("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@@,,,,,,@@+++++++++++++++++++++++++++++++++++++@@:,,,,,,@@@+++++++");
		log("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@@,,,,,'@@+++++++++++++++++++++++++++++++++++++@@@,,,,,,@@#+++++++");
		log("++++++++++++++#@@@@@@@@+#@@@@#+++++#@@@@@@@@@+++++++++@@@@@@@@@#+++@@@,,,,,+@@++++@@@@@@@@@+++++++++@@@@@@@@##@@@@@@@@+,,,,@@@++++++++");
		log("+++++++++++++@@@@@@@@@@@@@@@@@@++@@@@@@@@@@@@@@+++++@@@@@@@@@@@@@++@@+,,,,,@@@+#@@@@@@@@@@@@@+++++@@@@@@@@@@@@@@@@@@@@@@@@@@@@++++++++");
		log("+++++++++++#@@@@@;,,'@@@@@@@@@@+@@@@@@;,,,+@@@@@++#@@@@@+,,,;@@@@@#@@;,,,,,@@@@@@@@@':::'@@@@@+++@@@@@'::'@@@@@@@@@@@@@@@@@@@#++++++++");
		log("+++++++++++@@@'........@@...,@@@@@@..........#@@@#@@@'.........+@@@@@,,,,,,@@@@@@',,,,,,,,,#@@@+@@@@,,,,,,,,;@,,,,@@@,,,,,,@@#++++++++");
		log("++++++++++@@@,..............;@@@@@............+@@@@@............:@@@@......@@@@@............+@@#@@@...............@@@......@@#++++++++");
		log("++++++++++@@+...............@@@@@..............@@@@..............@@@@......@@@@..............@@@@@................@@@.....:@@+++++++++");
		log("+++++++++@@@................@@@@...............@@@@..............,@@@......@@@+..............'@@@:...............:@@@.....'@@+++++++++");
		log("+++++++++@@#``````@@@@``````@@@@`````:@@@.`````@@@``````'@@#``````@@@.....;@@@......#@@;......@@@......'@@@,.....'@@@.....#@@+++++++++");
		log("+++++++++@@``````@@@@@``````@@@``````@@@@;`````@@@``````@@@@``````@@@.....+@@@.....:@@@@......@@@.....:@@@@#.....@@@+.....@@@+++++++++");
		log("++++++++@@@``````@@@@@``````@@@`````,@@@@``````@@+`````@@@@@``````@@+.....@@@;.....@@@@@......@@:.....@@@@@;.....@@@;.....@@@+++++++++");
		log("++++++++@@@`````;@@@@@``````@@@```````````````@@@.`````@@@@@``````@@;.....@@@......@@@@@......@@......@@@@@,.....@@@......@@@+++++++++");
		log("++++++++@@@`````@@@@@@`````,@@@``````````````:@@@``````@@@@@``````@@......@@@......@@@@@......@@......@@#@@......@@@......@@@+++++++++");
		log("++++++++@@+`````@@@@@@`````;@@+`````````````@@@@@``````@@@@@``````@@......@@@......@@@@@.....,@@.....,@@#@@......@@@......@@#+++++++++");
		log("++++++++@@'`````@@@@@@`````@@@;`````,,,,;#@@@@@@@``````@@@@@`````;@@......@@@......@@@@@.....#@@.....,@@@@@......@@@......@@++++++++++");
		log("++++++++@@;`````@@@@@'`````@@@;`````@@@@@@@@@@@@@``````@@@@:`````@@@``````@@@``````@@@@.`````@@@``````@@@@@`````,@@@`````;@@@+++++++++");
		log("++++++++@@+``````@@@@,`````@@@#`````@@@@@@@@@'@@@``````@@@@``````@@@`````.@@@``````@@@@``````@@@``````@@@@@`````;@@@`````'@@@+++++++++");
		log("++++++++@@@````````````````@@@@```````,;,````@@@@```````,.``````:@@@```````@@```````,.``````+@@@````````````````@@@@```````@@+++++++++");
		log("++++++++@@@````````````````@@@@``````````````@@@@#``````````````@@@@```````@@;``````````````@@@@````````````````@@@@``````,@@+++++++++");
		log("++++++++#@@.```````````````@@@@@`````````````@@@@@`````````````@@@@@``````,@@@`````````````@@@@@@```````````````@@@@``````'@@+++++++++");
		log("+++++++++@@@``````````````.@@@@@#````````````@@@@@@```````````@@@@@@@`````;@@@@```````````@@@@@@@:``````````````@@@@#`````@@@+++++++++");
		log("+++++++++#@@@;`````:@`````;@@+@@@@;```````:@@@@#@@@@#```````#@@@@+@@@@;```@@@@@@+``````.@@@@@++@@@#``````@``````@@@@@@:```@@@+++++++++");
		log("++++++++++@@@@@@@@@@@`````+@@++@@@@@@@@@@@@@@@@++@@@@@@@@@@@@@@@+++@@@@@@@@@@@@@@@@@@@@@@@@@++++@@@@@@@@@@``````@@#@@@@@@@@@@+++++++++");
		log("++++++++@@@@@@@@@@@@'`````@@@+++#@@@@@@@@@@@@#+++++@@@@@@@@@@@#+++++#@@@@@@@++#@@@@@@@@@@@+++++++@@@@@@@@@`````.@@++@@@@@@@@++++++++++");
		log("++++++++@@@@@@@@@@@@``````@@@++++++#@@@@##++++++++++++@@@@#++++++++++++###+++++++#@@@@#++++++++++++#@@@@@@     ;@@+++++###++++++++++++");
		log("++++++++@@`.@@@@@@@.``````@@#++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@@     #@@++++++++++++++++++++");
		log("+++++++#@@```````````````@@@+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@'     @@@++++++++++++++++++++");
		log("+++++++@@@``````````````.@@@+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@.     @@@++++++++++++++++++++");
		log("+++++++@@@``````````````@@@++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@      @@@++++++++++++++++++++");
		log("+++++++@@@````````````+@@@#+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++#@@      @@@++++++++++++++++++++");
		log("+++++++@@@@@;,`````;@@@@@#++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++#@@@@@@@@@@#++++++++++++++++++++");
		log("++++++++@@@@@@@@@@@@@@@@+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@@@@@@@@@+++++++++++++++++++++");
		log("+++++++++#@@@@@@@@@@@@#+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@@@@@@@++++++++++++++++++++++");
		log("+++++++++++++++###++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}
