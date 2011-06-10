package com.geoloqi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BatteryReceiver extends BroadcastReceiver {
	
	Context context;
	
	private int batteryLevel = 0;
	
	public BatteryReceiver(Context context) {
		this.context = context;
		context.registerReceiver(this,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		batteryLevel = intent.getIntExtra("level", 0);
	}
	
	public int getBatteryLevel() {
		return batteryLevel;
	}
	
	@Override
	public void finalize() throws Throwable{
		try{
			Util.log("BatteryReceiver going down.");
			context.unregisterReceiver(this);
		}finally {
			super.finalize();
		}
	}
}
