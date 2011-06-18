package com.geoloqi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

public abstract class GeoloqiReceiver extends BroadcastReceiver {

	protected GeoloqiReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		onReceive(context, Util.decodeLocation(intent.getData()));
	}

	protected abstract void onReceive(Context context, Location location);

}
