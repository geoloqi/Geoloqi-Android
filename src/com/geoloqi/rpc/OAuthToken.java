package com.geoloqi.rpc;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class OAuthToken {
	public final String accessToken;
	public final String refreshToken;
	public final int expiresIn;
	public final Date expiresAt;
	public final String scope;

	public OAuthToken(JSONObject json) throws JSONException {
		accessToken = json.get("access_token").toString();
		refreshToken = json.get("refresh_token").toString();
		expiresIn = Integer.parseInt(json.get("expires_in").toString());
		expiresAt = new Date(System.currentTimeMillis() + (expiresIn * 1000l));
		scope = json.get("scope").toString();
	}

	@Override
	public String toString() {
		return accessToken + " " + refreshToken + " " + expiresAt.toString() + " " + scope.toString();
	}
}
