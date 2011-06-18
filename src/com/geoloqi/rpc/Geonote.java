package com.geoloqi.rpc;

import android.os.Parcel;
import android.os.Parcelable;

public class Geonote implements Parcelable {

	public static final Parcelable.Creator<Geonote> CREATOR = new Parcelable.Creator<Geonote>() {
		public Geonote createFromParcel(Parcel in) {
			return new Geonote(in);
		}

		public Geonote[] newArray(int size) {
			return new Geonote[size];
		}
	};

	private Geonote(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		//TODO Auto-generated method stub
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
