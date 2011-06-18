package com.geoloqi.rpc;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Beacon implements Parcelable {

	public String link;
	public String shortLink;
	public String token;

	private Beacon(Parcel in) {
		readFromParcel(in);
	}

	public Beacon(JSONObject json) throws JSONException {
		this.link = json.getString("link");
		this.shortLink = json.getString("shortlink");
		this.token = json.getString("token");
	}

	public void readFromParcel(Parcel in) {
		link = in.readString();
		shortLink = in.readString();
		token = in.readString();
	}

	public static final Parcelable.Creator<Beacon> CREATOR = new Parcelable.Creator<Beacon>() {
		public Beacon createFromParcel(Parcel in) {
			return new Beacon(in);
		}

		public Beacon[] newArray(int size) {
			return new Beacon[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(link);
		dest.writeString(shortLink);
		dest.writeString(token);
	}

}
