package com.geoloqi.android1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class LQLocationData extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "geoloqi.db";
	private static final int DATABASE_VERSION = 2;

	public LQLocationData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE lqLocationData (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"sent INTEGER DEFAULT 0," +
				"date INTEGER," +
				"latitude REAL," +
				"longitude REAL," +
				"altitude INTEGER," +
				"speed INTEGER," +
				"heading INTEGER," +
				"horizontal_accuracy INTEGER," +
				"source TEXT," +
				"distance_filter INTEGER," +
				"tracking_limit INTEGER," +
				"rate_limit INTEGER," +
				"battery INTEGER);");
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
		values.put("date", (int)(System.currentTimeMillis() / 1000));
		values.put("latitude", location.getLatitude());
		values.put("longitude", location.getLongitude());
		values.put("altitude", (int)location.getAltitude());
		values.put("heading", (int)location.getBearing());
		values.put("horizontal_accuracy", location.getAccuracy());
		values.put("source", location.getProvider());
		values.put("distance_filter", distanceFilter);
		values.put("tracking_limit", trackingLimit);
		values.put("rate_limit", rateLimit);
		values.put("battery",0);
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
}
