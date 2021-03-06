package edu.ntu.mpp.keymap;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class Server {
	
private String result = null;
private HttpResponse response=null;

private static final String PLACES_SEARCH_URL =  "http://linux9.csie.org:3796/";
//private static final String API_KEY = "AIzaSyCm-5HSgkhLKgWjXV6OgbhpyqJaRxN--JA";

	/*******
 	* 
 	* @param latitude , longitude
 	* @return nearby places
 	*/
	public JSONArray Search(double latitude, double longitude, String token, boolean place) throws Exception {
	
	
		JSONArray places;
		JSONArray result = new JSONArray();
		JSONObject location;
		JSONArray keyword;
		ArrayList<String> checkins;
		String allcheckin;
	

		
		FacebookMiner fMiner = new FacebookMiner(KeyMap.facebook);
		YahooSplitter splitter = new YahooSplitter();
	

		if(place == true){
			places = fMiner.getPlaceID(latitude, longitude,50);
			return places;
		}
	
		places = fMiner.getPlaceID(latitude, longitude);
		int [] check = new int[places.length()];
		Log.e("places",places.toString());
		
		for(int i = 0 ; i < check.length ; i++)
			check[i] = 0;
		
		for(int i = 0 ; i < places.length() ; i++){
			location = (JSONObject) places.get(i);
			//checkins = fMiner.getCheckins(location.getString("id"));
			
			if(check[i] == 1)
				continue;
			
			allcheckin = fMiner.getAllCheckins(location.getString("id"));
			
			if(allcheckin.length() != 0){
				//allcheckin = fMiner.mergeCheckins(checkins);
				
				for(int j = i+1 ; j < places.length() ; j++){
					JSONObject near = (JSONObject) places.get(j);
					if(Distance.dist(location.getDouble("lat"),location.getDouble("lng"), near.getDouble("lat"), near.getDouble("lng")) < 20){
						//allcheckin += fMiner.mergeCheckins(fMiner.getCheckins(near.getString("id")));
						allcheckin += fMiner.getAllCheckins(near.getString("id"));
						check[j] = 1;
						Log.e("merge",location.getString("name") + " " + near.getString("name"));
					}
				}
				keyword = splitter.split(allcheckin);
				if(keyword != null){
					JSONObject keycloud = new JSONObject();
					keycloud.put("name", location.get("name"));
					keycloud.put("id", location.get("id"));
					keycloud.put("lat", location.get("lat"));
					keycloud.put("lng", location.get("lng"));
					keycloud.put("kw", keyword);
					//Log.e("allcheckin",allcheckin);
					//Log.e("keyword",keyword.toString());
					result.put(keycloud);
				}
			}else{
				check[i] = 1;
			}
		}
		Log.e("result",result.toString());
		return result;
	}
	
}
