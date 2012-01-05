package edu.ntu.mpp.keymap;

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
	JSONArray result;
	//JSONArray test;
	//GeoPoint csie;
	MapOverlay(int size, int layer, JSONArray input){
    	reset(size, layer, input);
    }
	public void reset(int size, int layer, JSONArray input){
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
        	touchMode = NONE;//终止
        	break;
        }
        return false;		
    }
	@Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        super.draw(canvas, mapView, shadow);
        Projection projection = mapView.getProjection();
        Point screenPts = new Point();
        
        JSONObject location;
		JSONArray data;
		
		try {
			for(int i = 0; i < result.length() ; i++){
				canvas.save();
				location = result.getJSONObject(i);
				data = location.getJSONArray("kw");
				GeoPoint add = new GeoPoint(
						(int)( location.getDouble("lat")*1000000),
						(int)( location.getDouble("lng")*1000000)
				);
				
				projection.toPixels(add, screenPts);
				canvas.translate(screenPts.x, screenPts.y);
		        Keyword keycloud = new Keyword();
		        for(int j = 0 , size = maxSize; j < maxLayer ; j++ ,size/=2){
		        	if(data.getJSONArray(j).length() == 0)
		        		break;
					keycloud.draw(canvas,size, data.getJSONArray(j).length() ,data.getJSONArray(j));
		        }
				canvas.restore();
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        return true;
    }
}
