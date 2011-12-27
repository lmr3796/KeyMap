package edu.ntu.mpp.keymap;

import java.util.ArrayList;
import java.util.Queue;

import org.json.JSONArray;

public class CloudTextMaker implements Runnable{
	private double focusLat, focusLng;
	private JSONArray places;
	private FacebookMiner miner;
	private Splitter splitter;
	private Queue<Cloud> cloudQueue;
	public CloudTextMaker(FacebookMiner m, Splitter s){
		miner = m;
		splitter=s;
	}
	public void setFocus(double lat, double lng){

	}
	public Cloud getCloud(){
		synchronized (cloudQueue) {			
			return cloudQueue.poll();
		}
	}
	public boolean keepRunning = true;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (cloudQueue) {
			
		}
		
	}
};
