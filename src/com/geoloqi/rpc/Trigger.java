package com.geoloqi.rpc;

import android.os.Parcel;
import android.os.Parcelable;

public class Trigger implements Parcelable {

	public static final Parcelable.Creator<Trigger> CREATOR = new Parcelable.Creator<Trigger>() {
		public Trigger createFromParcel(Parcel in) {
			return new Trigger(in);
		}

		public Trigger[] newArray(int size) {
			return new Trigger[size];
		}
	};

	private Trigger(Parcel in) {
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
