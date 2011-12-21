package edu.ntu.mpp.keymap;

import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SelectPlace  extends Activity {
		private ListView listview;
		ArrayList<HashMap<String, Object>> listItem;
		Intent intent;
		SimpleAdapter listItemAdapter;
		
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.checkin);
	        listview=(ListView)findViewById(R.id.listView1);
	        
	        intent = getIntent();
	        
	    	listItem = new ArrayList<HashMap<String, Object>>();
	    	if(intent.getIntExtra("size",0)==0){
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("Name", "Near-by place not found");
	    		map.put("lat", "0");
	    		map.put("lng", "0");
	    		listItem.add(map);
	    		listview.setClickable(false);
	    	}
	    	for(int i=0;i<intent.getIntExtra("size", 0);i++){
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("Name", intent.getStringExtra("p"+i));
	    		map.put("lat", intent.getStringExtra("lat"+i));
	    		map.put("lng", intent.getStringExtra("lng"+i));
	    		listItem.add(map);
	    	}  
	    	listItemAdapter = new SimpleAdapter(this,listItem,R.layout.place,  
	        new String[] {"Name"},   
	          new int[] {R.id.name}
	    	);    
	    	listview.setAdapter(listItemAdapter); 
	    	
	    	
	        //點擊list
	        listview.setOnItemClickListener(new OnItemClickListener	() {  
	            @Override  
	            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
	                    long arg3) {  
	           
	            	HashMap<String, Object> map2 = (HashMap<String, Object>)listview.getItemAtPosition(arg2);
	            	//setTitle("選取了" + map2.get("Name"));
	 
	            	Intent intent2 = new Intent();
	            	intent2.putExtra("token", intent.getStringExtra("token"));
					intent2.putExtra("p", intent.getStringExtra("p"+arg2));
					intent2.putExtra("lat", intent.getStringExtra("lat"+arg2));
					intent2.putExtra("lng", intent.getStringExtra("lng"+arg2));
					intent2.putExtra("id", intent.getStringExtra("id"+arg2));
					//Log.e("lng",intent.getStringExtra("lng"+arg2));
	    			intent2.setClass(SelectPlace.this, CheckIn.class);
	            	startActivity(intent2);
	            	finish();
	            }
	        });
	        
	        
	   }
}
