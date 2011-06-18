package com.geoloqi.rpc;

import java.util.List;
import com.geoloqi.rpc.AccountConnection;
import com.geoloqi.rpc.GeometricQuery;
import com.geoloqi.rpc.Geonote;
import com.geoloqi.rpc.Layer;
import com.geoloqi.rpc.OAuthToken;
import com.geoloqi.rpc.Place;
import com.geoloqi.rpc.PrivacySettings;
import com.geoloqi.rpc.SharingLink;
import com.geoloqi.rpc.SharingWindow;
import com.geoloqi.rpc.Trigger;
import com.geoloqi.rpc.UserProfile;

interface RPC {

	String getLastLocation();

	List<String> getLocationHistory(int count, long after, long before, boolean sortAscending, int accuracy, int thinning, in GeometricQuery geometry);

	void postLocationUpdate(in OAuthToken token, in List<String> locations);

	String getUsername(in OAuthToken token);

	UserProfile getProfile();

	boolean putProfile(in UserProfile profile);

	PrivacySettings getPrivacySettings();

	boolean putPrivacySettings(in PrivacySettings settings);

	List<AccountConnection> getAccountConnections();

	//	void postApplePushNotificationToken();

	OAuthToken createAccount(String username, String email);

	OAuthToken createAnonymousAccount();

	boolean postUsername(String username);

	SharingLink postSharingLink(in OAuthToken token, String message, long start, long end, boolean recurring);

	boolean activateSharingLink(inout SharingLink link);

	boolean deactivateSharingLink(inout SharingLink link);

	boolean deleteSharingLink(inout SharingLink link);

	String getLastSharedLocation(in SharingLink link);

	UserProfile getSharedInfo(in SharingLink link);

	boolean postGeonote(inout Geonote note);

	boolean postTrigger(inout Trigger trigger);

	boolean deleteTrigger(inout Trigger trigger);

	List<Trigger> getTriggerList();

	boolean postTriggerList(inout List<Trigger> triggers);

	Layer postLayer(String name);

	Layer getLayer(int id, boolean countPlaces, boolean countValidPlaces, boolean includePlaces, boolean includeValidPlaces);

	boolean subscribeToLayer(int id);

	boolean unsubscribeFromLayer(int id);

	boolean getLayerSubscribed(int id);

	boolean deleteLayer(int id);

	List<Layer> getLayerList();

	boolean postPlace(inout Place place);

	List<Place> getPlaceList(int layerID);

	Place getPlaceInfo(int placeID);

	boolean deletePlace(int placeID);
	
	boolean postMessage(int userID, String message);

	boolean postBroadcast(String message);

	int getBroadcastCount();

	void postDeviceMessage(int userID, int layerID, String message);

	List<String> postBatch(in List<String> requests);

	boolean postAuthorization(String grantType, String username, String password, String secret);

	OAuthToken getToken(String username, String password, String refreshToken);

}