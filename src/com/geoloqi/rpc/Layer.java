package com.geoloqi.rpc;

import android.os.Parcel;
import android.os.Parcelable;

public class Layer implements Parcelable {

	public static final Parcelable.Creator<Layer> CREATOR = new Parcelable.Creator<Layer>() {
		public Layer createFromParcel(Parcel in) {
			return new Layer(in);
		}

		public Layer[] newArray(int size) {
			return new Layer[size];
		}
	};

	private Layer(Parcel in) {
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
