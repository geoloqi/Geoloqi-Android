package com.geoloqi.messaging;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.net.Uri;
import android.util.Pair;

import com.geoloqi.BatteryReceiver;
import com.geoloqi.GeoloqiReceiver;
import com.geoloqi.Util;

public class GeoloqiMessenger extends SQLiteOpenHelper implements Runnable {

	public boolean running;
	private static GeoloqiMessenger messenger = null;
	
	Semaphore queueLock = new Semaphore(1);
	LinkedList<Pair<Location, Integer>> backlog = new LinkedList<Pair<Location, Integer>>();

	long lastSend = 0l;
	private final Semaphore rezendezvous = new Semaphore(0,true);
	protected LocationListElement first;
	
	protected Context context;
	
	private final MessagingReceiver receiver;
	
	protected GeoloqiMessenger(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//Rebuild the list of unsent locations persisting from a previous session.
		first = LocationListElement.initializeDatabase(this.getReadableDatabase());
		if(first!=null){
			rezendezvous.release();
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
		try {
			receiver.finalize();
		} catch (Throwable e) {
			Util.log("Error in GeoloqiMessenger.stop(): " + e.getMessage());
		}	
	}
	
	private void getBatch() {
		queueLock.acquireUninterruptibly();
		for(Pair<Location, Integer> pair: backlog){
			enqueueLocation(context, pair.first, pair.second);
		}
		backlog.clear();
		queueLock.release();
	}
	
	private final int maximumMessageSize = 100;
	
	private void sendData() {
		Util.log("Sending Data.");
		GeoloqiHTTPRequest post = GeoloqiHTTPRequest.singleton();
		// firstUnsent is not null or the queue is empty
		while(first!=null){
			LocationListElement next = first;

			//Build the message.
			String message;
			try{
				String debug = "Message:\n";
				JSONArray json = new JSONArray();
				for (int i = 0; i < maximumMessageSize && next != null; i++) {
					json.put(next.toJSON());
					debug += Util.encodeLocation(next.getLocation())+"\n";
					next = next.next;
				}
				message = json.toString();
				Util.log(debug);
			}catch(JSONException e){
				Util.log(e.getMessage());
				throw new RuntimeException("GeoloqiMessenger failed with a JSON Exception.");
			}
			
			//Send the message.
			boolean success = true;
			do{
				if(success==false){
					Util.log("Send failed.");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) { }
				}
				Util.log("Sending");
				//success = post.locationUpdate(context,message);
				success = Math.random()>.5;//FIXME debug
			}while(!success);
			Util.log("Send succeeded.");
			// Delete the sent points.
			while(first!=next){
				Util.log("Deleting: " + first.elementID);
				first.delete();
				first = first.next;
			}
		}
		broadcastUnsentPointCount();
	}
	
	private void enqueueLocation(Context context, Location location, int batteryLevel) {
		LocationListElement next, last;
		next = new LocationListElement(context, location,batteryLevel);
		
		if (first == null) {
			first = next;
		} else {
			last = first;
			while(last.next!=null){
				last = last.next;
			}
			last.setNext(next);
		}
	}
	
	private void broadcastUnsentPointCount() {
		Uri uri = new Uri.Builder().scheme("mupdate").authority("geoloqi.com").path("/"+getUnsentPointCount()+"/").build();
		Intent updateUnsentPointCount = new Intent(Intent.ACTION_EDIT,uri);
		context.sendBroadcast(updateUnsentPointCount);
	}
	
	private int getUnsentPointCount() {
		queueLock.acquireUninterruptibly();
		int count = backlog.size();
		Util.log("Backlog size is "+count+" points.");
		LocationListElement l = first;
		while(l!=null){
			count++;
			l = l.next;
		}
		queueLock.release();
		Util.log("updateUnsentPointCount() returning " + count + " points");
		return count;
	}
	
private class MessagingReceiver extends GeoloqiReceiver {
		
		BatteryReceiver battery;

		public MessagingReceiver(Context context) {
			super(context);
			battery = new BatteryReceiver(context);
			
		}

		@Override
		public void onReceive(Context context, Location location) {
			Util.log("Received a broadcast.");
			queueLock.acquireUninterruptibly();
			backlog.add(new Pair<Location, Integer>(location, battery.getBatteryLevel()));
			queueLock.release();
			broadcastUnsentPointCount();
			if (shouldSendData(context) && rezendezvous.availablePermits()==0) {
				rezendezvous.release();
			}
		}

		private boolean shouldSendData(Context context) {
			return lastSend < System.currentTimeMillis() - Util.getMinTime(context);
		}
		
		@Override
		public void finalize() throws Throwable{
			try{
				battery.finalize();
			}finally{
				super.finalize();
			}
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
