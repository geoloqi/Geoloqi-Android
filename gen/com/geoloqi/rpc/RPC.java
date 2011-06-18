/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Geoloqi/src/com/geoloqi/rpc/RPC.aidl
 */
package com.geoloqi.rpc;
public interface RPC extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.geoloqi.rpc.RPC
{
private static final java.lang.String DESCRIPTOR = "com.geoloqi.rpc.RPC";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.geoloqi.rpc.RPC interface,
 * generating a proxy if needed.
 */
public static com.geoloqi.rpc.RPC asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.geoloqi.rpc.RPC))) {
return ((com.geoloqi.rpc.RPC)iin);
}
return new com.geoloqi.rpc.RPC.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getLastLocation:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getLastLocation();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getLocationHistory:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
long _arg2;
_arg2 = data.readLong();
boolean _arg3;
_arg3 = (0!=data.readInt());
int _arg4;
_arg4 = data.readInt();
int _arg5;
_arg5 = data.readInt();
GeometricQuery _arg6;
if ((0!=data.readInt())) {
_arg6 = GeometricQuery.CREATOR.createFromParcel(data);
}
else {
_arg6 = null;
}
java.util.List<java.lang.String> _result = this.getLocationHistory(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
reply.writeNoException();
reply.writeStringList(_result);
return true;
}
case TRANSACTION_postLocationUpdate:
{
data.enforceInterface(DESCRIPTOR);
OAuthToken _arg0;
if ((0!=data.readInt())) {
_arg0 = OAuthToken.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.util.List<java.lang.String> _arg1;
_arg1 = data.createStringArrayList();
this.postLocationUpdate(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getUsername:
{
data.enforceInterface(DESCRIPTOR);
OAuthToken _arg0;
if ((0!=data.readInt())) {
_arg0 = OAuthToken.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _result = this.getUsername(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getProfile:
{
data.enforceInterface(DESCRIPTOR);
UserProfile _result = this.getProfile();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_putProfile:
{
data.enforceInterface(DESCRIPTOR);
UserProfile _arg0;
if ((0!=data.readInt())) {
_arg0 = UserProfile.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.putProfile(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getPrivacySettings:
{
data.enforceInterface(DESCRIPTOR);
PrivacySettings _result = this.getPrivacySettings();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_putPrivacySettings:
{
data.enforceInterface(DESCRIPTOR);
PrivacySettings _arg0;
if ((0!=data.readInt())) {
_arg0 = PrivacySettings.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.putPrivacySettings(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getAccountConnections:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<AccountConnection> _result = this.getAccountConnections();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_createAccount:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
OAuthToken _result = this.createAccount(_arg0, _arg1);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_createAnonymousAccount:
{
data.enforceInterface(DESCRIPTOR);
OAuthToken _result = this.createAnonymousAccount();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_postUsername:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.postUsername(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_postSharingLink:
{
data.enforceInterface(DESCRIPTOR);
OAuthToken _arg0;
if ((0!=data.readInt())) {
_arg0 = OAuthToken.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
long _arg2;
_arg2 = data.readLong();
long _arg3;
_arg3 = data.readLong();
boolean _arg4;
_arg4 = (0!=data.readInt());
SharingLink _result = this.postSharingLink(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_activateSharingLink:
{
data.enforceInterface(DESCRIPTOR);
SharingLink _arg0;
if ((0!=data.readInt())) {
_arg0 = SharingLink.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.activateSharingLink(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_deactivateSharingLink:
{
data.enforceInterface(DESCRIPTOR);
SharingLink _arg0;
if ((0!=data.readInt())) {
_arg0 = SharingLink.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.deactivateSharingLink(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_deleteSharingLink:
{
data.enforceInterface(DESCRIPTOR);
SharingLink _arg0;
if ((0!=data.readInt())) {
_arg0 = SharingLink.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.deleteSharingLink(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getLastSharedLocation:
{
data.enforceInterface(DESCRIPTOR);
SharingLink _arg0;
if ((0!=data.readInt())) {
_arg0 = SharingLink.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _result = this.getLastSharedLocation(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getSharedInfo:
{
data.enforceInterface(DESCRIPTOR);
SharingLink _arg0;
if ((0!=data.readInt())) {
_arg0 = SharingLink.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
UserProfile _result = this.getSharedInfo(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_postGeonote:
{
data.enforceInterface(DESCRIPTOR);
Geonote _arg0;
if ((0!=data.readInt())) {
_arg0 = Geonote.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.postGeonote(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_postTrigger:
{
data.enforceInterface(DESCRIPTOR);
Trigger _arg0;
if ((0!=data.readInt())) {
_arg0 = Trigger.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.postTrigger(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_deleteTrigger:
{
data.enforceInterface(DESCRIPTOR);
Trigger _arg0;
if ((0!=data.readInt())) {
_arg0 = Trigger.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.deleteTrigger(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getTriggerList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<Trigger> _result = this.getTriggerList();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_postTriggerList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<Trigger> _arg0;
_arg0 = data.createTypedArrayList(Trigger.CREATOR);
boolean _result = this.postTriggerList(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
reply.writeTypedList(_arg0);
return true;
}
case TRANSACTION_postLayer:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
Layer _result = this.postLayer(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getLayer:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _arg2;
_arg2 = (0!=data.readInt());
boolean _arg3;
_arg3 = (0!=data.readInt());
boolean _arg4;
_arg4 = (0!=data.readInt());
Layer _result = this.getLayer(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_subscribeToLayer:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.subscribeToLayer(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unsubscribeFromLayer:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.unsubscribeFromLayer(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getLayerSubscribed:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.getLayerSubscribed(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_deleteLayer:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.deleteLayer(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getLayerList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<Layer> _result = this.getLayerList();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_postPlace:
{
data.enforceInterface(DESCRIPTOR);
Place _arg0;
if ((0!=data.readInt())) {
_arg0 = Place.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.postPlace(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getPlaceList:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.util.List<Place> _result = this.getPlaceList(_arg0);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getPlaceInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
Place _result = this.getPlaceInfo(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_deletePlace:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.deletePlace(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_postMessage:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.postMessage(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_postBroadcast:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.postBroadcast(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getBroadcastCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBroadcastCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_postDeviceMessage:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
this.postDeviceMessage(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_postBatch:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _arg0;
_arg0 = data.createStringArrayList();
java.util.List<java.lang.String> _result = this.postBatch(_arg0);
reply.writeNoException();
reply.writeStringList(_result);
return true;
}
case TRANSACTION_postAuthorization:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
boolean _result = this.postAuthorization(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getToken:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
OAuthToken _result = this.getToken(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.geoloqi.rpc.RPC
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.lang.String getLastLocation() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLastLocation, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.util.List<java.lang.String> getLocationHistory(int count, long after, long before, boolean sortAscending, int accuracy, int thinning, GeometricQuery geometry) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<java.lang.String> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(count);
_data.writeLong(after);
_data.writeLong(before);
_data.writeInt(((sortAscending)?(1):(0)));
_data.writeInt(accuracy);
_data.writeInt(thinning);
if ((geometry!=null)) {
_data.writeInt(1);
geometry.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getLocationHistory, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArrayList();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void postLocationUpdate(OAuthToken token, java.util.List<java.lang.String> locations) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((token!=null)) {
_data.writeInt(1);
token.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStringList(locations);
mRemote.transact(Stub.TRANSACTION_postLocationUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getUsername(OAuthToken token) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((token!=null)) {
_data.writeInt(1);
token.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getUsername, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public UserProfile getProfile() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
UserProfile _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getProfile, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = UserProfile.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean putProfile(UserProfile profile) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((profile!=null)) {
_data.writeInt(1);
profile.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_putProfile, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public PrivacySettings getPrivacySettings() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
PrivacySettings _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPrivacySettings, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = PrivacySettings.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean putPrivacySettings(PrivacySettings settings) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((settings!=null)) {
_data.writeInt(1);
settings.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_putPrivacySettings, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.util.List<AccountConnection> getAccountConnections() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<AccountConnection> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAccountConnections, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(AccountConnection.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//	void postApplePushNotificationToken();

public OAuthToken createAccount(java.lang.String username, java.lang.String email) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
OAuthToken _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(username);
_data.writeString(email);
mRemote.transact(Stub.TRANSACTION_createAccount, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = OAuthToken.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public OAuthToken createAnonymousAccount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
OAuthToken _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_createAnonymousAccount, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = OAuthToken.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean postUsername(java.lang.String username) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(username);
mRemote.transact(Stub.TRANSACTION_postUsername, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public SharingLink postSharingLink(OAuthToken token, java.lang.String message, long start, long end, boolean recurring) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
SharingLink _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((token!=null)) {
_data.writeInt(1);
token.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(message);
_data.writeLong(start);
_data.writeLong(end);
_data.writeInt(((recurring)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_postSharingLink, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = SharingLink.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean activateSharingLink(SharingLink link) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((link!=null)) {
_data.writeInt(1);
link.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_activateSharingLink, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
if ((0!=_reply.readInt())) {
link.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean deactivateSharingLink(SharingLink link) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((link!=null)) {
_data.writeInt(1);
link.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_deactivateSharingLink, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
if ((0!=_reply.readInt())) {
link.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean deleteSharingLink(SharingLink link) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((link!=null)) {
_data.writeInt(1);
link.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_deleteSharingLink, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
if ((0!=_reply.readInt())) {
link.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getLastSharedLocation(SharingLink link) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((link!=null)) {
_data.writeInt(1);
link.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getLastSharedLocation, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public UserProfile getSharedInfo(SharingLink link) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
UserProfile _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((link!=null)) {
_data.writeInt(1);
link.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getSharedInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = UserProfile.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean postGeonote(Geonote note) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((note!=null)) {
_data.writeInt(1);
note.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_postGeonote, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
if ((0!=_reply.readInt())) {
note.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean postTrigger(Trigger trigger) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((trigger!=null)) {
_data.writeInt(1);
trigger.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_postTrigger, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
if ((0!=_reply.readInt())) {
trigger.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean deleteTrigger(Trigger trigger) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((trigger!=null)) {
_data.writeInt(1);
trigger.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_deleteTrigger, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
if ((0!=_reply.readInt())) {
trigger.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.util.List<Trigger> getTriggerList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<Trigger> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTriggerList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(Trigger.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean postTriggerList(java.util.List<Trigger> triggers) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(triggers);
mRemote.transact(Stub.TRANSACTION_postTriggerList, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
_reply.readTypedList(triggers, Trigger.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public Layer postLayer(java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
Layer _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_postLayer, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = Layer.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public Layer getLayer(int id, boolean countPlaces, boolean countValidPlaces, boolean includePlaces, boolean includeValidPlaces) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
Layer _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
_data.writeInt(((countPlaces)?(1):(0)));
_data.writeInt(((countValidPlaces)?(1):(0)));
_data.writeInt(((includePlaces)?(1):(0)));
_data.writeInt(((includeValidPlaces)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_getLayer, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = Layer.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean subscribeToLayer(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_subscribeToLayer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean unsubscribeFromLayer(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_unsubscribeFromLayer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getLayerSubscribed(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_getLayerSubscribed, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean deleteLayer(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_deleteLayer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.util.List<Layer> getLayerList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<Layer> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLayerList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(Layer.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean postPlace(Place place) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((place!=null)) {
_data.writeInt(1);
place.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_postPlace, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
if ((0!=_reply.readInt())) {
place.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.util.List<Place> getPlaceList(int layerID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<Place> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(layerID);
mRemote.transact(Stub.TRANSACTION_getPlaceList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(Place.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public Place getPlaceInfo(int placeID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
Place _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(placeID);
mRemote.transact(Stub.TRANSACTION_getPlaceInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = Place.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean deletePlace(int placeID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(placeID);
mRemote.transact(Stub.TRANSACTION_deletePlace, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean postMessage(int userID, java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(userID);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_postMessage, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean postBroadcast(java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_postBroadcast, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getBroadcastCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBroadcastCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void postDeviceMessage(int userID, int layerID, java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(userID);
_data.writeInt(layerID);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_postDeviceMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.util.List<java.lang.String> postBatch(java.util.List<java.lang.String> requests) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<java.lang.String> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStringList(requests);
mRemote.transact(Stub.TRANSACTION_postBatch, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArrayList();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean postAuthorization(java.lang.String grantType, java.lang.String username, java.lang.String password, java.lang.String secret) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(grantType);
_data.writeString(username);
_data.writeString(password);
_data.writeString(secret);
mRemote.transact(Stub.TRANSACTION_postAuthorization, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public OAuthToken getToken(java.lang.String username, java.lang.String password, java.lang.String refreshToken) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
OAuthToken _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(username);
_data.writeString(password);
_data.writeString(refreshToken);
mRemote.transact(Stub.TRANSACTION_getToken, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = OAuthToken.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getLastLocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getLocationHistory = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_postLocationUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getUsername = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getProfile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_putProfile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getPrivacySettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_putPrivacySettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getAccountConnections = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_createAccount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_createAnonymousAccount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_postUsername = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_postSharingLink = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_activateSharingLink = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_deactivateSharingLink = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_deleteSharingLink = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_getLastSharedLocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getSharedInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_postGeonote = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_postTrigger = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_deleteTrigger = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_getTriggerList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_postTriggerList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_postLayer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_getLayer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_subscribeToLayer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_unsubscribeFromLayer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_getLayerSubscribed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_deleteLayer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_getLayerList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_postPlace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_getPlaceList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_getPlaceInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_deletePlace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_postMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_postBroadcast = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_getBroadcastCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_postDeviceMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
static final int TRANSACTION_postBatch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
static final int TRANSACTION_postAuthorization = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
static final int TRANSACTION_getToken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
}
public java.lang.String getLastLocation() throws android.os.RemoteException;
public java.util.List<java.lang.String> getLocationHistory(int count, long after, long before, boolean sortAscending, int accuracy, int thinning, GeometricQuery geometry) throws android.os.RemoteException;
public void postLocationUpdate(OAuthToken token, java.util.List<java.lang.String> locations) throws android.os.RemoteException;
public java.lang.String getUsername(OAuthToken token) throws android.os.RemoteException;
public UserProfile getProfile() throws android.os.RemoteException;
public boolean putProfile(UserProfile profile) throws android.os.RemoteException;
public PrivacySettings getPrivacySettings() throws android.os.RemoteException;
public boolean putPrivacySettings(PrivacySettings settings) throws android.os.RemoteException;
public java.util.List<AccountConnection> getAccountConnections() throws android.os.RemoteException;
//	void postApplePushNotificationToken();

public OAuthToken createAccount(java.lang.String username, java.lang.String email) throws android.os.RemoteException;
public OAuthToken createAnonymousAccount() throws android.os.RemoteException;
public boolean postUsername(java.lang.String username) throws android.os.RemoteException;
public SharingLink postSharingLink(OAuthToken token, java.lang.String message, long start, long end, boolean recurring) throws android.os.RemoteException;
public boolean activateSharingLink(SharingLink link) throws android.os.RemoteException;
public boolean deactivateSharingLink(SharingLink link) throws android.os.RemoteException;
public boolean deleteSharingLink(SharingLink link) throws android.os.RemoteException;
public java.lang.String getLastSharedLocation(SharingLink link) throws android.os.RemoteException;
public UserProfile getSharedInfo(SharingLink link) throws android.os.RemoteException;
public boolean postGeonote(Geonote note) throws android.os.RemoteException;
public boolean postTrigger(Trigger trigger) throws android.os.RemoteException;
public boolean deleteTrigger(Trigger trigger) throws android.os.RemoteException;
public java.util.List<Trigger> getTriggerList() throws android.os.RemoteException;
public boolean postTriggerList(java.util.List<Trigger> triggers) throws android.os.RemoteException;
public Layer postLayer(java.lang.String name) throws android.os.RemoteException;
public Layer getLayer(int id, boolean countPlaces, boolean countValidPlaces, boolean includePlaces, boolean includeValidPlaces) throws android.os.RemoteException;
public boolean subscribeToLayer(int id) throws android.os.RemoteException;
public boolean unsubscribeFromLayer(int id) throws android.os.RemoteException;
public boolean getLayerSubscribed(int id) throws android.os.RemoteException;
public boolean deleteLayer(int id) throws android.os.RemoteException;
public java.util.List<Layer> getLayerList() throws android.os.RemoteException;
public boolean postPlace(Place place) throws android.os.RemoteException;
public java.util.List<Place> getPlaceList(int layerID) throws android.os.RemoteException;
public Place getPlaceInfo(int placeID) throws android.os.RemoteException;
public boolean deletePlace(int placeID) throws android.os.RemoteException;
public boolean postMessage(int userID, java.lang.String message) throws android.os.RemoteException;
public boolean postBroadcast(java.lang.String message) throws android.os.RemoteException;
public int getBroadcastCount() throws android.os.RemoteException;
public void postDeviceMessage(int userID, int layerID, java.lang.String message) throws android.os.RemoteException;
public java.util.List<java.lang.String> postBatch(java.util.List<java.lang.String> requests) throws android.os.RemoteException;
public boolean postAuthorization(java.lang.String grantType, java.lang.String username, java.lang.String password, java.lang.String secret) throws android.os.RemoteException;
public OAuthToken getToken(java.lang.String username, java.lang.String password, java.lang.String refreshToken) throws android.os.RemoteException;
}
