package com.geoloqi.rpc;

import android.os.Parcel;
import android.os.Parcelable;

public class GeometricQuery implements Parcelable {

	public static final Parcelable.Creator<GeometricQuery> CREATOR = new Parcelable.Creator<GeometricQuery>() {
		public GeometricQuery createFromParcel(Parcel in) {
			return new GeometricQuery(in);
		}

		public GeometricQuery[] newArray(int size) {
			return new GeometricQuery[size];
		}
	};

	private GeometricQuery(Parcel in) {
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
