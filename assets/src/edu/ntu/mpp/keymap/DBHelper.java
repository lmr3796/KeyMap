package edu.ntu.mpp.keymap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "keyword.db";
	private static final String TBL_NAME_EXP = "Keyword";
	private static final String CREATE_TBL = " create table "
			+ " Keyword(_id integer primary key autoincrement,type text,item text,cost int,receipt int,score float,comment text,date text) ";
	
	private SQLiteDatabase db;

	DBHelper(Context c) {
		super(c, DB_NAME, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL(CREATE_TBL);
	}

	public void insert(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		db.insert(TBL_NAME_EXP, null, values);
		db.close();
	}

	public Cursor query(String today) {
		SQLiteDatabase db = getWritableDatabase();
		//Cursor c = db.rawQuery("SELECT * FROM Expense WHERE date=today",null);
		Cursor c = db.query(TBL_NAME_EXP, null, "date='"+today+"'" , null, null, null, null);
		return c;
	}
	
	public int daily_total(String today){
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(TBL_NAME_EXP, null, "date='"+today+"'" , null, null, null, null);
		int list_num = c.getCount();
		int total = 0;
		c.moveToFirst();
		for(int i=0; i<list_num; i++){
			int temp = c.getInt(3);
			total += temp;
			c.moveToNext();
		}
		
		return total;
	}

	public void del(int id) {
		if (db == null)
			db = getWritableDatabase();
		db.delete(TBL_NAME_EXP, "_id=?", new String[] { String.valueOf(id) });
	}

	public void close() {
		if (db != null)
			db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}