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
	private HashSet<Long> renderedPlaces = new HashSet<Long>();
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
			Log.e("lmr3796", "refresh");
			cloudTextMaker.refresh(lat, lng);
			Log.e("lmr3796", "end refresh");
			Thread t = new Thread(GoogleMapActivity.this);
	    	status.setText("Loading text...");
	    	t.start();
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
		cloudTextMaker = new CloudTextMaker(new FacebookMiner(KeyMap.facebook), new YahooSplitter(), this);
    }
	
	@Override
    public void onResume() {
		super.onResume();
        refreshCloudOnMap();
	}
	@Override
    public void onPause() {
		super.onPause();
		//cloudTextMaker.closeDB();
		cloudTextMaker = null;
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
			//Log.e("result", result.toString());
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
		listOfOverlays.clear();
		switch(level){
		case 21:
			mapOverlay = new MapOverlay(30,2,result);
			listOfOverlays.add(mapOverlay);
			break;
		case 20:
			mapOverlay = new MapOverlay(23,2,result);
			listOfOverlays.add(mapOverlay);
			break;
		case 19:
			mapOverlay = new MapOverlay(15,1,result);
			listOfOverlays.add(mapOverlay);
			break;
		case 18:
			mapOverlay = new MapOverlay(10,1,result);
			listOfOverlays.add(mapOverlay);
			break;
		default:
			break;
		}
	}
	private int getClusterThreshold(){
		// TODO: Determine clustering threshold by zoom level
		return 50;
	}
	public void run() {
		while(cloudTextMaker != null && cloudTextMaker.working()){
			ArrayList<Cloud> cloudList = cloudTextMaker.getCloud(lat, lng);
			ArrayList<Cloud> cloudToDraw = new ArrayList<Cloud>();
			// Group up places that are too close
			for(int i = 0 ; i < cloudList.size() ; i++){
				Cloud currCloud = cloudList.get(i); 
				if(renderedPlaces.contains(cloudList.get(i).getID()))
					continue;
				for(int j = i+1 ; j < cloudList.size() ; j++){
					Cloud near = cloudList.get(j);
					if(Distance.dist(currCloud.getLat(), currCloud.getLng(), near.getLat(), near.getLng())
							< getClusterThreshold()){	// Too close 
						currCloud.join(near);
						renderedPlaces.add(near.getID());
						cloudList.remove(j);
					}
				}
				renderedPlaces.add(currCloud.getID());
				if(currCloud.getKeyWords().size() > 0)
					cloudToDraw.add(currCloud);
			}
			//result = Cloud.listToJSONArray(cloudToDraw);
			try{
				JSONArray result2 = Cloud.listToJSONArray(cloudToDraw);
				for(int i = 0 ; i<result2.length() ; i++)
					result.put(result2.get(i));
			}catch(JSONException e){
				Log.e("lmr3796", "adding result", e);
			}
			
			//Log.d("lmr3796","Result: " + result.toString());
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
			try{
				Thread.sleep(2000);
			}catch (InterruptedException e){
				continue;
			}
		}
	}
}