package com.geoloqi.rpc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

import com.geoloqi.GeoloqiConstants;
import com.geoloqi.Util;
import com.geoloqi.service.LocationCollection;

public abstract class GeoloqiHTTPClient {

	// Authorization State
	private static final int LOGGED_OUT = 0;
	@SuppressWarnings("unused")
	private static final int ANONYMOUS = 1;
	private static final int LOGGED_IN = 2;
	private static int status = LOGGED_OUT;
	private static OAuthToken token = null;
	// End Authorization State

	private static final HttpClient client = new DefaultHttpClient();
	private static final String URL_BASE = "https://api.geoloqi.com/1/";

	public static boolean isLoggedIn() {
		return status != LOGGED_OUT;
	}

	public static boolean logIn(String username, String password) {
		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "oauth/token");
		request = request.entityParams(pair("grant_type", "password"), pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("username", username), pair("password", password));

		try {
			JSONObject response = send(request);
			token = new OAuthToken(response);
			status = LOGGED_IN;
			return true;
		} catch (RPCException e) {
			Util.log("RPC Exception: " + e.getMessage());
			return false;
		} catch (JSONException e) {
			throw new RuntimeException(" JSON Exception: " + e.getMessage());
		}
	}

	public static boolean signUp(String email, String username) {
		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "user/create");

		request = request.params(pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("email", email), pair("name", username));

		JSONObject response;
		try {
			response = send(request);
		} catch (RPCException e) {
			Util.log(e.getMessage());
			return false;
		}

		try {
			token = new OAuthToken(response);
			status = LOGGED_IN;
			return true;
		} catch (JSONException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String getUsername() {
		if (status == LOGGED_OUT) {
			return "";//FIXME Do we return "" or "(anonymous)?"
		}
		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "account/username").headers(header("Authorization", "OAuth " + token.accessToken));

		JSONObject response;
		try {
			response = send(request);
		} catch (RPCException e) {
			throw new RuntimeException(e);
		}

		try {
			return response.getString("username");
		} catch (JSONException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static boolean postLocationUpdate(LocationCollection locations) {
		if (status == LOGGED_OUT) {
			return false;
		}
		String json = locations.toJSON();
		MyRequest request;
		try {
			request = new MyRequest(MyRequest.POST, URL_BASE + "location/update").headers(header("Content-type", "application/json"), header("Authorization", "OAuth " + token.accessToken)).entity(new StringEntity(json, HTTP.UTF_8));
			try {
				send(request);
			} catch (RPCException e) {
				throw new RuntimeException(e);
			}
			return true;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static SharingLink postSharingLink(Integer minutes, String message) {

		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "link/create");
		request.headers(header("Authorization", "OAuth " + token.accessToken));
		if (minutes == null) {
			request.params(pair("minutes", "" + minutes), pair("description", message));
		} else {
			request.params(pair("description", message));
		}

		JSONObject response;
		try {
			response = send(request);
		} catch (RPCException e) {
			throw new RuntimeException(e.getMessage());
		}

		try {
			return new SharingLink(response);
		} catch (JSONException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	//
	//	public String getUsername(OAuthToken token) throws RemoteException {
	//		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "account/username").headers(header("Authorization", "OAuth " + token.accessToken));
	//
	//		JSONObject response = request.send();
	//
	//		try {
	//			Util.log(response.toString());
	//			return response.getString("username");
	//		} catch (JSONException e) {
	//			Util.log("getUsername: JSONException: " + e.getMessage());
	//			throw new RemoteException();
	//		}
	//	}
	//
	//	public OAuthToken createAccount(String username, String email) throws RemoteException {
	//		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "user/create");
	//
	//		request = request.params(pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("email", email), pair("name", username));
	//
	//		JSONObject response = request.send();
	//		try {
	//			return new OAuthToken(response);
	//		} catch (JSONException e) {
	//			Util.log("createAccount: JSONException: " + e.getMessage());
	//			throw new RemoteException();
	//		}
	//	}
	//
	//	public SharingLink postSharingLink(OAuthToken token, String message, long start, long end, boolean recurring) throws RemoteException {
	//
	//		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "link/create");
	//		request.headers(header("Authorization", "OAuth " + token.accessToken));
	//
	//		if (message != null) {
	//			if (recurring) {
	//				request = request.params(pair("time_from", "" + start), pair("time_to", "" + end), pair("message", message));
	//			} else {
	//				request = request.params(pair("date_from", "" + start), pair("date_to", "" + end), pair("message", message));
	//			}
	//		} else {
	//			if (recurring) {
	//				request = request.params(pair("time_from", "" + start), pair("time_to", "" + end));
	//			} else {
	//				request = request.params(pair("date_from", "" + start), pair("date_to", "" + end));
	//			}
	//		}
	//
	//		JSONObject response = request.send();
	//
	//		try {
	//			return new SharingLink(response);
	//		} catch (JSONException e) {
	//			Util.log("postSharingLink: JSON Exception: " + e.getMessage());
	//			throw new RemoteException();
	//		}
	//	}
	//
	//	protected OAuthToken getToken(String username, String password, String refreshToken) throws RemoteException {
	//		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "oauth/token");
	//		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	//		if (refreshToken != null) {
	//			request = request.params(pair("grant_type", "refresh_token"), pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("refresh_token", refreshToken));
	//			params.add(new BasicNameValuePair("grant_type", "refresh_token"));
	//			params.add(new BasicNameValuePair("client_id", GeoloqiConstants.GEOLOQI_ID));
	//			params.add(new BasicNameValuePair("client_secret", GeoloqiConstants.GEOLOQI_SECRET));
	//			params.add(new BasicNameValuePair("refresh_token", refreshToken));
	//		} else if (username != null && password != null) {
	//			params.add(new BasicNameValuePair("grant_type", "password"));
	//			params.add(new BasicNameValuePair("client_id", GeoloqiConstants.GEOLOQI_ID));
	//			params.add(new BasicNameValuePair("client_secret", GeoloqiConstants.GEOLOQI_SECRET));
	//			params.add(new BasicNameValuePair("username", username));
	//			params.add(new BasicNameValuePair("password", password));
	//		} else {
	//			Util.log("getToken: Insufficient information.");
	//			throw new RemoteException();
	//		}
	//
	//		// Attach the params as an entity
	//		try {
	//			request.entity(new UrlEncodedFormEntity(params));
	//		} catch (UnsupportedEncodingException e) {
	//			Util.log("getToken: Unsupported Encoding Exception: " + e.getMessage());
	//			throw new RemoteException();
	//		}
	//
	//		JSONObject response = request.send();
	//
	//		try {
	//			return new OAuthToken(response);
	//		} catch (JSONException e) {
	//			Util.log("getToken: JSON Exception: " + e.getMessage());
	//			throw new RemoteException();
	//		}
	//	}

	protected static boolean refreshToken() {
		if (token == null) {
			throw new RuntimeException("Token is null");
		}
		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "oauth/token");
		request = request.entityParams(pair("grant_type", "refresh_token"), pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("refresh_token", token.refreshToken));
		// Attach the params as an entity

		try {
			send(request);
		} catch (RPCException e) {
			throw new RuntimeException(e.getMessage());
		}
		return true;
	}

	private static JSONObject send(MyRequest request) throws RPCException {
		JSONObject response;
		try {
			response = new JSONObject(EntityUtils.toString(client.execute(request.request).getEntity()));
		} catch (ParseException e) {
			Util.log("ParseException: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} catch (ClientProtocolException e) {
			Util.log("ClientProtocolException: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} catch (JSONException e) {
			Util.log("JSONException: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			Util.log("IOException: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}

		if (response.has("error")) {
			try {
				throw new RPCException(response.getString("error"));
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