package edu.ntu.mpp.keymap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class GoogleMapActivity extends MapActivity implements Runnable{
	//
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
    
    double lat, lng;
    String token;
    boolean load=false;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        setTitle("KeyMap");
        //
        intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
		Log.e("Location","lat="+lat+"lng="+lng);
        token = intent.getStringExtra("token");
        Log.e("get_lat","lat="+intent.getDoubleExtra("lat", 0));
        Log.e("get_lng","lng="+intent.getDoubleExtra("lng", 0));
        Log.e("get_token","tok="+intent.getStringExtra("token"));

        loc = new GeoPoint(
    			(int) (lat * 1000000),
    			(int) (lng * 1000000)
    		);
        refresh = (Button)findViewById(R.id.refresh);
        checkin = (Button)findViewById(R.id.checkin);
        status = (TextView)findViewById(R.id.info);
        progress = (ProgressBar)findViewById(R.id.progressBar1);

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
        
        refresh.setOnClickListener(new OnClickListener() {
    		public void onClick(View v){
    			//Toast.makeText(GoogleMapActivity.this, "Please wait..", Toast.LENGTH_LONG).show();
    			Log.e("center",Map.getMapCenter().toString());
    			GeoPoint center = Map.getMapCenter();
    			lat = (double)center.getLatitudeE6() / 1000000;
    			lng = (double)center.getLongitudeE6()/ 1000000;
    			
    			Thread t=new Thread(GoogleMapActivity.this);
    	    	status.setText("Loading text...");
    	    	t.start();
    		}
    	});
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
    	
        findViews();
        setupMap();
        Map.setOnPanListener(listener);
        listOfOverlays = Map.getOverlays();
        listOfOverlays.clear();
        
    	Thread t=new Thread(this);
    	status.setText("Loading text...");
    	t.start();
        
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
			if(ready == true){
				setOverlay(level,result);
			}
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
		
		places = fMiner.getPlaceID( lat, lng);
		int [] check = new int[places.length()];
		Log.e("places",places.toString());
		
		
		// Get ilmftb's stuff
		for(int i = 0 ; i < check.length ; i++)
			check[i] = 0;
		for(int i = 0 ; i < places.length() ; i++){
			if(check[i] == 1)
				continue;
			try{
				location = (JSONObject) places.get(i);
				allcheckin = fMiner.getAllCheckins(location.getString("id"));
			
				if(allcheckin.length() == 0){
					check[i] = 1;
					continue;
				}
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