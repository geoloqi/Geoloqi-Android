package com.geoloqi.rpc;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class OAuthToken implements Parcelable {
	public final String accessToken;
	public final String refreshToken;
	public final int expiresIn;
	public final Date expiresAt;
	public final String scope;

	//TODO remove this method
	public OAuthToken(String access, String refresh, String expires, String scope) {
		this.accessToken = access;
		this.refreshToken = refresh;
		this.expiresIn = Integer.parseInt(expires);
		this.expiresAt = new Date(System.currentTimeMillis() + (expiresIn * 1000l));
		this.scope = scope;
	}

	//TODO remove this method
	public OAuthToken(String access, String refresh, long expiresAt, String scope) {
		this.accessToken = access;
		this.refreshToken = refresh;
		this.expiresIn = -1;
		this.expiresAt = new Date(expiresAt);
		this.scope = scope;
	}

	public OAuthToken(JSONObject json) throws JSONException {
		this(json.get("access_token").toString(), json.get("refresh_token").toString(), json.get("expires_in").toString(), json.get("scope").toString());
	}

	public static final Parcelable.Creator<OAuthToken> CREATOR = new Parcelable.Creator<OAuthToken>() {
		public OAuthToken createFromParcel(Parcel in) {
			return new OAuthToken(in);
		}

		public OAuthToken[] newArray(int size) {
			return new OAuthToken[size];
		}
	};

	private OAuthToken(Parcel in) {
		//FIXME temporary
		scope = "";
		accessToken = "";
		refreshToken = "";
		expiresIn = 0;
		expiresAt = new Date(System.currentTimeMillis());
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

	@Override
	public String toString() {
		return accessToken + " " + refreshToken + " " + expiresAt.toString() + " " + scope.toString();
	}
}
