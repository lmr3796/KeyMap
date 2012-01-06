package edu.ntu.mpp.keymap;

import java.util.ArrayList;

import org.json.JSONArray;
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
	public static int getMaxKeyWordSize(){
		int sum = 0;
		for(int i = 0 ; i < sizeConstraint.length ; i++)
			sum += sizeConstraint[i];
		return sum;
	}
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
		this.keyWords = (ArrayList<String>)keyWords.clone();
	}
	public void addKeyWord(String s){
		keyWords.add(s);
	}
	public ArrayList<ArrayList<String>> getTieredKeyWords(){
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		for(int i = 0 ; i < keyWords.size() ; i++){
			for(int j = 0 ; j < sizeConstraint.length ; j++){
				if(result.get(j).size() < sizeConstraint[j]){
					result.get(j).add(keyWords.get(i));
					break;
				}
			}
		}
		return result;
	}
	public JSONObject toJSONObject(){
		JSONObject result = new JSONObject();
		try{
			result.put("id", Long.toString(page_id));
			result.put("name", name);
			result.put("lat", lat);
			result.put("lng", lng);
			// Tier key words to 3 level
			JSONArray kw = new JSONArray();
			for(int i = 0 ; i < 3 ; i++)
				kw.put(new JSONArray());
			ArrayList<ArrayList<String>> tieredKeyWord = getTieredKeyWords();
			for(int i = 0 ; i < tieredKeyWord.size() ; i++){
				for(int j = 0 ; j < tieredKeyWord.get(i).size(); j++){
					((JSONArray)kw.get(i)).put(tieredKeyWord.get(i).get(j));
				}
			}
			result.put("kw", kw);
		}catch(JSONException e){
			Log.e("Cloud", "Converting JSONObject.", e);
			return null;
		}
		return result;
	}
	public static JSONArray listToJSONArray(ArrayList<Cloud> cloudList){
		JSONArray result = new JSONArray();
		for(int i = 0 ; i < cloudList.size() ; i++){
			JSONObject cloudJSONObj = cloudList.get(i).toJSONObject();
			if(cloudJSONObj != null)
				result.put(cloudList.get(i).toJSONObject());
		}
		return result;
	}
	public void join(Cloud cloud){
		if(cloud.getKeyWords().size() == 0)
			return;
		if(keyWords.size() == 0)
			keyWords = (ArrayList<String>)cloud.getKeyWords().clone();
		
		int idx_a = 0, idx_b = 0;
		ArrayList<String> all = new ArrayList<String>();
		ArrayList<String> a, b;
		// Ensure that size of a > size of b
		if(keyWords.size() > cloud.getKeyWords().size()){
			a = keyWords;
			b = cloud.getKeyWords();
		}else{
			b = keyWords;
			a = cloud.getKeyWords();
		}
		int ratio = a.size() / (b.size()+1) + ((a.size() % b.size() == 0) ? 0 : 1); 
		for(int i = 0 ; i < getMaxKeyWordSize() ; i = (i + 1) % (ratio + 1)){
			if(idx_a == a.size() && idx_b == b.size())
				break;
			else if(idx_a == a.size() && idx_b < b.size())
				all.add(b.get(idx_b++));
			else if(idx_a < a.size() && idx_b == b.size())
				all.add(a.get(idx_a++));
			else
				all.add( (i < ratio) ? a.get(idx_a++) : b.get(idx_b++) );
		}
		keyWords = all;
	}
}
