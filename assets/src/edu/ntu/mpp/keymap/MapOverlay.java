package edu.ntu.mpp.keymap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MapOverlay extends Overlay {
	private int maxSize;
	private int maxLayer;
	JSONArray result;
	JSONArray test;
	GeoPoint csie;
	MapOverlay(int size,int layer ,JSONArray input){
    	maxSize = size;
    	maxLayer = layer;
    	try {
    		result = input;
			test = input.getJSONObject(1).getJSONArray("kw");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	csie = new GeoPoint(
    			(int) (25.019521057333 * 1000000),
    			(int) (121.541764862 * 1000000)
    	);
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