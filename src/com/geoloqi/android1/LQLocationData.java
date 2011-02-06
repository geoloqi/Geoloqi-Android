package com.geoloqi.android1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class LQLocationData extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "geoloqi.db";
	private static final int DATABASE_VERSION = 2;
	
	public static final String ID = "id";
	public static final String SENT = "sent";
	public static final String DATE = "date";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ALTITUDE = "altitude";
	public static final String SPEED = "speed";
	public static final String HEADING = "heading";
	public static final String HORIZONTAL_ACCURACY = "horizontal_accuracy";
	public static final String SOURCE = "source";
	public static final String DISTANCE_FILTER = "distance_filter";
	public static final String TRACKING_LIMIT = "tracking_limit";
	public static final String RATE_LIMIT = "rate_limit";
	public static final String BATTERY = "battery";

	public LQLocationData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE lqLocationData (" +
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				SENT + " INTEGER DEFAULT 0," +
				DATE + " INTEGER," +
				LATITUDE + " REAL," +
				LONGITUDE + " REAL," +
				ALTITUDE + " INTEGER," +
				SPEED + " INTEGER," +
				HEADING + " INTEGER," +
				HORIZONTAL_ACCURACY + " INTEGER," +
				SOURCE + " TEXT," +
				DISTANCE_FILTER + " INTEGER," +
				TRACKING_LIMIT + " INTEGER," +
				RATE_LIMIT + " INTEGER," +
				BATTERY + " INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion == 1 && newVersion == 2) {
			db.execSQL("ALTER TABLE lqLocationData ADD COLUMN sent INTEGER DEFAULT 0");
		}
	}

	public void addLocation(Location location, int distanceFilter, int trackingLimit, int rateLimit) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DATE, (int)(System.currentTimeMillis() / 1000));
		values.put(LATITUDE, location.getLatitude());
		values.put(LONGITUDE, location.getLongitude());
		values.put(ALTITUDE, (int)location.getAltitude());
		values.put(HEADING, (int)location.getBearing());
		values.put(HORIZONTAL_ACCURACY, location.getAccuracy());
		values.put(SOURCE, location.getProvider());
		values.put(DISTANCE_FILTER, distanceFilter);
		values.put(TRACKING_LIMIT, trackingLimit);
		values.put(RATE_LIMIT, rateLimit);
		values.put(BATTERY,0);
		db.insertOrThrow("lqLocationData", null, values);
	}
	
	public int numberOfUnsentPoints() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM lqLocationData WHERE sent = 0", null);
		while(cursor.moveToNext()) {
			return cursor.getInt(0);
		}
		return 0;
	}
	
	public Cursor getLastLocation() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM lqLocationData ORDER BY date DESC LIMIT 1", null);
		while(cursor.moveToNext()) {
			return cursor;
		}
		return null;
	}
}
