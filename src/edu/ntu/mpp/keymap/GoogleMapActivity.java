package edu.ntu.mpp.keymap;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;

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
    Intent intent;
    GeoPoint csie = new GeoPoint(
			(int) (25.019521057333 * 1000000),
			(int) (121.541764862 * 1000000)
		);
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        //
        intent = getIntent();
        Log.e("get_lat","lat="+intent.getDoubleExtra("lat", 0));
        Log.e("get_lng","lng="+intent.getDoubleExtra("lng", 0));
        Log.e("get_token","tok="+intent.getStringExtra("token"));
        
        search = (Button)findViewById(R.id.search);
        checkin = (Button)findViewById(R.id.checkin);
        
    	checkin.setOnClickListener(new OnClickListener() {
    		public void onClick(View v){
    			Intent intent = new Intent();
            	intent.setClass(GoogleMapActivity.this, CheckIn.class);
            	startActivity(intent);
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
    	controller.setZoom(17);
    	controller.animateTo(csie);
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
	public void run() {
		Server server = new Server();
        try{
            result=server.Search(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lng", 0),
            		intent.getStringExtra("token"));
            Log.e("result", result.toString());
            }catch(Exception e){
    		      Log.e("log_tag", e.toString());
    		}
        /*
        Map.setOnPanListener(listener);
        listOfOverlays = Map.getOverlays();
        listOfOverlays.clear();*/
        GoogleMapActivity.this.runOnUiThread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				int level = Map.getZoomLevel();
		        setOverlay(level,result);
			}
        	
        });
	}
}