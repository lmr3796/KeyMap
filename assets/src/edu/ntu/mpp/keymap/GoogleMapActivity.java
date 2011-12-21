package edu.ntu.mpp.keymap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class GoogleMapActivity extends MapActivity implements Runnable{
	//
	private Button search, checkin;
	//
    /** Called when the activity is first created. */
	private CustomMapView Map;
    MapController controller;
    List<Overlay> listOfOverlays;
    MapOverlay mapOverlay;
    JSONArray result = new JSONArray();
    JSONArray result_p = new JSONArray();
    Intent intent;
    GeoPoint csie = new GeoPoint(
			(int) (25.019521057333 * 1000000),
			(int) (121.541764862 * 1000000)
		);
    //加上自己的位置
    GeoPoint loc;
    
    double lat, lng;
    String token;
    boolean load=false;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        //
        intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
		Log.e("Location","lat="+lat+"lng="+lng);
        token = intent.getStringExtra("token");
        Log.e("get_lat","lat="+intent.getDoubleExtra("lat", 0));
        Log.e("get_lng","lng="+intent.getDoubleExtra("lng", 0));
        Log.e("get_token","tok="+intent.getStringExtra("token"));
        //
        loc = new GeoPoint(
    			(int) (lat * 1000000),
    			(int) (lng * 1000000)
    		);
        search = (Button)findViewById(R.id.search);
        checkin = (Button)findViewById(R.id.checkin);
        //
        
        checkin.setOnTouchListener(new Button.OnTouchListener(){
            @Override
           public boolean onTouch(View arg0, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {  //按下的時候改變背景及顏色
                	checkin.setBackgroundResource(R.drawable.pa_on);
                }  
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {  //起來的時候恢復背景與顏色
                	checkin.setBackgroundResource(R.drawable.pa);  
                }  
            return false;
           }
        });
        
    	checkin.setOnClickListener(new OnClickListener() {
    		public void onClick(View v){
    			Intent intent2 = new Intent();	
    			
    			while(!load){
    				
    			}
    			intent2.putExtra("token", token);
    			intent2.putExtra("size", result_p.length());
    			for(int i=0;i<result_p.length();i++){
    				try {
						intent2.putExtra("p"+i, result_p.getJSONObject(i).getString("name"));
						intent2.putExtra("lat"+i, result_p.getJSONObject(i).getString("lat"));
						intent2.putExtra("lng"+i, result_p.getJSONObject(i).getString("long"));
						intent2.putExtra("id"+i, result_p.getJSONObject(i).getString("pid"));
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
    	t.start();
        //
        

     
        
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
			if(result.length() != 0){
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
		case 20:
			listOfOverlays.clear();
			mapOverlay = new MapOverlay(20,2,result);
			listOfOverlays.add(mapOverlay);
			break;
		case 19:
			listOfOverlays.clear();
			mapOverlay = new MapOverlay(15,2,result);
			listOfOverlays.add(mapOverlay);
			break;
		case 18:
			listOfOverlays.clear();
			mapOverlay = new MapOverlay(8,1,result);
			listOfOverlays.add(mapOverlay);
			break;
		default:
			listOfOverlays.clear();
			break;
		}
	}
	@Override
	public void run() {
		//ProgressBar progress = new ProgressBar(this);
		
		
		Server server = new Server();
        try{
            result=server.Search(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lng", 0),
            		intent.getStringExtra("token"),false);
            Log.e("result", result.toString());
            }catch(Exception e){
    		      Log.e("log_tag", e.toString());
    		}
        GoogleMapActivity.this.runOnUiThread(new Runnable(){

    			public void run() {
    				// TODO Auto-generated method stub
    				int level = Map.getZoomLevel();
    		        setOverlay(level,result);
    			}
            	
            });
		//Server server = new Server();
	    try{
            result_p=server.Search(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lng", 0),
            		intent.getStringExtra("token"),true);
            Log.e("result_p", result_p.toString());

			load=true;
            }catch(Exception e){
    		      Log.e("log_tag", e.toString());
    		}
	}
}