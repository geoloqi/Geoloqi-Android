package com.geoloqi.messaging;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Pair;

import com.geoloqi.BatteryReceiver;
import com.geoloqi.GeoloqiReceiver;
import com.geoloqi.Util;
import com.geoloqi.android1.R;
import com.geoloqi.ui.Geoloqi;

public class GeoloqiMessenger extends SQLiteOpenHelper implements Runnable {

	public boolean running;
	private static GeoloqiMessenger messenger = null;
	
	Semaphore queueLock = new Semaphore(1);
	LinkedList<Pair<Location, Integer>> backlog = new LinkedList<Pair<Location, Integer>>();

	long lastSend = 0l;
	Semaphore rezendezvous = new Semaphore(0,true);
	
	// last is null if and only if firstSent and firstUnsent are also null.
	// If firstSent is not null, then firstSent is first.
	// If firstSent is null and firstUnsent is not null, then firstUnsent is
	// first.
	// If last is null, then the list is empty.
	protected LocationListElement firstSent;
	protected LocationListElement firstUnsent;
	protected LocationListElement last;
	
	protected Context context;
	
	private static int unsentPointCount = 0;
	
	@SuppressWarnings("unused")
	private final MessagingReceiver receiver;
	
	protected GeoloqiMessenger(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		LocationListElement old = LocationListElement.initializeDatabase(this.getReadableDatabase());
		if(old != null) {
			firstUnsent = old;
			last = old;
		}
		this.context = context;
		receiver = new MessagingReceiver(context);

	}
	
	public static GeoloqiMessenger singleton(Context context) {
		if(messenger==null) {
			messenger = new GeoloqiMessenger(context);
		}
		return messenger;
	}
	
	public void run() {
		Util.log("Messenger is online.");
		running = true;
		while(running) {
			rezendezvous.acquireUninterruptibly();
			getBatch();
			sendData();
		}
		Util.log("Messenger thread terminating.");
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public void stop() {
		Util.log("Messenger is going down.");
		running = false;
	}
	
	public static int getUnsentPointCount(){
		return unsentPointCount;
	}
	
	private void getBatch() {
		queueLock.acquireUninterruptibly();
		for(Pair<Location, Integer> pair: backlog){
			enqueueLocation(context, pair.first, pair.second);
		}
		queueLock.release();
	}
	
	private void sendData() {
		Util.log("Sending Data.");
		GeoloqiHTTPRequest post = GeoloqiHTTPRequest.singleton();
		boolean success = false;
		while(firstUnsent!=null){
			try {
				success = post.locationUpdate(context, makeJSON());
			} catch (JSONException e) {
				success = false;
			}
			if (success) {
				firstSent = null;
			} else {
				firstUnsent = firstSent;
			}
		}
		last = null;
	}
	
	private String makeJSON() throws JSONException {
		JSONArray json = new JSONArray();
		for (int i = 0; i < 100 && firstUnsent != null; i++) {
			json.put(firstUnsent.toJSON());
		}
		return json.toString();
	}
	
	private void enqueueLocation(Context context, Location location, int batteryLevel) {
		LocationListElement next = new LocationListElement(context, location,batteryLevel);
		if (last == null) {
			firstUnsent = next;
			last = next;
		} else {
			last.setNext(next);
			if (firstUnsent == null) {
				firstUnsent = next;
			}
		}
	}
	
private class MessagingReceiver extends GeoloqiReceiver {
		
		private static final int NOTIFICATION_ID = 1024;
		private final Notification notification;
		BatteryReceiver battery;

		public MessagingReceiver(Context context) {
			super(context);
			battery = new BatteryReceiver(context);
			
			// Instantiate the notification
			CharSequence tickerText = "Geoloqi tracker is running";
			notification = new Notification(R.drawable.ic_stat_notify, tickerText, System.currentTimeMillis());
		}

		@Override
		public void onReceive(Context context, Location location) {
			Util.log("Received a broadcast.");
			backlog.add(new Pair<Location, Integer>(location, battery.getBatteryLevel()));
			updateUnsentPointCount();
			updateNotification(location);
			if (shouldSendData(context) && rezendezvous.availablePermits()==0) {
				rezendezvous.release();
			}
		}
		
		private void updateUnsentPointCount() {
			LocationListElement l;
			if(firstSent!=null) {
				l = firstSent;
			}else if(firstUnsent!=null) {
				l = firstUnsent;
			}else{
				unsentPointCount = 0;
				return;
			}
			int count;
			for(count=1;l!=null;count++){
				l=l.next;
			}
			unsentPointCount = count + backlog.size();
		}
		
		private void updateNotification(Location location) {
			CharSequence contentTitle = "Geoloqi";
			CharSequence contentText = "" + new DecimalFormat("#.0").format(location.getSpeed() * 3.6) + " km/h, " + unsentPointCount + " points";
			Intent notificationIntent = new Intent(context, Geoloqi.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			notification.flags = Notification.FLAG_ONGOING_EVENT;
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);//FIXME This is deprecated.
			((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
		}

		private boolean shouldSendData(Context context) {
			return lastSend < System.currentTimeMillis() - Util.getMinTime(context);
		}
	}

//DATABASE STUFF

private static final String DATABASE_NAME = "geoloqi.db";
private static final int DATABASE_VERSION = 2;

@Override
public void onCreate(SQLiteDatabase db) {
	try{
		db.execSQL("CREATE TABLE lqLocationData (" +
				LocationListElement.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				LocationListElement.SENT + " INTEGER DEFAULT 0," +
				LocationListElement.DATE + " INTEGER," +
				LocationListElement.LATITUDE + " REAL," +
				LocationListElement.LONGITUDE + " REAL," +
				LocationListElement.ALTITUDE + " INTEGER," +
				LocationListElement.SPEED + " INTEGER," +
				LocationListElement.HEADING + " INTEGER," +
				LocationListElement.HORIZONTAL_ACCURACY + " INTEGER," +
				LocationListElement.SOURCE + " TEXT," +
				LocationListElement.DISTANCE_FILTER + " INTEGER," +
				LocationListElement.TRACKING_LIMIT + " INTEGER," +
				LocationListElement.RATE_LIMIT + " INTEGER," +
				LocationListElement.BATTERY + " INTEGER);");
	}catch(RuntimeException e) { }
}

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	onCreate(db);
	if(oldVersion == 1 && newVersion == 2) {
		db.execSQL("ALTER TABLE lqLocationData ADD COLUMN sent INTEGER DEFAULT 0");
	}
}

@Override
public void onOpen(SQLiteDatabase db) {
	onCreate(db);
}

}
