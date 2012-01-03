package edu.ntu.mpp.keymap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;

public class GoogleMapActivity extends MapActivity implements Runnable{
	private CloudTextMaker cloudTextMaker;
	private HashSet<String> renderedPlaces = new HashSet<String>();
	public boolean recordPlace(String placeID){
		if(renderedPlaces.contains(placeID))
			return false;
		renderedPlaces.add(placeID);
		return true;
	}
	public void refreshCloudOnMap(){
		refreshOnClickListener.onClick(refresh);
	}
	private OnClickListener refreshOnClickListener = new OnClickListener() {
		public void onClick(View v){
			//Toast.makeText(GoogleMapActivity.this, "Please wait..", Toast.LENGTH_LONG).show();
			Log.e("center",Map.getMapCenter().toString());
			GeoPoint center = Map.getMapCenter();
			lat = (double)center.getLatitudeE6() / 1000000;
			lng = (double)center.getLongitudeE6()/ 1000000;
			cloudTextMaker.stopFetching();
			cloudTextMaker.setFocus(lat, lng);
			cloudTextMaker.start();
			/*
			Thread t = new Thread(GoogleMapActivity.this);
	    	status.setText("Loading text...");
	    	t.start();
	    	*/
		}
	};
	private Button refresh, checkin;
	TextView status;
	ProgressBar progress;
	//
    /** Called when the activity is first created. */
	private CustomMapView Map;
    MapController controller;
    List<Overlay> listOfOverlays;
    MapOverlay mapOverlay;
    JSONArray result = new JSONArray();
    JSONArray result_p = new JSONArray();
    boolean ready = false;
    Intent intent;
    GeoPoint csie = new GeoPoint(
			(int) (25.019521057333 * 1000000),
			(int) (121.541764862 * 1000000)
		);
    GeoPoint loc;
    
    double lat, lng, focusLat, focusLng;
    String token;
    boolean load=false;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        setTitle("KeyMap");
        //
        intent = getIntent();
        focusLat = intent.getDoubleExtra("lat", 0);
        focusLng = intent.getDoubleExtra("lng", 0);
		Log.e("Location","lat="+lat+"lng="+lng);
        token = intent.getStringExtra("token");
        Log.e("get_lat","lat="+intent.getDoubleExtra("lat", 0));
        Log.e("get_lng","lng="+intent.getDoubleExtra("lng", 0));
        Log.e("get_token","tok="+intent.getStringExtra("token"));

        loc = new GeoPoint(
    			(int) (focusLat * 1000000),
    			(int) (focusLng * 1000000)
    		);
        refresh = (Button)findViewById(R.id.refresh);
        checkin = (Button)findViewById(R.id.checkin);
        status = (TextView)findViewById(R.id.info);
        progress = (ProgressBar)findViewById(R.id.progressBar1);
        
        findViews();
        setupMap();
        Map.setOnPanListener(listener);
        listOfOverlays = Map.getOverlays();
        listOfOverlays.clear();

        refresh.setOnTouchListener(new Button.OnTouchListener(){
           
           public boolean onTouch(View arg0, MotionEvent motionEvent) {
        	   	if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {  
                	refresh.setBackgroundResource(R.drawable.re_on);
                }  
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {  
                	refresh.setBackgroundResource(R.drawable.re);  
                }  
                return false;
           }
        });
        refresh.setOnClickListener(refreshOnClickListener);
        checkin.setOnTouchListener(new Button.OnTouchListener(){
           
           public boolean onTouch(View arg0, MotionEvent motionEvent) {
        	    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {  //���U���ɭԧ��ܭI�����C��
                	checkin.setBackgroundResource(R.drawable.pa_on);
                }  
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {  //�_�Ӫ��ɭԫ�_�I���P�C��
                	checkin.setBackgroundResource(R.drawable.pa);  
                }  
            return false;
           }
        });
    	checkin.setOnClickListener(new OnClickListener() {
    		public void onClick(View v){
    			FacebookMiner fMiner = new FacebookMiner(KeyMap.facebook);
    			result_p = fMiner.getPlaceID(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lng", 0));
    			Intent intent2 = new Intent();	
    			intent2.putExtra("token", token);
    			intent2.putExtra("size", result_p.length());
    			for(int i=0;i<result_p.length();i++){
    				try {
						intent2.putExtra("p"+i, result_p.getJSONObject(i).getString("name"));
						intent2.putExtra("lat"+i, result_p.getJSONObject(i).getString("lat"));
						intent2.putExtra("lng"+i, result_p.getJSONObject(i).getString("lng"));
						intent2.putExtra("id"+i, result_p.getJSONObject(i).getString("id"));
						Log.e("place",result_p.getJSONObject(i).getString("name"));
    				} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    			intent2.setClass(GoogleMapActivity.this, SelectPlace.class);
            	startActivity(intent2);
    		}
    	});
    	
        //ilmftb's super method
    	cloudTextMaker = new CloudTextMaker(new FacebookMiner(KeyMap.facebook), new YahooSplitter(), this);
        refreshCloudOnMap();
    }

    private void findViews(){
    	Map = (CustomMapView) findViewById(R.id.map);
    	controller = Map.getController();
    }
    private void setupMap(){
		Map.setSatellite(false);
		Map.setTraffic(false);
    	Map.setStreetView(false);
    	Map.setBuiltInZoomControls(true);
    	controller.setZoom(18);
    	controller.animateTo(loc);
    }
	OnPanAndZoomListener listener = new OnPanAndZoomListener(){

		public void onPan() {
			// TODO Auto-generated method stub
			
		}
		public void onZoom() {
			// TODO Auto-generated method stub
			int level = Map.getZoomLevel();
			Log.e("result", result.toString());
			setTitle("ZOOM CHANGED "+Integer.toString(level));
			setOverlay(level,result);
		
		}
	};
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	public void setOverlay(int level ,JSONArray result){
		switch(level){
		case 21:
			listOfOverlays.clear();
			mapOverlay = new MapOverlay(30,2,result);
			listOfOverlays.add(mapOverlay);
			break;
		case 20:
			listOfOverlays.clear();
			mapOverlay = new MapOverlay(23,2,result);
			listOfOverlays.add(mapOverlay);
			break;
		case 19:
			listOfOverlays.clear();
			mapOverlay = new MapOverlay(15,1,result);
			listOfOverlays.add(mapOverlay);
			break;
		case 18:
			listOfOverlays.clear();
			mapOverlay = new MapOverlay(10,1,result);
			listOfOverlays.add(mapOverlay);
			break;
		default:
			listOfOverlays.clear();
			break;
		}
	}
	
	public void run() {
		//ProgressBar progress = new ProgressBar(this);
		ready = false;
		JSONArray places;
		JSONObject location;
		JSONArray keyword;
		String allcheckin;
		
		FacebookMiner fMiner = new FacebookMiner(KeyMap.facebook);
		YahooSplitter splitter = new YahooSplitter();
		
		places = fMiner.getPlaceID(lat, lng);
		boolean [] check = new boolean[places.length()];
		Log.e("places",places.toString());
		
		
		// Get ilmftb's stuff
		for(int i = 0 ; i < places.length() ; i++){
			if(check[i])
				continue;
			try{
				location = (JSONObject) places.get(i);
				// Escape gained checkins
				if(!recordPlace(location.getString("id")))
					continue;
				//Log.d("lmr3796", "Fetching~");
				allcheckin = fMiner.getAllCheckins(location.getString("id"));
				if(allcheckin.length() == 0){
					//Log.d("lmr3796", "Nothing~");
					check[i] = true;
					continue;
				}
				
				for(int j = i+1 ; j < places.length() ; j++){
					JSONObject near = (JSONObject) places.get(j);
					if(Distance.dist(location.getDouble("lat"),location.getDouble("lng"),
							near.getDouble("lat"), near.getDouble("lng")) < 20 && 
							recordPlace(near.getString("id"))){
						allcheckin += fMiner.getAllCheckins(near.getString("id"));
						//Log.d("lmr3796", "Clustering~");
						check[j] = true;
						Log.e("merge",location.getString("name") + " " + near.getString("name"));
					}
				}
				keyword = splitter.split(allcheckin);
				//Log.d("lmr3796", "Drawing~");
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
					Log.d("lmr3796", "Drawing2~");
				}
			}catch(JSONException e){
				Log.e("ilmftb3191",e.getStackTrace().toString());
				continue;
			}
		}
        GoogleMapActivity.this.runOnUiThread(new Runnable(){

    		public void run() {
    			// TODO Auto-generated method stub
    			ready = true;
    			int level = Map.getZoomLevel();
    			setOverlay(level,result);
    			status.setText("Touch it!");
    			progress.setVisibility(progress.INVISIBLE);
    		}
    	});

	}
}