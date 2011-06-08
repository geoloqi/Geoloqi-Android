package com.geoloqi;

import android.location.Location;
import android.net.Uri;

public abstract class Util {

	public static Uri encodeLocation(Location l){
		String path = "/" +
			l.getLatitude() + "/" +
			l.getLongitude() + "/" + 
			l.getAltitude() + "/" +
			l.getBearing() + "/" +
			l.getSpeed() + "/" +
			l.getTime() + "/" +
			l.getAccuracy() + "/";
		
		// (latitude,longitude,altitude,bearing,speed,time,accuracy)
		Uri uri = (new Uri.Builder()).scheme("geo").authority("geoloqi.com").path(path).build();
		decodeLocation(uri);
		return uri;
	}

	public static Location decodeLocation(Uri uri) {
		String[] data = uri.getPath().split("/");
		Location l = new Location("Geoloqi");
		l.setLatitude(Float.parseFloat(data[1]));
		l.setLongitude(Float.parseFloat(data[2]));
		l.setAltitude(Float.parseFloat(data[3]));
		l.setBearing(Float.parseFloat(data[4]));
		l.setSpeed(Float.parseFloat(data[5]));
		l.setTime(Long.parseLong(data[6]));
		l.setAccuracy(Float.parseFloat(data[7]));
		return l;
	}
	
	

}
