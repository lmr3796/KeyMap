package edu.ntu.mpp.keymap;

import java.math.BigDecimal;

import org.json.JSONArray;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MapIndex extends Activity{
	private Button search, checkin;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        Intent intent = getIntent();
        Log.e("get_lat","lat="+intent.getDoubleExtra("lat", 0));
        Log.e("get_lng","lng="+intent.getDoubleExtra("lng", 0));
        Log.e("get_token","tok="+intent.getStringExtra("token"));
        
        search = (Button)findViewById(R.id.search);
        checkin = (Button)findViewById(R.id.checkin);
        
    	checkin.setOnClickListener(new OnClickListener() {
    		public void onClick(View v){
    			Intent intent = new Intent();
            	intent.setClass(MapIndex.this, CheckIn.class);
            	startActivity(intent);
    		}
    	});
        
        Server server = new Server();
        try{
        JSONArray result=server.Search(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lng", 0),
        		intent.getStringExtra("token"),false);
        }catch(Exception e){
		      Log.e("log_tag", e.toString());
		}
    }
    
}
