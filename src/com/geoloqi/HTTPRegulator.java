package com.geoloqi;

import java.util.LinkedList;

public class HTTPRegulator {
	
	private int windowSize;
	private boolean slowStart = true;
	private boolean congestionAvoidance = false;
	LinkedList<Boolean> history = new LinkedList<Boolean>();
	
	public HTTPRegulator(int initialWindow) {
		this.windowSize = initialWindow;
		history.add(true);
		history.add(true);
		history.add(true);
	}
	
	public HTTPRegulator() {
		this(2);
	}
	
	public void sendFailed() {
		update(false);
		windowSize = congestionAvoidance ? Math.max(1,windowSize/2) : windowSize+1;
		if(windowSize == 1 && congestionAvoidance) {
			try {// If sending fails for minimal parameters, network may be down.  Sleep to reduce polling.
				Thread.sleep(15000);
			} catch (InterruptedException e) { }
		}
	}
	
	public void sendSucceeded() {
		update(true);
		windowSize = slowStart ? windowSize * 2 : windowSize+1;
	}
	
	private void update(boolean b) {
		history.offer(b);
		history.remove();
		slowStart = history.get(0) && history.get(1) && history.get(2);
		congestionAvoidance = !(history.get(0) || history.get(1) || history.get(2));
		
		String debug = "Window size is now "+windowSize+" points.  " +
			(slowStart ? "Slow start is on.  " : "Slow start is off.  ") +
			(congestionAvoidance ? "Congestion avoidance is on." : "Congestion avoidance is off.");
		Util.log(debug);
	}
	
	public int getWindowSize() {
		return windowSize;
	}
	
}
