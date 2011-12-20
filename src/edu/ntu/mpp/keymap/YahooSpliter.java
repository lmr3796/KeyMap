package edu.ntu.mpp.keymap;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class YahooSpliter {
	private static final String URL="http://asia.search.yahooapis.com/cas/v1/ke";
	private static final String APP_ID= "Tsvd7E3V34FE2Iva.bjSC0gdXsY.3KA4KMR3sRpHiradFSNyZ5wonLrX79u38NocIQCGqA--";
	
	private static HttpClient httpclient = new DefaultHttpClient();
	private static HttpResponse rp = null;
	public YahooSpliter(){
		
	}
	public ArrayList<String> split(String text){
		try{	
			HttpPost post = new HttpPost(URL);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("format","json"));
			nvps.add(new BasicNameValuePair("appid",APP_ID));
			nvps.add(new BasicNameValuePair("content",text));
			nvps.add(new BasicNameValuePair("threshold","100"));
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			rp = httpclient.execute(post);
			if(rp.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
				throw new HttpException("Accessing Yahoo API Error.");
			}
			ArrayList<String> result = new ArrayList<String>();
			String rpStr = EntityUtils.toString(rp.getEntity());
			Log.e("response", rpStr);
			JSONArray w = new JSONArray(rpStr);
			for(int i = 0 ; i < w.length() ; i++){
				result.add(w.getJSONObject(i).getString("token"));
			}
			return result;
			
		}catch(JSONException e){
			Log.e("lmr3796", "Error in JSON ", e);
			return null;
		}catch(HttpException e){
			Log.e("lmr3796", "Error in http connection ", e);
			return null;
		}catch(Exception e){
			Log.e("lmr3796", "Unknown Exception", e);
			return null;
		}
	}

}
