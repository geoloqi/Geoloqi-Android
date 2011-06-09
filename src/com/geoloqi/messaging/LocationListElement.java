package com.geoloqi.messaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;

import com.geoloqi.Util;

class LocationListElement {

	protected static final String DATABASE_NAME = "geoloqi.db";
	protected static final String TABLE_NAME = "lqLocationData";

	protected static final String ID = "id";
	protected static final String SENT = "sent";
	protected static final String DATE = "date";
	protected static final String LATITUDE = "latitude";
	protected static final String LONGITUDE = "longitude";
	protected static final String ALTITUDE = "altitude";
	protected static final String SPEED = "speed";
	protected static final String HEADING = "heading";
	protected static final String HORIZONTAL_ACCURACY = "horizontal_accuracy";
	protected static final String SOURCE = "source";
	protected static final String DISTANCE_FILTER = "distance_filter";
	protected static final String TRACKING_LIMIT = "tracking_limit";
	protected static final String RATE_LIMIT = "rate_limit";
	protected static final String BATTERY = "battery";

	private static SQLiteDatabase db;

	private static Semaphore statementLock = new Semaphore(1);
	private static SQLiteStatement delete;

	long elementID;
	LocationListElement next = null;

	private Cursor get() {
		Cursor cursor = db.query(TABLE_NAME, null, "ID="+elementID, null, null, null, null, "1");
		if (cursor.getCount() != 1) {
			throw new RuntimeException("Location with id " + elementID + " does not exist in the database.");
		}
		cursor.moveToFirst();
		return cursor;
	}

	/**
	 * Constructor for a locations not yet in the Android SQLite Database.
	 * 
	 * @param location
	 *            location to be inserted into the database
	 * @param batteryLife
	 *            battery life recorded at the time the location was fixed
	 */
	public LocationListElement(Context context, Location location, int batteryLife) {
		Util.log("Creating new list element.");
		float distanceFilter = Util.getDistanceFilter();
		long trackingLimit = Util.getTrackingLimit();
		int rateLimit = Util.getRateLimit(context);
		ContentValues values = new ContentValues();
		values.put(DATE, (int) (System.currentTimeMillis() / 1000));
		values.put(LATITUDE, location.getLatitude());
		values.put(LONGITUDE, location.getLongitude());
		values.put(SPEED, (int) location.getSpeed() * 3.6);
		values.put(ALTITUDE, (int) location.getAltitude());
		values.put(HEADING, (int) location.getBearing());
		values.put(HORIZONTAL_ACCURACY, location.getAccuracy());
		values.put(SOURCE, location.getProvider());
		values.put(DISTANCE_FILTER, distanceFilter);
		values.put(TRACKING_LIMIT, trackingLimit);
		values.put(RATE_LIMIT, rateLimit);
		values.put(BATTERY, batteryLife);
		elementID = db.insertOrThrow("lqLocationData", null, values);
	}

	/**
	 * Constructor for a location in the Android SQLite Database.
	 * 
	 * @param id
	 *            unique row ID of the location
	 * @param next
	 *            LocationListElement representing the next location
	 */
	public LocationListElement(long id, LocationListElement next) {
		this.elementID = id;
		this.next = next;
	}

	/**
	 * Convenience constructor for a location in the Android SQLite Database
	 * whose next element is unknown.
	 * 
	 * @param id
	 *            unique row ID of the location.
	 */
	public LocationListElement(long id) {
		this(id, null);
	}

	/**
	 * @return the location represented by this list element.
	 */
	public Location getLocation() {
		Cursor cursor = get();
		Location location = new Location("Geoloqi");
		location.setLatitude(cursor.getDouble(cursor.getColumnIndex(LATITUDE)));
		location.setLongitude(cursor.getDouble(cursor.getColumnIndex(LONGITUDE)));
		location.setTime(cursor.getInt(cursor.getColumnIndex(DATE)));
		location.setAltitude(cursor.getInt(cursor.getColumnIndex(ALTITUDE)));
		location.setSpeed(cursor.getInt(cursor.getColumnIndex(SPEED)));
		location.setAccuracy(cursor.getInt(cursor.getColumnIndex(HORIZONTAL_ACCURACY)));
		return location;
	}

	/**
	 * @return the battery life stored with this location.
	 */
	public int getBattery() {
		Cursor cursor = get();
		return cursor.getInt(cursor.getColumnIndex(BATTERY));
	}

	/**
	 * @return true if there is a list element after this one.
	 */
	public boolean hasNext() {
		return next != null;
	}

	/**
	 * @return the next LocationListElement in the queue.
	 */
	public LocationListElement getNext() {
		return next;
	}

	/**
	 * Sets the next LocationListElement in the queue
	 * 
	 * @param next
	 *            LocationListElement you wish to set.
	 */
	public void setNext(LocationListElement next) {
		this.next = next;
	}

	/**
	 * Set the database underlying the LocationListElements.
	 * 
	 * @param db
	 *            SQLite database to back all LocationListElements.
	 */
	public static LocationListElement initializeDatabase(SQLiteDatabase db) {
		Util.log("Setting Database.");
		statementLock.acquireUninterruptibly();
		delete = db.compileStatement("DELETE * FROM "+TABLE_NAME+" WHERE ID=?");
		statementLock.release();
		
		if (LocationListElement.db != null) {
			throw new RuntimeException(
					"Cannot set the LocationListElement database twice.");
		}
		db.setLockingEnabled(true);
		LocationListElement.db = db;
		
		//Reclaim any unaccounted for locations.
		Cursor cursor = db.query(TABLE_NAME, new String[] {ID}, null, null, null, null, null, "1");
		if(!cursor.moveToFirst()){
			return null;
		}
		LocationListElement last, first = new LocationListElement(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
		last = first;
		while(cursor.moveToNext()){
			LocationListElement next = new LocationListElement(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
			last.next = next;
			last = next;
		}
		return first;
	}

	/**
	 * @return the JSON encoding of the location represented by this list
	 *         element.
	 */
	public JSONObject toJSON() throws JSONException {
		Cursor cursor = get();

		JSONObject point = new JSONObject();

		point.put("latitude", cursor.getDouble(cursor.getColumnIndexOrThrow(LATITUDE)));
		point.put("longitude", cursor.getDouble(cursor.getColumnIndexOrThrow(LONGITUDE)));
		point.put("speed", cursor.getInt(cursor.getColumnIndexOrThrow(SPEED)));
		point.put("altitude", cursor.getInt(cursor.getColumnIndexOrThrow(ALTITUDE)));
		point.put("horizontal_accuracy", cursor.getInt(cursor.getColumnIndexOrThrow(HORIZONTAL_ACCURACY)));
		
		JSONObject location = new JSONObject();
		JSONObject raw = new JSONObject();
		JSONObject client = new JSONObject();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",Locale.US);
		Date d = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(LQLocationData.DATE)) * 1000);

		location.put("type", "point");
		location.put("position", point);
		
		raw.put("battery", cursor.getInt(cursor.getColumnIndexOrThrow(LQLocationData.BATTERY)));
		raw.put("rate_limit", cursor.getInt(cursor.getColumnIndexOrThrow(LQLocationData.RATE_LIMIT)));
		
		client.put("name", "Geoloqi");
		client.put("version", Util.getVersion());
		client.put("platform", "Android");
		client.put("hardware", "unknown");

		JSONObject json = new JSONObject();
		
		json.put("date", sdf.format(d));
		json.put("location", location);
		json.put("raw", raw);
		json.put("client", client);

		return json;
	}

	@Override
	public void finalize() throws Throwable{
		try{
			statementLock.acquireUninterruptibly();
			delete.clearBindings();
			delete.bindLong(0, elementID);
			delete.execute();
			statementLock.release();
		}finally {
			super.finalize();
		}
	}

}
