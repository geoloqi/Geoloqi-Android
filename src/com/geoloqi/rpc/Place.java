package com.geoloqi.rpc;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {

	public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
		public Place createFromParcel(Parcel in) {
			return new Place(in);
		}

		public Place[] newArray(int size) {
			return new Place[size];
		}
	};

	private Place(Parcel in) {
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
