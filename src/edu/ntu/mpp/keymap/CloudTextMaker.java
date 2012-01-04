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

public class CloudTextMaker extends Thread{
	private int focusRange;
	private double focusLat, focusLng;
	private JSONArray allPlaces;
	private FacebookMiner miner;
	private Splitter splitter;
	private Queue<Cloud> fetchedCloud = new LinkedList<Cloud>(); 
	private SQLiteDatabase db;
	
	private class CheckInDBHlp extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "checkins";
		private static final int DATABASE_VERSION = 2;
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
			db.execSQL("DROP TABLE IF EXISTS Checkin");	//刪除舊有的資料表
			onCreate(db);
		}
		
		/**
		 * Check if the input places processed or not
		 * Insert it into DB if not processed 
		 * @param place JSONObject indicates the place
		 * @return true if exists 
		 */
		public boolean recordPlace(JSONObject place) throws JSONException{
			//If exists, return false to indicate it.
			Cursor c = db.query("Place", new String[]{"page_id"}, "page_id='"+place.getString("id")+"'", null, null, null, null);
			if(c.getCount() > 0)
				return false;
			insertPlace(place);
			return true;
		}
		public void insertPlace(JSONObject place) throws JSONException{
			ContentValues cv = new ContentValues();
			cv.put("page_id", Long.parseLong(place.getString("id")));
			cv.put("lat", Double.parseDouble(place.getString("lat")));
			cv.put("lng", Double.parseDouble(place.getString("lng")));
			cv.put("name", place.getString("name"));
			long i = db.insert("Place", null, cv);
			Log.d("lmr3796", "Insert place: " +(( i == -1 )?"Error" :cv.getAsString("name")));
		}
		public ArrayList<Cloud> getCloud(ArrayList<String> placeIDList){
			ArrayList<Cloud> result = new ArrayList<Cloud>();
			for(int i = 0 ; i < placeIDList.size() ; i++){
				Cursor c = db.query("Place", null, "page_id='"+placeIDList.get(i)+"'", null, null, null, null);
				if(!c.moveToFirst()) continue;
				Cloud cloud = new Cloud();
				cloud.setID(c.getLong(0));
				cloud.setLat(c.getDouble(1));
				cloud.setLng(c.getDouble(2));
				cloud.setName(c.getString(3));
				String[] text = c.getString(4).split("\n");
				for(int j = 0 ; j < text.length ; j++){
					if(text[j].trim().length() > 0)
						cloud.addKeyWord(text[j].trim());
				}
				result.add(cloud);
			}
			return result;
		}
	}
	private CheckInDBHlp dbHlp;
	public CloudTextMaker(FacebookMiner m, Splitter s, Context context){
		focusRange = FacebookMiner.DEFAULT_RANGE; 
		miner = m;
		splitter = s;
		dbHlp = new CheckInDBHlp(context);
		db = dbHlp.getWritableDatabase();
	}
	public void setFocus(double lat, double lng){
		focusLat = lat;
		focusLng = lng;
	}
	
	public ArrayList<Cloud> getCloud(double lat, double lng, int range){
		ArrayList<String> placeIDList = new ArrayList<String>();
		JSONArray p = miner.getPlaceID(lat, lng, range);
		for(int i = 0 ; i < p.length() ; i++){
			try{
				placeIDList.add(((JSONObject)p.get(i)).getString("id"));
			}catch(JSONException e){
				Log.e("lmr3796", "Get page_id error.");
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
	public void genCloud(){
		keepFetching.set(true);
		JSONArray allPlaces= miner.getPlaceID(focusLat, focusLng, focusRange);
		for(int i = 0 ; keepFetching.keep() && i < allPlaces.length() ; i++){
			//synchronized (keepFetching) {
				long page_id;
				JSONObject location;
				String allCheckin;
				// Fetch checkins of the place
				try{
					location = (JSONObject) allPlaces.get(i);
					page_id = Long.parseLong(location.getString("id"));
					// Escape gained checkins
					if(!dbHlp.recordPlace(location))
						continue;
				}catch (JSONException e){
					Log.e("lmr3796", "Error getting page_id.");
					Log.e("lmr3796", e.getStackTrace().toString());
					continue;
				}
				allCheckin = miner.getAllCheckins(Long.toString(page_id));
				// Insert it to the database
				try{
					String aggregatedKeyWords = (allCheckin.length() == 0) ? "" : aggregateKeyWords(allCheckin);
					ContentValues cv = new ContentValues();
					cv.put("cloud", aggregatedKeyWords);
					int jizz = db.update("Place", cv, "page_id="+Long.toString(page_id), null);	// check return value == 1???
					Log.d("lmr3796", "Update keywords: "+((jizz == 1)?Long.toString(page_id):"Error"));
				}catch(JSONException e){
					Log.e("lmr3796", "Error aggregating key words.");
					Log.e("lmr3796", e.getStackTrace().toString());
					continue;
				}
			//}
		}
		keepFetching.set(false);
		return;
	}
	public void stopFetching(){
		//synchronized (keepFetching) {
			keepFetching.set(false);
		//}
	}
	private String aggregateKeyWords(String allCheckin) throws JSONException{
		JSONArray keyWordArr = splitter.split(allCheckin);
		return aggregateKeyWords(keyWordArr);
	}
	private String aggregateKeyWords(JSONArray keyWordArr) throws JSONException{
		String allKeyWords = "";
		for(int j = 0 ; j < keyWordArr.length() ; j++)
			for(int k = 0; k < keyWordArr.getJSONArray(j).length() ; k++)
				allKeyWords = allKeyWords + keyWordArr.getJSONArray(j).getString(k) + "\n";
		return allKeyWords;
	}
	@Override
	public void run() {
		genCloud();
	}
};
