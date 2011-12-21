package edu.ntu.mpp.keymap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

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
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
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
								/*
								FacebookMiner facebookMiner = new FacebookMiner(facebook);
								Splitter yahoo = new YahooSplitter();
								ArrayList<String> test1 = facebookMiner.getPlaceID(25.019047,121.5417528);
								String page_id = test1.get(1);
								Log.e("lmr3796", "page_id: " + page_id);
								ArrayList<String> test2 = facebookMiner.getCheckins(page_id);
								String jizz = "";
								for(int i = 0 ;  i < test2.size() ; i++){
									Log.e("lmr3796", test2.get(i));
									jizz = jizz + test2.get(i) + "\n";
								}
								ArrayList<ArrayList<String>> kw = yahoo.split(jizz);
								for(int i = 0 ; i < kw.get(0).size() ;i++)
									Log.e("lmr3796",kw.get(0).get(i));
								*/
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
					Toast.makeText(KeyMap.this, "�n�X�F!", Toast.LENGTH_SHORT).show();
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
					//textview.setText("�w�ǰe�T����"+item_name);
					//timer.schedule(task, 2000); 
					//setTimer();
					//Toast.makeText(MppFB.this, "�W�Ǧ��\", Toast.LENGTH_SHORT).show();
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
        	//textview.setText("�w�ǰe�T����"+item_name);


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