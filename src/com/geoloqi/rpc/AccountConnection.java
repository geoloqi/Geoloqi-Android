package com.geoloqi.rpc;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountConnection implements Parcelable {

	public static final Parcelable.Creator<AccountConnection> CREATOR = new Parcelable.Creator<AccountConnection>() {
		public AccountConnection createFromParcel(Parcel in) {
			return new AccountConnection(in);
		}

		public AccountConnection[] newArray(int size) {
			return new AccountConnection[size];
		}
	};

	private AccountConnection(Parcel in) {
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
