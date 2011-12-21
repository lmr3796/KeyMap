package edu.ntu.mpp.keymap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.facebook.android.*;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.facebook.android.SessionStore;

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
				String status = loginButton.getText().toString();
				if(status.equals("Log in")){
				
				facebook.authorize(KeyMap.this, new String[]{"publish_checkins","user_checkins","friends_checkins"},
						new DialogListener(){
							
							public void onComplete(Bundle values){
								//fbAsyncFacebookRunner.request("me/checkins", checkinRequestListener);
								token = facebook.getAccessToken();
								Log.e("token",token);
								save(token);
								loginButton.setText("Log out");
								findloc();
							}
							
							public void onFacebookError(FacebookError error){}
							
							
							public void onError(DialogError e){}
							
							
							public void onCancel(){}
					}
				);
				}
				else{
					fbAsyncFacebookRunner.logout(KeyMap.this, logoutListener);
					save("");
				}
			}
		});
		
	}
    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
        	setTitle("Logging out...");
        }

        public void onLogoutFinish() {
        	setTitle("You have logged out! ");
        }
    }
    
	private void findloc(){
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
	
	private RequestListener logoutListener = new RequestListener(){
		
		public void onMalformedURLException(MalformedURLException e,Object state){
			Log.e("error mal",e.toString());
		}
		
		
		public void onIOException(IOException e, Object state){
			Log.e("error io",e.toString());
		}
		
		
		public void onFileNotFoundException(FileNotFoundException e, Object state){
			Log.e("error file",e.toString());
		}
		
		public void onFacebookError(FacebookError e, Object state){
			Log.e("error fb",e.toString());
		}
		
		public void onComplete(String response, Object state){
			KeyMap.this.runOnUiThread(new Runnable(){
				
				public void run(){
			    	loginButton.setText("Log in");
					Toast.makeText(KeyMap.this, "登出了!", Toast.LENGTH_SHORT).show();
					//textview.setText("Hello world!");
				}
			});
		}
	};
	
	private RequestListener postlistener = new RequestListener(){
		
		public void onMalformedURLException(MalformedURLException e,Object state){
			Log.e("error mal",e.toString());
		}
		
		public void onIOException(IOException e, Object state){
			Log.e("error io",e.toString());
		}
		
		public void onFileNotFoundException(FileNotFoundException e, Object state){
			Log.e("error file",e.toString());
		}
		
		public void onFacebookError(FacebookError e, Object state){
			Log.e("error fb",e.toString());
		}
		
		public void onComplete(String response, Object state){
			KeyMap.this.runOnUiThread(new Runnable(){
				
				public void run(){
					findloc();
					//textview.setText("已傳送訊息至"+item_name);
					//timer.schedule(task, 2000); 
					//setTimer();
					//Toast.makeText(MppFB.this, "上傳成功", Toast.LENGTH_SHORT).show();
				}
			});
		}
	};
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
        SessionStore.restore(facebook, this);
        //SessionEvents.addAuthListener(new SampleAuthListener());
        //SessionEvents.addLogoutListener(new SampleLogoutListener());
        
        Intent intent2 = getIntent();
        if(intent2.hasExtra("p")&&intent2.hasExtra("id")&&intent2.hasExtra("token")){
        	
        	//item_name = intent2.getStringExtra("name");
        	//textview.setText("已傳送訊息至"+item_name);


        	Bundle b = new Bundle();
        	b.putString("method", "POST");
        	b.putString("message",intent2.getStringExtra("text"));
        	b.putString("access_token",intent2.getStringExtra("token"));
        	b.putString("place",intent2.getStringExtra("id"));
        	b.putString("coordinates","{\"latitude\":"+intent2.getStringExtra("lat")+
        					",\"longitude\":"+intent2.getStringExtra("lng")+"}");
        	Log.e("b",b.toString());
        	fbAsyncFacebookRunner.request("me/checkins",b,postlistener);
        
        }
        
    }
    
    private void save(String token){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	prefs.edit().putString("Token", token).commit();
    	}
}