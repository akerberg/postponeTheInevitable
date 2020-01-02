package postpone.the.inevitable.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import postpone.the.inevitable.menu.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {

	//table name
	public static final String TABLE_LEVEL_DATA = "level_data";

	//Columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_COMPLETED = "completed";
	public static final String COLUMN_BEST_TIME = "best_time";
	public static final String COLUMN_COMPLETION_DATE = "date";
	public static final String COLUMN_MAP = "map";
	public static final String COLUMN_MAP_SOLUTION = "map_solution";
	public static final String COLUMN_TARGET_TIME = "target_time";
	public static final String COLUMN_CURRENCY1 = "currency1";
	public static final String COLUMN_CURRENCY2 = "currency2";
		
	//Database name
	private static final String DATABASE_NAME = "level.db";

	//Database version. Change to access onUpgrade
	private static final int DATABASE_VERSION = 3;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_LEVEL_DATA + "( " 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_COMPLETED + " boolean not null, "
			+ COLUMN_BEST_TIME + " double not null, "
			+ COLUMN_COMPLETION_DATE + " text null, "
			+ COLUMN_MAP + " text null, "
			+ COLUMN_MAP_SOLUTION + " text null, "
			+ COLUMN_TARGET_TIME + " double null, "
			+ COLUMN_CURRENCY1 + " integer null, "
			+ COLUMN_CURRENCY2 + " integer null "
			+ ");";
	
	//Currently used to be able to access files in raw dir
	private final Context mContext;

	//Constructor
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	//Create a new database. Runs first time game is started
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		populateDatabase(mContext, database);
	}

	//Accessed when version number is changed. So far it only deletes the old database and creates a new one
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		/*Log.w(SQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion);*/

		populateDatabase(mContext, db);
		
	}
	
	//Read through a specific file and populate database with the data from that file
	public void populateDatabase(Context context, SQLiteDatabase database) {

		final String sql = "SELECT COUNT(*) FROM " + TABLE_LEVEL_DATA;
	    final SQLiteStatement statement = database.compileStatement(sql);
	    final int numberOfRows = (int) statement.simpleQueryForLong();
		
        //Read file
        try {
            final InputStream is = context.getResources().openRawResource(R.raw.levels);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is,"ISO-8859-15"));

            int id = 0;
            
            String tmp;
            
    		//SQLiteHelper.COLUMN_MAP, 
    		String map = "";
    		//SQLiteHelper.COLUMN_TARGET_TIME, 
    		double target_time = 0;
    		//SQLiteHelper.COLUMN_CURRENCY1, 
        	int currency1 = 0;
    		//SQLiteHelper.COLUMN_CURRENCY2, 
        	int currency2 = 0;            

			int counter = 0;
            while ((tmp = reader.readLine()) != null) {
            	
            	counter++;
				String[] content = tmp.split("###");
				
				if (content.length < 2)
					continue;
				
				String data = content[1].trim();

				if (counter == 1) {
					//Ignore first row. Can be used for commentary
				}
				else if (counter == 2) {
					//Target time
					target_time = Double.parseDouble(data);
				}
				else if (counter == 3) {
					//Currency1
					currency1 = Integer.parseInt(data);
				}
				else if (counter == 4) {
					//Currency2
					currency2 = Integer.parseInt(data);
				}
				else if (counter == 5) {
					//Map row1
					map = data;
				}
				else if (counter > 5 && counter < 13) {
					//Map row2-8
					map += data;
				}
				else if (counter == 13) {
					//Map row9
					map += data;
					counter = 0;
					id++;
					addOrUpdateLevel(database, map, target_time, currency1, currency2,id,numberOfRows);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	//Helper method used to write a level to database
	private void addOrUpdateLevel(SQLiteDatabase database, String map, double target_time, int currency1, int currency2, int id, int numberOfRows) {

		//If this is an already existing level we update instead of insert new
		if (id <= numberOfRows) {
			final ContentValues values = new ContentValues();
			values.put(SQLiteHelper.COLUMN_MAP, map);
			values.put(SQLiteHelper.COLUMN_MAP_SOLUTION, map);
			values.put(SQLiteHelper.COLUMN_TARGET_TIME, target_time);
			values.put(SQLiteHelper.COLUMN_CURRENCY1, currency1);
			values.put(SQLiteHelper.COLUMN_CURRENCY2, currency2);
			database.update(SQLiteHelper.TABLE_LEVEL_DATA, values, SQLiteHelper.COLUMN_ID + " = ?", new String[] { ""+id });
		}
		else {
			final ContentValues values = new ContentValues();
			values.put(SQLiteHelper.COLUMN_COMPLETED, 0);
			values.put(SQLiteHelper.COLUMN_BEST_TIME, 0);
			values.put(SQLiteHelper.COLUMN_COMPLETION_DATE, "");
			values.put(SQLiteHelper.COLUMN_MAP, map);
			values.put(SQLiteHelper.COLUMN_MAP_SOLUTION, map);
			values.put(SQLiteHelper.COLUMN_TARGET_TIME, target_time);
			values.put(SQLiteHelper.COLUMN_CURRENCY1, currency1);
			values.put(SQLiteHelper.COLUMN_CURRENCY2, currency2);
			database.insert(SQLiteHelper.TABLE_LEVEL_DATA, null,values);		
		}
		
	}
}
