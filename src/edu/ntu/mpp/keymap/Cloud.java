package edu.ntu.mpp.keymap;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Cloud {
	private static final int[] sizeConstraint = {4, 8, 16};
	private long page_id = 0; 
	private double lat = 0;
	private double lng = 0;
	private String name = "";
	private ArrayList<String> keyWords = new ArrayList<String>();
	
	public long getID(){
		return page_id;
	}
	public void setID(long id){
		page_id = id;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(ArrayList<String> keyWords) {
		this.keyWords = keyWords;
	}
	public void addKeyWord(String s){
		keyWords.add(s);
	}
	public JSONObject toJSONObject(){
		JSONObject result = new JSONObject();
		try{
			result.put("id", Long.toString(page_id));
			result.put("name", name);
			result.put("lat", lat);
			result.put("lng", lng);
		}catch(JSONException e){
			Log.e("Cloud", "Converting JSONObject.");
			Log.e("Cloud", e.getStackTrace().toString());
			return null;
		}
		return result;
	}
}
