package com.geoloqi.rpc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.RemoteException;

import com.geoloqi.GeoloqiConstants;
import com.geoloqi.Util;

public class RPCBinder extends RPC.Stub {

	private static final RPCBinder binder = new RPCBinder();
	private final HttpClient client = new DefaultHttpClient();
	private final String URL_BASE = "https://api.geoloqi.com/1/";

	public static RPCBinder singleton() {
		return binder;
	}

	protected RPCBinder() {
	}

	public String getLastLocation() throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException();
	}

	public List<String> getLocationHistory(int count, long after, long before, boolean sortAscending, int accuracy, int thinning, GeometricQuery geometry) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException();
	}

	public void postLocationUpdate(OAuthToken token, List<String> locations) throws RemoteException {
		JSONArray array = new JSONArray();
		try {
			Util.log("Building the JSON Array of size " + locations.size());
			for (String location : locations) {
				array.put(new JSONObject(location.toString()));
				Util.log("Encoded another point");
			}
			Util.log("Built the array.");
			MyRequest request;
			request = new MyRequest(MyRequest.POST, URL_BASE + "location/update").headers(header("Content-type", "application/json"), header("Authorization", "OAuth " + token.accessToken)).entity(new StringEntity(array.toString(), HTTP.UTF_8));
			request.send();
			return;
		} catch (UnsupportedEncodingException e) {
			Util.log("postLocationUpdate: Unsupported Encoding Exception : " + e.getMessage());
		} catch (JSONException e) {
			Util.log("postLocationUpdate: JSON Exception : " + e.getMessage());
		}
		throw new RemoteException();
	}

	public String getUsername(OAuthToken token) throws RemoteException {
		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "account/username").headers(header("Authorization", "OAuth " + token.accessToken));

		JSONObject response = request.send();

		try {
			Util.log(response.toString());
			return response.getString("username");
		} catch (JSONException e) {
			Util.log("getUsername: JSONException: " + e.getMessage());
			throw new RemoteException();
		}
	}

	public UserProfile getProfile() throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException();
	}

	public boolean putProfile(UserProfile profile) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException();
	}

	public PrivacySettings getPrivacySettings() throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException();
	}

	public boolean putPrivacySettings(PrivacySettings settings) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException();
	}

	public List<AccountConnection> getAccountConnections() throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException();
	}

	public OAuthToken createAccount(String username, String email) throws RemoteException {
		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "user/create");

		request = request.params(pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("email", email), pair("name", username));

		JSONObject response = request.send();
		try {
			return new OAuthToken(response);
		} catch (JSONException e) {
			Util.log("createAccount: JSONException: " + e.getMessage());
			throw new RemoteException();
		}
	}

	public OAuthToken createAnonymousAccount() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean postUsername(String username) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public Beacon postSharingLink(OAuthToken token, String message, long start, long end, boolean recurring) throws RemoteException {

		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "link/create");
		request.headers(header("Authorization", "OAuth " + token.accessToken));

		if (message != null) {
			if (recurring) {
				request = request.params(pair("time_from", "" + start), pair("time_to", "" + end), pair("message", message));
			} else {
				request = request.params(pair("date_from", "" + start), pair("date_to", "" + end), pair("message", message));
			}
		} else {
			if (recurring) {
				request = request.params(pair("time_from", "" + start), pair("time_to", "" + end));
			} else {
				request = request.params(pair("date_from", "" + start), pair("date_to", "" + end));
			}
		}

		JSONObject response = request.send();

		try {
			return new Beacon(response);
		} catch (JSONException e) {
			Util.log("postSharingLink: JSON Exception: " + e.getMessage());
			throw new RemoteException();
		}
	}

	public boolean activateSharingLink(Beacon link) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deactivateSharingLink(Beacon link) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteSharingLink(Beacon link) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public String getLastSharedLocation(Beacon link) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public UserProfile getSharedInfo(Beacon link) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean postGeonote(Geonote note) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean postTrigger(Trigger trigger) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteTrigger(Trigger trigger) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Trigger> getTriggerList() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean postTriggerList(List<Trigger> triggers) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public Layer postLayer(String name) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Layer getLayer(int id, boolean countPlaces, boolean countValidPlaces, boolean includePlaces, boolean includeValidPlaces) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean subscribeToLayer(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unsubscribeFromLayer(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getLayerSubscribed(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteLayer(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Layer> getLayerList() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean postPlace(Place place) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Place> getPlaceList(int layerID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Place getPlaceInfo(int placeID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deletePlace(int placeID) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean postMessage(int userID, String message) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean postBroadcast(String message) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public int getBroadcastCount() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void postDeviceMessage(int userID, int layerID, String message) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public List<String> postBatch(List<String> requests) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean postAuthorization(String grantType, String username, String password, String secret) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public OAuthToken getToken(String username, String password, String refreshToken) throws RemoteException {
		MyRequest request = new MyRequest(MyRequest.POST, URL_BASE + "oauth/token");
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		if (refreshToken != null) {
			request = request.params(pair("grant_type", "refresh_token"), pair("client_id", GeoloqiConstants.GEOLOQI_ID), pair("client_secret", GeoloqiConstants.GEOLOQI_SECRET), pair("refresh_token", refreshToken));
			params.add(new BasicNameValuePair("grant_type", "refresh_token"));
			params.add(new BasicNameValuePair("client_id", GeoloqiConstants.GEOLOQI_ID));
			params.add(new BasicNameValuePair("client_secret", GeoloqiConstants.GEOLOQI_SECRET));
			params.add(new BasicNameValuePair("refresh_token", refreshToken));
		} else if (username != null && password != null) {
			params.add(new BasicNameValuePair("grant_type", "password"));
			params.add(new BasicNameValuePair("client_id", GeoloqiConstants.GEOLOQI_ID));
			params.add(new BasicNameValuePair("client_secret", GeoloqiConstants.GEOLOQI_SECRET));
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("password", password));
		} else {
			Util.log("getToken: Insufficient information.");
			throw new RemoteException();
		}

		// Attach the params as an entity
		try {
			request.entity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			Util.log("getToken: Unsupported Encoding Exception: " + e.getMessage());
			throw new RemoteException();
		}

		JSONObject response = request.send();

		try {
			return new OAuthToken(response);
		} catch (JSONException e) {
			Util.log("getToken: JSON Exception: " + e.getMessage());
			throw new RemoteException();
		}
	}

	private BasicNameValuePair pair(String key, String val) {
		return new BasicNameValuePair(key, val);
	}

	private Header header(String name, String val) {
		return new BasicHeader(name, val);
	}

	private class MyRequest {
		static final int GET = 0;
		static final int POST = 1;

		private final HttpRequestBase request;

		MyRequest(int requestType, String url) {
			switch (requestType) {
			case GET:
				request = new HttpGet(url);
				break;
			case POST:
				request = new HttpPost(url);
				break;
			default:
				throw new IllegalArgumentException("Request type must be one of the static types.");
			}
		}

		MyRequest headers(Header... headers) {
			request.setHeaders(headers);
			return this;
		}

		MyRequest params(BasicNameValuePair... pairs) {
			BasicHttpParams params = new BasicHttpParams();
			for (int i = 0; i < pairs.length; i++) {
				params.setParameter(pairs[i].getName(), pairs[i].getValue());
			}
			request.setParams(params);
			return this;
		}

		MyRequest entity(AbstractHttpEntity entity) {
			if (request instanceof HttpEntityEnclosingRequestBase) {
				((HttpEntityEnclosingRequestBase) request).setEntity(entity);
			} else {
				throw new IllegalArgumentException("Request must be PUT or POST to enclose an entity.");
			}
			return this;
		}

		JSONObject send() throws RemoteException {
			try {
				JSONObject response = new JSONObject(EntityUtils.toString(client.execute(request).getEntity()));
				if (!response.has("error")) {
					return response;
				}
				if (response.has("error_description")) {
					Util.log("RPC Exception: " + response.get("error_description"));
				} else {
					Util.log("RPC Exception: " + response.getString("error"));
				}
			} catch (ClientProtocolException e) {
				Util.log(request.getURI() + " -> Client Protocol Exception: " + e.getMessage());
			} catch (ParseException e) {
				Util.log(request.getURI() + " -> Parse Exception: " + e.getMessage());
			} catch (IOException e) {
				Util.log(request.getURI() + " -> I/O Exception: " + e.getMessage());
			} catch (JSONException e) {
				Util.log(request.getURI() + " -> JSON Exception: " + e.getMessage());
			} catch (NullPointerException e) {
				Util.log(request.getURI() + " -> Null Pointer Exception: " + e.getMessage());
			}
			throw new RemoteException();
		}
	}
}