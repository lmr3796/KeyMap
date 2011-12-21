package edu.ntu.mpp.keymap;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.Facebook;


import android.app.Activity;
import android.location.Location;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
				//Log.e("log_tag", "ok");
				outcome = json_data.getJSONArray("result");
				/*
				//Log.e("log_tag","length "+jArray.length());
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject j = jArray.optJSONObject(i);
					//Log.e("log_tag",j.getString("name"));
					//outcome.add(j);
					}*/
			}/*
			else if("ZERO_RESULTS".equals(status)){
				Log.e("log_tag", "ZERO_RESULTS");
			}*/
			else{
				Log.e("log_tag", status);
			}
		}
		else{
			Log.e("log_tag", "server_error");
		}
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data "+e.toString());
		}
		
		

	 return outcome;
 }
 /*
 public static ArrayList<PlaceModel> getPlaceList(Facebook facebook,double lat,double lon, int offset, int limit) {
     Bundle params = new Bundle();
     int distance = 500 ;
     params.putString("method", "fql.query");
     params.putString("query", "SELECT page_id, name, latitude, longitude FROM place WHERE distance(latitude, longitude, '" + lat + "', '" + lon + "') < "+ distance);

     ArrayList<HashMap> placeList = new ArrayList<PlaceModel>();
     String response = null;
     try {
         response = facebook.request(params);
         JSONArray jsonArr = new JSONArray(response);
         for(int i=0; i<jsonArr.length(); i++) {
             JSONObject jsonObj = jsonArr.getJSONObject(i);
             PlaceModel place = new PlaceModel();
             place.pageId = jsonObj.getString("page_id");
             place.name = jsonObj.getString("name");
             place.description = jsonObj.getString("description");
             place.latitude = jsonObj.getDouble("latitude");
             place.longitude = jsonObj.getDouble("longitude");
             place.checkInCount = jsonObj.getInt("checkin_count");
             place.distance = jsonObj.getDouble("distance_meters");
             placeList.add(place);
         }
         return placeList;
     } catch (MalformedURLException e) {
         Log.e("Example", "ERROR", e);
         return placeList;
     } catch (IOException e) {
         Log.e("Example", "ERROR", e);
         return placeList;
     } catch (Exception e) {
         Log.e("Example", "ERROR", e);
         return placeList;
     }
 }*/
 
 
}//end class

