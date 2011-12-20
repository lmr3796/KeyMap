package edu.ntu.mpp.keymap;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class FacebookMiner {
	static final int DEFAULT_RANGE = 200;
	Facebook facebook;
	public FacebookMiner(Facebook f){
		facebook = f;
	}
	public JSONObject getPlaces(double lat, double lng){
		return getPlaces(lat, lng, DEFAULT_RANGE);
	}
	public JSONObject getPlaces(double lat, double lng, int range){
		String fql = "SELECT page_id as pid, latitude as lat, longitude as lng, name " +
						"FROM place WHERE distance(lat,lng,\"" + 
						Double.toString(lat)+"\",\""+Double.toString(lng)+"\") < " + 
						Integer.toString(range);
		Bundle params = new Bundle();
        params.putString("method", "fql.query");
        params.putString("query", fql);
        try{
        	String response = facebook.request(params);
        	JSONObject jobj = new JSONObject(response);
        	return jobj;
        }catch(Exception e){
        	Log.e("lmr3796", "Error requesting for places.", e);
        }
        return null;
	}
	public ArrayList<String> getCheckins(String placeID){
		ArrayList<String> checkins = new ArrayList<String>();
		try{
			String response = facebook.request(placeID+"/checkins");
			JSONObject jobj = new JSONObject(response);
			JSONArray jarr = jobj.getJSONArray("data");
			for(int i = 0 ; i < jarr.length() ; i++){
				checkins.add(jarr.getJSONObject(i).getString("message"));
			}
		}catch(Exception e){
        	Log.e("lmr3796", "Error requesting for checkins.", e);
        }
		return checkins;
	}
};
