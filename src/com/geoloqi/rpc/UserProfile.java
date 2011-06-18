package com.geoloqi.rpc;

import android.os.Parcel;
import android.os.Parcelable;

public class UserProfile implements Parcelable {

	public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
		public UserProfile createFromParcel(Parcel in) {
			return new UserProfile(in);
		}

		public UserProfile[] newArray(int size) {
			return new UserProfile[size];
		}
	};

	private UserProfile(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		//TODO Auto-generated method stub
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
