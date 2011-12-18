package edu.ntu.mpp.keymap;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CheckIn extends Activity{
	private Button back;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        Intent intent = getIntent();
        back = (Button)findViewById(R.id.back);
        /*
    	back.setOnClickListener(new OnClickListener() {
    		public void onClick(View v){
    			Intent intent = new Intent();
            	intent.setClass(CheckIn.this, MapIndex.class);
            	startActivity(intent);
    		}
    	});
    	*/
    }
    
}
