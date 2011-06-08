package com.geoloqi.messaging;

import java.util.Date;

public class LQToken {
	public final String accessToken;
	public final String refreshToken;
	public final int expiresIn;
	public final Date expiresAt;
	public final String scope;
	
	public LQToken(String access, String refresh, String expires, String scope) {
		this.accessToken = access;
		this.refreshToken = refresh;
		this.expiresIn = Integer.parseInt(expires);
		this.expiresAt = new Date(System.currentTimeMillis() + (expiresIn * 1000l));
		this.scope = scope;
	}

	public LQToken(String access, String refresh, long expiresAt, String scope) {
		this.accessToken = access;
		this.refreshToken = refresh;
		this.expiresIn = -1;
		this.expiresAt = new Date(expiresAt);
		this.scope = scope;
	}

	public String toString() {
		return accessToken + " " + refreshToken + " " + expiresAt.toString() + " " + scope.toString();
	}
}
