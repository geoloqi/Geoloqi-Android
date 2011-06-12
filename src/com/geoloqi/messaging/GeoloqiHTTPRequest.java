package com.geoloqi.messaging;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.geoloqi.Util;
import com.geoloqi.ui.Geoloqi;

public class GeoloqiHTTPRequest {

	private static String VERSION = "11.132";
	private static GeoloqiHTTPRequest singleton;
	private final String urlBase = "https://api.geoloqi.com/1/";
	public Date lastSent;

	public static GeoloqiHTTPRequest singleton() {
		if (singleton == null) {
			singleton = new GeoloqiHTTPRequest();
		}
		return singleton;
	}

	public boolean locationUpdate(Context context, String json) {
		// Post the JSON object to the API
		// Log.d(Geoloqi.TAG, json.toString());
		LQToken token = Util.getToken(context);
		if (token == null) {
			return false;
		}

		HttpClient client = new DefaultHttpClient();
		String postURL = this.urlBase + "location/update";
		HttpPost post = new HttpPost(postURL);
		post.setHeader("Content-type", "application/json");

		post.setHeader("Authorization", "OAuth " + token.accessToken);

		try {
			StringEntity ent = new StringEntity(json, HTTP.UTF_8);
			post.setEntity(ent);

			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();
			if (resEntity != null) {
				String responseString;
				responseString = EntityUtils.toString(resEntity);
				JSONObject response = new JSONObject(responseString);

				if (response.has("error")) {
					// If the error was because of an expired token, refresh the
					// token and try again
					if (response.getString("error").equals("expired_token")) {
						token = GeoloqiHTTPRequest.singleton().oauthToken(
								token.refreshToken);
						if (token != null) {
							Util.setToken(token, context);
							return locationUpdate(context, json);
						} else {
							return false;
						}
					} else {
						Log.d(Geoloqi.TAG, response.get("error") + " "
								+ response.get("error_description"));
						return false;
					}
				} else {
					Log.d(Geoloqi.TAG, "Completed HTTP request. "
							+ responseString);
					this.lastSent = new Date();
					return true;
				}
			} else {
				Log.i(Geoloqi.TAG, "Unknown error");
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public String accountUsername(Context context) {

		try {
			LQToken token = Util.getToken(context);
			if (token == null) {
				throw new Exception("No access token present.");
			}

			HttpClient client = new DefaultHttpClient();
			String postURL = this.urlBase + "account/profile";
			HttpGet request = new HttpGet(postURL);
			request.setHeader("Authorization", "OAuth " + token.accessToken);

			HttpResponse responsePOST = client.execute(request);
			HttpEntity resEntity = responsePOST.getEntity();
			if (resEntity != null) {
				String responseString = EntityUtils.toString(resEntity);
				JSONObject response = new JSONObject(responseString);

				Log.i("Geoloqi", ">>> account/username response" + responseString);

				if (response.has("error")) {
					// If the error was because of an expired token, give up :)
					if (response.getString("error").equals("expired_token")) {
						throw new Exception("Expired token");
					} else {
						throw new Exception("Unknown error: "
								+ response.getString("error"));
					}
				} else if (response.has("username")) {
					String username = response.getString("username");
					if (username.startsWith("_")) {
						if (response.has("email")
								&& !response.getString("email").equals("")) {
							return response.getString("email");
						} else {
							return "(anonymous)";
						}
					} else {
						return username;
					}
				} else {
					throw new Exception("Unknown response: " + responseString);
				}
			} else {
				throw new Exception("Empty response");
			}
		} catch (Exception e) {
			return null;
		}
	}

	public LQToken oauthToken(String username, String password) {
		try {
			HttpClient client = new DefaultHttpClient();
			String postURL = this.urlBase + "oauth/token";
			HttpPost post = new HttpPost(postURL);

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("grant_type", "password"));
			params.add(new BasicNameValuePair("client_id",
					GeoloqiConstants.GEOLOQI_ID));
			params.add(new BasicNameValuePair("client_secret",
					GeoloqiConstants.GEOLOQI_SECRET));
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("password", password));
			post.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();
			if (resEntity != null) {
				String responseString = EntityUtils.toString(resEntity);
				JSONObject response = new JSONObject(responseString);

				if (response.has("error")) {
					// Handle friendly user/pass login error messages here
					throw new Exception(response.get("error") + " "
							+ response.get("error_description"));
				}

				return new LQToken(response.get("access_token").toString(),
						response.get("refresh_token").toString(), response.get(
								"expires_in").toString(), response.get("scope")
								.toString());
			} else {
				Log.i(Geoloqi.TAG, "++++ Error getting token");
			}
		} catch (Exception e) {
			Log.i(Geoloqi.TAG, "Error getting token: " + e.toString());
		}
		return null;
	}

	public LQToken oauthToken(String refreshToken) {
		try {
			HttpClient client = new DefaultHttpClient();
			String postURL = this.urlBase + "oauth/token";
			HttpPost post = new HttpPost(postURL);

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("grant_type", "refresh_token"));
			params.add(new BasicNameValuePair("client_id",
					GeoloqiConstants.GEOLOQI_ID));
			params.add(new BasicNameValuePair("client_secret",
					GeoloqiConstants.GEOLOQI_SECRET));
			params.add(new BasicNameValuePair("refresh_token", refreshToken));
			post.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();
			if (resEntity != null) {
				String responseString = EntityUtils.toString(resEntity);

				JSONObject response = new JSONObject(responseString);

				if (response.has("error"))
					throw new Exception(response.get("error") + " "
							+ response.get("error_description"));

				return new LQToken(response.get("access_token").toString(),
						response.get("refresh_token").toString(), response.get(
								"expires_in").toString(), response.get("scope")
								.toString());
			} else {
				Log.i(Geoloqi.TAG, "++++ Error getting token");
			}
		} catch (Exception e) {
			Log.i(Geoloqi.TAG, "Error getting token: " + e.toString());
		}
		return null;
	}

	public LQToken createUser(String email, String name) {
		try {
			HttpClient client = new DefaultHttpClient();
			String postURL = this.urlBase + "user/create";
			HttpPost post = new HttpPost(postURL);

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("client_id",
					GeoloqiConstants.GEOLOQI_ID));
			params.add(new BasicNameValuePair("client_secret",
					GeoloqiConstants.GEOLOQI_SECRET));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("name", name));
			post.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();
			if (resEntity != null) {
				String responseString = EntityUtils.toString(resEntity);
				JSONObject response = new JSONObject(responseString);

				if (response.has("error")) {
					// Handle friendly signup error messages here
					throw new Exception(response.get("error") + " "
							+ response.get("error_description"));
				}

				return new LQToken(response.get("access_token").toString(),
						response.get("refresh_token").toString(), response.get(
								"expires_in").toString(), response.get("scope")
								.toString());
			} else {
				Log.i(Geoloqi.TAG, "++++ Error getting token");
			}
		} catch (Exception e) {
			Log.i(Geoloqi.TAG, "Error getting token: " + e.toString());
		}
		return null;
	}

}
