package com.geoloqi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.PatternMatcher;

public abstract class GeoloqiReceiver extends BroadcastReceiver {

	Context context;
	
	protected GeoloqiReceiver(Context context){
		super();
		this.context = context;
		IntentFilter filter = new IntentFilter(Intent.ACTION_EDIT);
		filter.addDataScheme("geo");
		filter.addDataAuthority("geoloqi.com", null);
		filter.addDataPath(".*", PatternMatcher.PATTERN_SIMPLE_GLOB);
		context.registerReceiver(this, filter);
	}
	
	@Override
	public void onReceive(Context context, Intent intent){
		onReceive(context, Util.decodeLocation(intent.getData()));
	}
	
	protected abstract void onReceive(Context context, Location location);
	
	@Override
	public void finalize() throws Throwable{
		try{
			Util.log("Receiver going down.");
			context.unregisterReceiver(this);
		}finally {
			super.finalize();
		}
	}

}
