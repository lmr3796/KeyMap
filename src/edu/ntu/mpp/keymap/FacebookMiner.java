package edu.ntu.mpp.keymap;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class FacebookMiner {
	AsyncFacebookRunner fbAsyncFacebookRunner;
	public FacebookMiner(Facebook f){
		fbAsyncFacebookRunner = new AsyncFacebookRunner(f);
	}
	public FacebookMiner(AsyncFacebookRunner a) {
		fbAsyncFacebookRunner = a;
	}
	public String[] getPlaces(double lat, double lng){
		return null;
	}
	public String[] getCheckins(String placeID){
		return null;
	}
};
