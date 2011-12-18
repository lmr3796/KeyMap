package edu.ntu.mpp.keymap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.android.*;
import com.facebook.android.Facebook.DialogListener;
public class KeyMap extends Activity {
	static Facebook facebook = new Facebook("307831019240147");
	static AsyncFacebookRunner fbAsyncFacebookRunner = new	AsyncFacebookRunner(facebook);
	private Button loginButton;
	private String provider;
	private LocationManager locationManager;
	Intent intent;
	String token = "";
	
	private void setListener(){
		loginButton.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				//String status = loginButton.getText().toString();
				//if(status.equals("Log in")){
				
				facebook.authorize(KeyMap.this, new String[]{"publish_checkins","user_checkins","friends_checkins"},
						new DialogListener(){
							
							public void onComplete(Bundle values){
								//fbAsyncFacebookRunner.request("me/checkins", checkinRequestListener);
								token = facebook.getAccessToken();
								Log.e("token",token);
								save(token);
								Criteria criteria = new Criteria();
								provider = locationManager.getBestProvider(criteria, false);
								Location location = locationManager.getLastKnownLocation(provider); 
								Double own_lat = location.getLatitude();
								Double own_lng = location.getLongitude();
								Log.e("Location","lat="+own_lat+"lng="+own_lng);
								
				            	intent = new Intent();
				            	intent.setClass(KeyMap.this, GoogleMapActivity.class);
				            	intent.putExtra("lat",own_lat);
				            	intent.putExtra("lng",own_lng);
				            	intent.putExtra("token", token);
								KeyMap.this.runOnUiThread(new Runnable(){
									
									public void run(){
						            	startActivity(intent);
									}
								});
							}
							
							public void onFacebookError(FacebookError error){}
							
							
							public void onError(DialogError e){}
							
							
							public void onCancel(){}
					}
				);
				//}
			}
		});
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		//Log.e("ya", "123@");
		facebook.authorizeCallback(requestCode, resultCode, data);
    	if (!facebook.isSessionValid()) {
    		facebook.setAccessToken(null);
    		facebook.setAccessExpires(0);
    		facebook.authorizeCallback(requestCode, resultCode, data);
    	}
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        loginButton = (Button)findViewById(R.id.login);
        setListener();

    }
    
    private void save(String token){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	prefs.edit().putString("Token", token).commit();
    	}
}