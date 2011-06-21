package com.geoloqi.rpc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Semaphore;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.geoloqi.GeoloqiConstants;
import com.geoloqi.Util;
import com.geoloqi.service.LocationCollection;

public final class GeoloqiHTTPClient {

	private static final HttpClient client = new DefaultHttpClient();
	private static final String URL_BASE = "https://api.geoloqi.com/1/";
	private static final String PREFERENCES_FILE = "GEOLOQIHTTPCLIENT";

	private static final Semaphore monitorLock = new Semaphore(1, true);

	public static boolean isConnected(Context context) {
		try {
			beLoggedIn(context);
		} catch (RPCException e) {
			return false;
		}
		return true;
	}

	public static boolean isAnonymous(Context context) {
		try {
			beLoggedIn(context);
		} catch (RPCException e) {
			return false;
		}
		return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).getString("username", "").startsWith("_");
	}

	public static boolean isLoggedIn(Context context) {
		try {
			beLoggedIn(context);
		} catch (RPCException e) {
			return false;
		}
		return !context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).getString("username", "_").startsWith("_");
	}

	public static boolean signUp(Context context, String email, String username) {
		monitorLock.acquireUninterruptibly();
		try {
			MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "user/create");

			request = request.params(pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("email", email), pair("name", username));

			JSONObject response;
			response = send(request);
			saveToken(context, new OAuthToken(response));
			return true;
		} catch (RPCException e) {
			Util.log(e.getMessage());
			return false;
		} catch (JSONException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			monitorLock.release();
		}
	}

	public static boolean logIn(Context context, String username, String password) {
		monitorLock.acquireUninterruptibly();
		try {
			MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "oauth/token");
			request = request.entityParams(pair("grant_type", "password"), pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("username", username), pair("password", password));

			JSONObject response = send(request);
			OAuthToken token = new OAuthToken(response);
			Util.log("Got token: " + token);
			saveToken(context, token);
			return true;
		} catch (RPCException e) {
			Util.log("RPC Exception: " + e.getMessage());
			return false;
		} catch (JSONException e) {
			throw new RuntimeException(" JSON Exception: " + e.getMessage());
		} finally {
			monitorLock.release();
		}
	}

	public static String getUsername(Context context) {
		try {
			beLoggedIn(context);
		} catch (RPCException e) {
			return "(not logged in)";
		}
		String username = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).getString("username", null);
		Util.log("username is " + username);
		if (username.startsWith("_")) {
			return "(anonymous)";
		} else {
			return username;
		}
	}

	public static boolean postLocationUpdate(Context context, LocationCollection locations) {
		try {
			beLoggedIn(context);
		} catch (RPCException e) {
			return false;
		}

		MyRequest request;
		try {
			String accessToken = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).getString("accessToken", null);
			String json = locations.toJSON();
			request = new MyRequest(MyRequest.POST, URL_BASE + "location/update").headers(header("Content-type", "application/json"), header("Authorization", "OAuth " + accessToken)).entity(new StringEntity(json, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage());
		}

		boolean tryAgain = false;

		monitorLock.acquireUninterruptibly();
		try {
			send(request);
		} catch (ExpiredTokenException e) {
			refreshToken(context);
			tryAgain = true;
		} catch (RPCException e) {
			Util.log("RPCException in postLocationUpdate: " + e.getMessage());
			return false;
		} finally {
			monitorLock.release();
		}

		if (tryAgain) {
			return postLocationUpdate(context, locations);
		} else {
			return true;
		}
	}

	public static SharingLink postSharingLink(Context context, Integer minutes, String message) throws RPCException {
		beLoggedIn(context);
		String token = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).getString("accessToken", null);

		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "link/create");
		request.headers(header("Authorization", "OAuth " + token));
		if (minutes != null) {
			request.entityParams(pair("minutes", "" + minutes), pair("description", message));
		} else {
			request.entityParams(pair("description", message));
		}

		monitorLock.acquireUninterruptibly();
		try {
			return new SharingLink(send(request));
		} catch (JSONException e) {
			throw new RuntimeException(e.getMessage());
		} catch (ExpiredTokenException e) {
			refreshToken(context);
		} finally {
			monitorLock.release();
		}

		return postSharingLink(context, minutes, message);
	}

	protected static void beLoggedIn(Context context) throws RPCException {
		if (!context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).contains("accessToken")) {
			createAnonymousAccount(context);
		}
	}

	protected static void createAnonymousAccount(Context context) throws RPCException {
		monitorLock.acquireUninterruptibly();
		try {
			MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "user/create_anon");
			request = request.entityParams(pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET));

			JSONObject response;
			response = send(request);
			saveToken(context, new OAuthToken(response));
		} catch (JSONException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			monitorLock.release();
		}
	}

	protected static void refreshToken(Context context) {
		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "oauth/token");
		String refreshToken = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).getString("refreshToken", null);
		request = request.entityParams(pair("grant_type", "refresh_token"), pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("refresh_token", refreshToken));
		// Attach the params as an entity

		try {
			saveToken(context, new OAuthToken(send(request)));
		} catch (RPCException e) {
			throw new RuntimeException(e.getMessage());
		} catch (JSONException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	protected static void saveToken(Context context, OAuthToken token) {
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
		editor.putString("username", token.username);
		editor.putString("accessToken", token.accessToken);
		editor.putString("refreshToken", token.refreshToken);
		editor.putLong("expiresIn", token.expiresIn);
		editor.putLong("expiresAt", token.expiresAt);
		editor.putString("scope", token.scope);
		editor.commit();
	}

	protected static OAuthToken loadToken(Context context) throws Exception {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

		// The token has been set previously, or access token is null. 
		String accessToken = preferences.getString("accessToken", null);
		if (accessToken == null) {
			throw new Exception("Token is null");
		}
		// The token has been set previously.

		String username = preferences.getString("username", null);
		String refreshToken = preferences.getString("refreshToken", null);
		Long expiresIn = preferences.getLong("expiresIn", 0);
		Long expiresAt = preferences.getLong("expiresAt", 0);
		String scope = preferences.getString("scope", null);

		return new OAuthToken(username, accessToken, refreshToken, expiresIn, expiresAt, scope);
	}

	protected static void deleteToken(Context context) throws Exception {
		Editor editor = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
		editor.remove("username");
		editor.remove("accessToken");
		editor.remove("refreshToken");
		editor.remove("expiresIn");
		editor.remove("expiresAt");
		editor.remove("scope");
		editor.commit();
	}

	protected static JSONObject send(MyRequest request) throws RPCException {
		JSONObject response;
		try {
			response = new JSONObject(EntityUtils.toString(client.execute(request.request).getEntity()));
		} catch (ParseException e) {
			Util.log("ParseException: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} catch (JSONException e) {
			Util.log("JSONException: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} catch (ClientProtocolException e) {
			Util.log("ClientProtocolException: " + e.getMessage());
			throw new RPCException(e.getMessage());
		} catch (IOException e) {
			Util.log("IOException: " + e.getMessage());
			throw new RPCException(e.getMessage());
		}

		if (response.has("error")) {
			try {
				if (response.getString("error").equals("expired_token")) {
					throw new ExpiredTokenException();
				} else {
					throw new RPCException(response.getString("error"));
				}
			} catch (JSONException e) {
				throw new RuntimeException(e.getMessage());
			}
		} else {
			return response;
		}
	}

	private static BasicNameValuePair pair(String key, String val) {
		return new BasicNameValuePair(key, val);
	}

	private static Header header(String name, String val) {
		return new BasicHeader(name, val);
	}
}