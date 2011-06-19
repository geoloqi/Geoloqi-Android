package com.geoloqi.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Pair;

import com.geoloqi.Util;
import com.geoloqi.rpc.GeoloqiHTTPClient;

public class LocationCollection extends SQLiteOpenHelper {

	ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	Context context;
	SQLiteDatabase db;
	String name;

	public LocationCollection(Context context, String name) {
		super(context, null, null, 1);
		this.name = name;
		this.context = context;
		db = getWritableDatabase();

		lock.writeLock().lock();
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + name + " (_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, json TEXT);");
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

	public long insertLocation(Location location, Pair<String, String>... raw) {
		JSONObject json = toJSON(location, raw);
		return insertLocation(json.toString());
	}

	public void transfer(LocationCollection collection, int count) {
		collection.lock.writeLock().lock();
		lock.writeLock().lock();
		collection.db.beginTransaction();
		Cursor cursor = collection.db.rawQuery("SELECT json FROM " + collection.name + " ORDER BY _ID ASC LIMIT " + count, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					insertLocation(cursor.getString(0));
					cursor.moveToNext();
				} while (!cursor.isAfterLast());
			}

			collection.db.execSQL("DELETE FROM " + collection.name + " WHERE _ID IN (SELECT _ID FROM " + collection.name + " ORDER BY _ID ASC LIMIT " + count + ")");
			collection.db.setTransactionSuccessful();
		} finally {
			collection.db.endTransaction();
			cursor.close();
			lock.writeLock().unlock();
			collection.lock.writeLock().unlock();
		}
		size();
		collection.size();
	}

	public List<String> toList() {
		lock.readLock().lock();
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = db.rawQuery("SELECT json FROM " + name + " ORDER BY _ID ASC", null);
		try {
			if (cursor.moveToFirst()) {
				do {
					list.add(cursor.getString(0));
					cursor.moveToNext();
				} while (!cursor.isAfterLast());
			}
			return list;
		} finally {
			cursor.close();
			lock.readLock().unlock();
		}
	}

	public String toJSON() {
		List<String> locations = toList();
		String json = "[" + locations.remove(0);
		for (String s : locations) {
			json += "," + s;
		}
		json += "]";
		return json;
	}

	public void clear() {
		lock.writeLock().lock();
		try {
			db.execSQL("DELETE FROM " + name + " WHERE 1");
		} finally {
			lock.writeLock().unlock();
		}
	}

	public int size() {
		Cursor cursor = db.rawQuery("SELECT _ID FROM " + name + " WHERE 1", null);
		try {
			Util.log("Size query on " + name + " returning " + cursor.getCount());
			return cursor.getCount();
		} finally {
			cursor.close();
		}
	}

	protected long insertLocation(String json) {
		ContentValues values = new ContentValues(1);
		values.put("json", json);
		lock.writeLock().lock();
		try {
			return db.insert(name, null, values);
		} finally {
			lock.writeLock().unlock();
		}
	}

	protected JSONObject toJSON(Location l, Pair<String, String>[] rawr) {
		try {
			String name = GeoloqiHTTPClient.getUsername();
			String version = Util.getVersion();
			String platform = "2.1";
			String hardware = "unknown";
			Util.log("Encoding LQLocation");
			JSONObject point = new JSONObject();

			point.put("latitude", l.getLatitude());
			point.put("longitude", l.getLongitude());
			point.put("speed", l.getSpeed());
			point.put("altitude", l.getAltitude());
			point.put("horizontal_accuracy", l.getAccuracy());

			JSONObject location = new JSONObject();
			JSONObject raw = new JSONObject();
			JSONObject client = new JSONObject();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
			Date d = new Date(l.getTime());

			location.put("type", "point");
			location.put("position", point);

			Util.log("Encoding raw data");
			for (int i = 0; i < rawr.length; i++) {
				Util.log("Raw key: " + rawr[i].first + " Raw value: " + rawr[i].second);
				raw.put(rawr[i].first, rawr[i].second);
			}

			client.put("name", name);
			client.put("version", version);
			client.put("platform", platform);
			client.put("hardware", hardware);

			JSONObject json = new JSONObject();

			json.put("date", sdf.format(d));
			json.put("location", location);
			json.put("raw", raw);
			json.put("client", client);

			Util.log("Finished encoding LQLocation");
			return json;
		} catch (JSONException e) {
			Util.log("JSON Exception in toJSON: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
}
