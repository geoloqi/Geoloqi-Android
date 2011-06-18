package com.geoloqi.rpc;

import android.os.Parcel;
import android.os.Parcelable;

public class PrivacySettings implements Parcelable {

	public static final Parcelable.Creator<PrivacySettings> CREATOR = new Parcelable.Creator<PrivacySettings>() {
		public PrivacySettings createFromParcel(Parcel in) {
			return new PrivacySettings(in);
		}

		public PrivacySettings[] newArray(int size) {
			return new PrivacySettings[size];
		}
	};

	private PrivacySettings(Parcel in) {
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
