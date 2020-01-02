package postpone.the.inevitable.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

//Helper class to access the levels that are stored in a sql database
public class LevelDataSource {

	// Database fields
	private SQLiteDatabase database;
	private final SQLiteHelper dbHelper;
	private final static String[] allColumns = { 
			SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_COMPLETED, 
			SQLiteHelper.COLUMN_BEST_TIME, 
			SQLiteHelper.COLUMN_COMPLETION_DATE, 
			SQLiteHelper.COLUMN_MAP, 
			SQLiteHelper.COLUMN_MAP_SOLUTION, 
			SQLiteHelper.COLUMN_TARGET_TIME, 
			SQLiteHelper.COLUMN_CURRENCY1, 
			SQLiteHelper.COLUMN_CURRENCY2, 
		};
	
	//Contains the number of available maps
	public static int NBR_OF_MAPS = 0;
	
	public LevelDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	//Open database connection
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		if (NBR_OF_MAPS == 0) {
			NBR_OF_MAPS = getNumberOfLevels(database);
		}
	}

	//Close database connection
	public void close() {
		dbHelper.close();
	}
	
	//Get all levels
	public ArrayList<Level> getAllLevels() {
		final ArrayList<Level> retValue = new ArrayList<Level>();
		
		final Cursor mCursor = database.query(SQLiteHelper.TABLE_LEVEL_DATA,
				allColumns, null, null, null, null, SQLiteHelper.COLUMN_ID + " ASC");
		
		if (mCursor != null) {
	        for(mCursor.moveToFirst();!mCursor.isAfterLast(); mCursor.moveToNext()) {
	        	retValue.add(createLevelObjectFromCursor(mCursor));
			}
		}
		
        mCursor.close();        		
		return retValue;
	}

	//Get a specific level
	public Level getSpecificLevels(int id) {
		final Cursor mCursor = database.query(SQLiteHelper.TABLE_LEVEL_DATA,
				allColumns, SQLiteHelper.COLUMN_ID + "=?" , new String[]{""+id}, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
	    	final Level tmp = createLevelObjectFromCursor(mCursor);
	    	mCursor.close();
	    	return tmp;
		}
		return null;
	}

	//Simple method converting a cursor to a level object
	private Level createLevelObjectFromCursor(Cursor mCursor) {
    	//SQLiteHelper.COLUMN_ID
    	final int id = mCursor.getInt(0);
		//SQLiteHelper.COLUMN_COMPLETED,
    	final boolean completed = mCursor.getInt(1) == 1? true: false;
		//SQLiteHelper.COLUMN_BEST_TIME,
    	final double time = mCursor.getDouble(2);
		//SQLiteHelper.COLUMN_COMPLETION_DATE, 
		final String completion_date = mCursor.getString(3);
    	//SQLiteHelper.COLUMN_MAP, 
		final String map = mCursor.getString(4);
		//SQLiteHelper.COLUMN_MAP_SOLUTION, 
		final String solution = mCursor.getString(5);
		//SQLiteHelper.COLUMN_TARGET_TIME, 
		final double target_time = mCursor.getDouble(6);
		//SQLiteHelper.COLUMN_CURRENCY1, 
    	final int currency1 = mCursor.getInt(7);
		//SQLiteHelper.COLUMN_CURRENCY2, 
    	final int currency2 = mCursor.getInt(8);
		
		final Level tmp = new Level(id,completed,time,completion_date,map,solution,target_time,currency1,currency2);
		return tmp;
	}

	//Reset database
	public int reset() {
		
		final ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_BEST_TIME, 0);
		values.put(SQLiteHelper.COLUMN_COMPLETED, 0);
		values.put(SQLiteHelper.COLUMN_COMPLETION_DATE, "");
		
		return database.update(SQLiteHelper.TABLE_LEVEL_DATA,
				values, null, null);
	}
	
	//Update single level. This method only updates data the user can affect (target time, completion date etc)
	public int updateLevel(Level lvl) {
		final ContentValues values = new ContentValues();
		
		values.put(SQLiteHelper.COLUMN_BEST_TIME, lvl.time);
		values.put(SQLiteHelper.COLUMN_COMPLETED, lvl.completed);
		values.put(SQLiteHelper.COLUMN_COMPLETION_DATE, lvl.completion_date);
		values.put(SQLiteHelper.COLUMN_MAP_SOLUTION, lvl.solution);
		
		return database.update(SQLiteHelper.TABLE_LEVEL_DATA,
				values, SQLiteHelper.COLUMN_ID + " = ?", new String[] { ""+lvl.id });
		
	} 
	
	//Return the total amount of levels in the database
	public int getNumberOfLevels(SQLiteDatabase database) {
	    final String sql = "SELECT COUNT(*) FROM " + SQLiteHelper.TABLE_LEVEL_DATA;
	    final SQLiteStatement statement = database.compileStatement(sql);
	    final int count = (int) statement.simpleQueryForLong();
	    return count;
		
	}
	
}