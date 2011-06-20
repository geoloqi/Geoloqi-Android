package com.geoloqi.rpc;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

class OAuthToken {
	public final String username;
	public final String accessToken;
	public final String refreshToken;
	public final Long expiresIn;
	public final Long expiresAt;
	public final String scope;

	public OAuthToken(JSONObject json) throws JSONException {
		username = json.getString("username");
		accessToken = json.get("access_token").toString();
		refreshToken = json.get("refresh_token").toString();
		expiresIn = Long.parseLong(json.get("expires_in").toString()) * 1000l;
		expiresAt = System.currentTimeMillis() + expiresIn;
		scope = json.get("scope").toString();
	}

	public OAuthToken(String username, String accessToken, String refreshToken, Long expiresIn, Long expiresAt, String scope) {
		this.username = username;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
		this.expiresAt = expiresAt;
		this.scope = scope;
	}

	@Override
	public String toString() {
		return username + " " + accessToken + " " + refreshToken + " " + new Date(expiresAt).toString() + " " + scope.toString();
	}
}
