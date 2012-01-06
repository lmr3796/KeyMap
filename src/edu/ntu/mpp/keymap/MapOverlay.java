package edu.ntu.mpp.keymap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MapOverlay extends Overlay {
	private int maxSize;
	private int maxLayer;
	private int touchMode;
	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DOWN = 1;
	static final int MOVE = 2;
	HashMap<Long, Cloud> result;
	//JSONArray test;
	//GeoPoint csie;
	MapOverlay(int size, int layer, HashMap<Long, Cloud> input){
    	reset(size, layer, input);
    }
	public void reset(int size, int layer, HashMap<Long, Cloud> input){
		maxSize = size;
    	maxLayer = layer;
    	result = input;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapview){
        // Handle touch events here...
        switch (event.getAction()) {
       	case MotionEvent.ACTION_MOVE:
        	touchMode = MOVE;
            break;
        case MotionEvent.ACTION_DOWN:
        	touchMode = DOWN;
            break;
        case MotionEvent.ACTION_UP:
       		switch(touchMode){
       		case MOVE:
       			Log.d("lmr3796", "Move");
       			((GoogleMapActivity)mapview.getContext()).refreshCloudOnMap();
       			break;
       		case DOWN:
       			//Log.e("lmr3796", "Press");
       			break;
       		default:
       			break;
       		}
       	case MotionEvent.ACTION_CANCEL:
        default:
        	touchMode = NONE;
        	break;
        }
        return false;		
    }
	@Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        super.draw(canvas, mapView, shadow);
        Projection projection = mapView.getProjection();
        Point screenPts = new Point();
       /* 
        JSONObject location;
        JSONObject near;
		JSONArray data;
		*/
        for ( Long key : result.keySet()) {
        	Cloud mergedCloud = result.get(key);
        	for( Long key2 : result.keySet()){
        		Cloud near = result.get(key2);
        		if(Distance.dist(mergedCloud.getLat(), mergedCloud.getLng(), near.getLat(), near.getLng()) > 20)
        			continue;
        		mergedCloud.join(near);
        		result.remove(key2);
        	}
        }
        for ( Long key : result.keySet()){
        	canvas.save();
        	Cloud location = result.get(key);
        	ArrayList<ArrayList<String>> data = location.getTieredKeyWords();
        	GeoPoint add = new GeoPoint(
        			(int)( location.getLat()*1000000),
        			(int)( location.getLng()*1000000)
        			);

        	projection.toPixels(add, screenPts);
        	canvas.translate(screenPts.x, screenPts.y);
        	Keyword keycloud = new Keyword();
        	for(int j = 0 , size = maxSize; j < maxLayer ; j++ ,size/=2){
        		if(data.get(j).size() == 0)
        			break;
        		keycloud.draw(canvas,size, data.get(j).size() ,data.get(j));
        	}
        	canvas.restore();

        }
        
        
        return true;
    }
}
