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
	ArrayList<String> checkins;
	JSONArray keyword;
	String allcheckin;
	
	FacebookMiner fMiner = new FacebookMiner(KeyMap.facebook);
	YahooSplitter splitter = new YahooSplitter();
	

	if(place == true){
		places = fMiner.getPlaceID(latitude, longitude, 100);
		return places;
	}
	
	places = fMiner.getPlaceID(latitude, longitude, 500);
	Log.e("places",places.toString());
	
	for(int i = 0 ; i < places.length() ; i++){
		location = (JSONObject) places.get(i);
		checkins = fMiner.getCheckins(location.getString("id"));
		
		if(checkins.size() > 0){
			allcheckin = fMiner.mergeCheckins(checkins);
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
		}
	}
	Log.e("result",result.toString());
	return result;
	
	
	/*
	JSONArray outcome = new JSONArray();
	String f = "FB";
	if(place) f = "pl";
	try{
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(PLACES_SEARCH_URL+f+"?lat="+latitude+"&lng="+
					longitude+"&tok="+token);
		response = httpclient.execute(httpget);
		Log.e("response",response.toString());
	}catch(Exception e){
		Log.e("log_tag", "Error in http connection "+e.toString());
	}
	 //convert response to string
	try{
		result = EntityUtils.toString(response.getEntity());
		Log.e("url request", "string:"+result);	
	}catch(Exception e){
			      Log.e("log_tag", "Error converting result "+e.toString());
	}
	//parse json data
			
	try{
		JSONObject json_data = new JSONObject(result);
		if(json_data.has("status")){
			String status=json_data.getString("status");
			if("0".equals(status)){
				outcome = json_data.getJSONArray("result");
			}else{
				Log.e("log_tag", status);
			}
		}else{
			Log.e("log_tag", "server_error");
		}
	}catch(JSONException e){
			Log.e("log_tag", "Error parsing data "+e.toString());
	}
	 return outcome;
	 */
	
 }
}
