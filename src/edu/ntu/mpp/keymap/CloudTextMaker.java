package edu.ntu.mpp.keymap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CloudTextMaker{
	private int focusRange;
	private double focusLat, focusLng;
	private JSONArray allPlaces;
	private FacebookMiner miner;
	private Splitter splitter;
	private Queue<Cloud> fetchedCloud = new LinkedList<Cloud>(); 
	private SQLiteDatabase db;
	
	private class CheckInDBHlp extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "checkins";
		private static final int DATABASE_VERSION = 3;
		public CheckInDBHlp(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		/**
		 ****************
		 **Table Schema**
		 ****************
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 * Place:
		 *		INTEGER page_id, REAL lat, REAL lng, TEXT name, TEXT cloud
		 *				^Primary Key 
		 *
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 * Checkin:
		 * 		INTEGER cid, INTEGER page_id, TEXT keyword
		 * 				^Primary Key	^References Place:page_id
		 * 
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 */
			
		@Override
		public void onCreate(SQLiteDatabase db) {
			final String CREATE_PLACE = 
					"CREATE TABLE Place (" +
							"page_id INTEGER PRIMARY KEY NOT NULL, " +
							"lat REAL, " +
							"lng REAL, " +
							"name TEXT," +
							"cloud TEXT" +
					");";
			final String CREATE_CHECKIN = 
					"CREATE TABLE Checkin (" +
							"cid INTEGER PRIMARY KEY NOT NULL, " +
							"page_id INTEGER NOT NULL REFERENCES Place ON DELETE CASCADE, " +
							"keyword TEXT" +
					");";
			db.execSQL(CREATE_PLACE);
			//db.execSQL(CREATE_CHECKIN);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int olderVersion, int newerVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS Place");	//刪除舊有的資料表
			//db.execSQL("DROP TABLE IF EXISTS Checkin");	//刪除舊有的資料表
			onCreate(db);
		}
		@Override
		public void onOpen(SQLiteDatabase db){
			super.onOpen(db);
			db.execSQL("DROP TABLE IF EXISTS Place");	//刪除舊有的資料表
			onCreate(db);
		}
		/**
		 * Check if the input places processed or not
		 * Insert it into DB if not processed 
		 * @param place JSONObject indicates the place
		 * @return true if exists 
		 */
		public boolean existPlace(JSONObject place) throws JSONException{
			//If exists, return false to indicate it.
			Cursor c = db.query("Place", new String[]{"page_id"}, "page_id='"+place.getString("id")+"'", null, null, null, null);
			if(c.getCount() > 0)
				return true;
			return false;
		}
		public void insertPlace(JSONObject place, String aggregatedKeyWords) throws JSONException{
			ContentValues cv = new ContentValues();
			cv.put("page_id", Long.parseLong(place.getString("id")));
			cv.put("lat", Double.parseDouble(place.getString("lat")));
			cv.put("lng", Double.parseDouble(place.getString("lng")));
			cv.put("name", place.getString("name"));
			cv.put("cloud", aggregatedKeyWords);
			long i = db.insert("Place", null, cv);
			Log.d("lmr3796", "Insert place: " +(( i == -1 )?"Error" :cv.getAsString("name")));
			Log.d("lmr3796", aggregatedKeyWords);
		}
		public ArrayList<Cloud> getCloud(ArrayList<String> placeIDList){
			ArrayList<Cloud> result = new ArrayList<Cloud>();
			for(int i = 0 ; i < placeIDList.size() ; i++){
				Cursor c = db.query("Place", null, "page_id='"+placeIDList.get(i)+"'", null, null, null, null);
				if(!c.moveToFirst()) continue;
				Cloud cloud = new Cloud();
				cloud.setID(c.getLong(c.getColumnIndex("page_id")));
				cloud.setLat(c.getDouble(c.getColumnIndex("lat")));
				cloud.setLng(c.getDouble(c.getColumnIndex("lng")));
				cloud.setName(c.getString(c.getColumnIndex("name")));
				String jizz = c.getString(c.getColumnIndex("cloud"));
				if(jizz == null)
					continue;
				String[] text = jizz.split("\n");
				for(int j = 0 ; j < text.length ; j++){
					if(text[j].trim().length() > 0)
						cloud.addKeyWord(text[j].trim());
				}
				result.add(cloud);
			}
			return result;
		}
	}
	public void closeDB(){
		//synchronized (keepFetching) {
			keepFetching.set(false);
		//}
		db.close();
		db = null;
	}
	private CheckInDBHlp dbHlp;
	public CloudTextMaker(FacebookMiner m, Splitter s, Context context){
		focusRange = FacebookMiner.DEFAULT_RANGE; 
		miner = m;
		splitter = s;
		dbHlp = new CheckInDBHlp(context);
		db = dbHlp.getWritableDatabase();
	}
	public void refresh(double lat, double lng){
		stopFetching();
		setFocus(lat, lng);
		startFetching();
	}
	public void setFocus(double lat, double lng){
		focusLat = lat;
		focusLng = lng;
	}
	public ArrayList<Cloud> getCloud(double lat, double lng, int range){
		ArrayList<String> placeIDList = new ArrayList<String>();
		JSONArray p = miner.getPlaceID(lat, lng, range);
		if(p == null)
			return new ArrayList<Cloud>();
		for(int i = 0 ; i < p.length() ; i++){
			try{
				placeIDList.add(((JSONObject)p.get(i)).getString("id"));
			}catch(JSONException e){
				Log.e("lmr3796", "Get page_id error.", e);
				continue;
			}
		}
		return dbHlp.getCloud(placeIDList);
	}
	public ArrayList<Cloud> getCloud(double lat, double lng){
		return getCloud(lat, lng, FacebookMiner.DEFAULT_RANGE);
	}
	public class Lock{
		private boolean run = false;
		public void set(boolean b){run = b;}
		public boolean keep(){return run;}
	} 
	public Lock keepFetching = new Lock();
	public void genCloud(){genCloud(false);}
	public void genCloud(boolean update){
		//synchronized (keepFetching) {
			keepFetching.set(true);
			allPlaces= miner.getPlaceID(focusLat, focusLng, focusRange);
			if(allPlaces == null){
				keepFetching.set(false);
				return;
			}
		//}
		for(int i = 0 ; keepFetching.keep() && i < allPlaces.length() ; i++){
			long page_id;
			JSONObject location;
			String allCheckin;
			// Fetch checkins of the place
			try{
				location = (JSONObject) allPlaces.get(i);
				page_id = Long.parseLong(location.getString("id"));
				// Escape processed locations 
				if(dbHlp.existPlace(location) && !update)
					continue;
			}catch (JSONException e){
				Log.e("lmr3796", "Error getting page_id.",e);
				continue;
			}
			if(!keepFetching.keep())
				break;
			allCheckin = miner.getAllCheckins(Long.toString(page_id));
			// Insert it to the database
			try{
				String aggregatedKeyWords = (allCheckin.length() == 0) ? "" : aggregateKeyWords(allCheckin);
				dbHlp.insertPlace(location, aggregatedKeyWords);
			}catch(JSONException e){
				Log.e("CloudTextMaker", "Error aggregating key words.", e);
				continue;
			}
			try{
				Thread.sleep(200);
			}catch(InterruptedException e){
				continue;
			}
		}
		stopFetching();
		return;
	}
	private Thread t;
	public void startFetching(){
		keepFetching.set(true);
		work = true;
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CloudTextMaker.this.genCloud();
			}
		});
		t.start();
	}
	public void stopFetching(){
		keepFetching.set(false);
		work = false;
	}
	private boolean work = false;
	public boolean working(){
		return work;
	}
	private String aggregateKeyWords(String allCheckin) throws JSONException{
		JSONArray keyWordArr = splitter.split(allCheckin);
		return aggregateKeyWords(keyWordArr);
	}
	private String aggregateKeyWords(JSONArray keyWordArr) throws JSONException{
		if(keyWordArr == null)
			return "";
		String allKeyWords = "";
		for(int j = 0 ; j < keyWordArr.length() ; j++)
			for(int k = 0; k < keyWordArr.getJSONArray(j).length() ; k++)
				allKeyWords = allKeyWords + keyWordArr.getJSONArray(j).getString(k) + "\n";
		return allKeyWords;
	}
	
};
