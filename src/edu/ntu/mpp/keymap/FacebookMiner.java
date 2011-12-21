package edu.ntu.mpp.keymap;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
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
	public ArrayList<String> getPlaceID(double lat, double lng){
		return getPlaceID(lat, lng, DEFAULT_RANGE);
	}
	public ArrayList<String> getPlaceID(double lat, double lng, int range){
		ArrayList<String> result = new ArrayList<String>();
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
        		result.add(jarr.getJSONObject(i).getString("page_id"));
        	}
        	return result;
        }catch(Exception e){
        	Log.e("lmr3796", "Error requesting for places.", e);
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
			Log.e("lmr3796",response);
			jobj = new JSONObject(response);
			jarr = jobj.getJSONArray("data");
			for(int i = 0 ; i < jarr.length() ; i++){
				try{
					JSONObject ckin = jarr.getJSONObject(i);
					checkins.add(ckin.getString("message"));
				}catch(JSONException e){
					Log.e("lmr3796", "Message not found.");
				}
			}
		}catch(Exception e){
        	Log.e("lmr3796", "Error requesting for checkins.", e);
			Log.e("lmr3796",e.getStackTrace().toString());
		}
		return checkins;
	}
};
