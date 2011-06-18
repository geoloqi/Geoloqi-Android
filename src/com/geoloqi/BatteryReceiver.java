package com.geoloqi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryReceiver extends BroadcastReceiver {

	Context context;

	private int batteryLevel = 0;

	public BatteryReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		batteryLevel = intent.getIntExtra("level", 0);
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}
}
