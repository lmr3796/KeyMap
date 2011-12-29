package edu.ntu.mpp.keymap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.facebook.android.Facebook;

public class FacebookMiner {
	static final int DEFAULT_RANGE = 200;
	Facebook facebook;
	public FacebookMiner(Facebook f){
		facebook = f;
	}
	public JSONArray getPlaceID(double lat, double lng){
		return getPlaceID(lat, lng, DEFAULT_RANGE);
	}
	public JSONArray getPlaceID(double lat, double lng, int range){
		JSONArray result = new JSONArray();
		String fql = "SELECT page_id, latitude, longitude, name " +
						"FROM place WHERE distance(latitude,longitude,\"" + 
						Double.toString(lat)+"\",\""+Double.toString(lng)+"\") < " + 
						Integer.toString(range);
		Bundle params = new Bundle();
        params.putString("method", "fql.query");
        params.putString("query", fql);
        try{
        	String response = facebook.request(params);
        	JSONArray jarr = new JSONArray(response);
        	for(int i = 0 ; i < jarr.length() ; i++){
        		JSONObject place = new JSONObject();
        		place.put("id", jarr.getJSONObject(i).getString("page_id"));
        		place.put("lat",jarr.getJSONObject(i).getString("latitude"));
        		place.put("lng",jarr.getJSONObject(i).getString("longitude"));
        		place.put("name",jarr.getJSONObject(i).getString("name"));
        		result.put(place);
        	}
        	return result;
        }catch(Exception e){
        	Log.d("lmr3796", "Error requesting for places.", e);
        }
        return null;
	}
	public ArrayList<String> getCheckins(String placeID){
		ArrayList<String> checkins = new ArrayList<String>();
		String response;
		JSONObject jobj;
		JSONArray jarr;
		try{
			response = facebook.request(placeID+"/checkins");
			//Log.e("lmr3796",response);
			jobj = new JSONObject(response);
			jarr = jobj.getJSONArray("data");
			for(int i = 0 ; i < jarr.length() ; i++){
				try{
					JSONObject ckin = jarr.getJSONObject(i);
					checkins.add(ckin.getString("message"));
				}catch(JSONException e){
					//Log.e("lmr3796", "Message not found.");
				}
			}
		}catch(Exception e){
        	Log.e("lmr3796", "Error requesting for checkins.", e);
			Log.e("lmr3796",e.getStackTrace().toString());
		}
		return checkins;
	}
	public String getAllCheckins(String placeID){
		ArrayList<String> list = getCheckins(placeID);
		String result = "";
		for(int i = 0 ;i < list.size() ; i++ ){
			result += list.get(i) + "\n";
		}
		return result;
	}
};
