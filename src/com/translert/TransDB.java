package com.translert;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * Designed with One table and extensible rows with just two columns
 * String fields to avoid complexity, App will specify the wrapper for each purpose
 * @
 */
public class TransDB {
	Context cont1; //current context
	protected SQLiteDatabase db;
	private final String DB_NAME = "TransDb";
	private final int DB_VERSION = 1;

	private final String TABLE_NAME = "TransTable";
	private final String TABLE_ROW_ID = "t_id";
	private final String TABLE_ROW_C1 = "t_row_one";
	private final String TABLE_ROW_C2 = "t_row_two";
	
	
	public TransDB(Context context) {
		this.cont1 = context;
		// create or open the database
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
		
	}
	/*
	 * @
	 */
	public int addRow(String c1, String c2) 	{ 
		//holder used by android's SQLite methods
	    ContentValues values = new ContentValues();
		values.put(TABLE_ROW_C1, c1);
		values.put(TABLE_ROW_C2, c2);
		// ask the database object to insert the new data
		try{
			db.insert(TABLE_NAME, null, values);
			return 0;
		}catch(Exception e){
			//Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		return -1;
	}
	
	/* 
	 * @param1: Row ID is assigned permanently for this application
	 * @para2&3: Fields that ignore update if null and do read modify write
	 */
	public void updateRow(long rowID, String rowStringOne, String rowStringTwo){
		//holder used by android's SQLite methods
		ContentValues values = new ContentValues();
		ArrayList<Object> tempArr = null;
		
		if(rowStringOne == null){
			//RMW
			tempArr = getRowAsArray(rowID);
			if(tempArr.size() != 0){
				rowStringOne = (String)tempArr.get(1);
			}
		}
		if(rowStringTwo == null){
			//RMW
			tempArr = getRowAsArray(rowID);
			if(tempArr.size() != 0){
				rowStringTwo = (String)tempArr.get(2);
			}
		}
		values.put(TABLE_ROW_C1, rowStringOne);
		values.put(TABLE_ROW_C2, rowStringTwo);
		
		try {
			db.update(TABLE_NAME, values, TABLE_ROW_ID + "=" + rowID, null);
		} catch (Exception e) {
			//Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}

	
	public ArrayList<Object> getRowAsArray(long rowID) {
		
		ArrayList<Object> rowArray = new ArrayList<Object>();
		Cursor cursor;
		
		try {
			cursor = db.query (TABLE_NAME,
			                   new String[] { TABLE_ROW_ID, TABLE_ROW_C1, TABLE_ROW_C2 },
							   TABLE_ROW_ID + "=" + rowID,
							   null, null, null, null, null);
			// move to zero
			cursor.moveToFirst();
            
			if (!cursor.isAfterLast()) { 
			    do {
					rowArray.add(cursor.getLong(0));
					rowArray.add(cursor.getString(1));
					rowArray.add(cursor.getString(2));
				} while (cursor.moveToNext());
			}
						
			cursor.close();
		} catch (SQLException e) {
			//Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		//containing the given row from the database.
		return rowArray; 	
	}
	
	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper{
	
		public CustomSQLiteOpenHelper(Context context) 
		{
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db){
			// This string is used to create the database.
			
			String newTableQueryString = "create table " +
			     TABLE_NAME + " (" + TABLE_ROW_ID +
				 " integer primary key autoincrement not null," +
				 TABLE_ROW_C1 + " text," + 	
				 TABLE_ROW_C2 + " text" + 	");";
				 //query string to the database.
				 db.execSQL(newTableQueryString);
		}
				 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		    // 
		}		
	}
}
