	package hamtarun.jogging;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * @author Gatellier Bastien, Pouchepanadin Christian
 * Define how to create and interact with the DB and table (create, insert, select, delete).
 */
public class HListofrunDB {
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "hamtarun.db";

	private static final String TABLE = "listofrun";
	private static final String COL_ID = "id";
	private static final String COL_STARTDATE = "startdate";
	private static final String COL_DURATION = "duration";
	private static final String COL_DISTANCE = "distance";
	private static final String COL_CALORIES = "calories";
	
	private SQLiteDatabase db;
	private HDB hdb;
	
	
	/**
	 * @author gatellba
	 *
	 */
	private static class HDB extends SQLiteOpenHelper {
		public HDB(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + TABLE + " ("
					+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COL_STARTDATE + " INTEGER NOT NULL, "
					+ COL_DURATION + " INTEGER NOT NULL, "
					+ COL_DISTANCE + " REAL NOT NULL, "
					+ COL_CALORIES + " REAL NOT NULL);"
			);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + TABLE + ";");
			onCreate(db);
		}

	}
	

	public HListofrunDB(Context context) {
		hdb = new HDB(context, DB_NAME, null, DB_VERSION);
	}
	
	public void open(){
		db = hdb.getWritableDatabase();
	}
	
	public void close(){
		db.close();
	}
	
	public SQLiteDatabase getDB(){
		return db;
	}
	
	public long insertRun(HRun run){
		ContentValues values = new ContentValues();

		values.put(COL_STARTDATE, run.getStartDate());
		values.put(COL_DURATION, run.getDuration());
		values.put(COL_DISTANCE, run.getDistance());
		values.put(COL_CALORIES, run.getCalories());
		
		return db.insert(TABLE, null, values);
	}
	
	public int deleteRun(int run_id){
		return db.delete(TABLE, COL_ID + " = " +run_id, null);
	}
	
	/**
	 * Get all the run stored in the SQLite DB, format some columns, and calculate some new one.
	 * Starting date in dd/mm/YYYY format
	 * Duration of the run in hh:mm:ss format
	 * Distance in km
	 * Speed in km/h, calculate from distance and duration
	 * Calories 
	 * @return Cursor element which contain the results
	 */
	public Cursor selectAll(){
		return db.rawQuery("SELECT "+COL_ID+" AS _id,"
							+"strftime('%d/%m/%Y',"+COL_STARTDATE+",'unixepoch') AS startdate,"
							+COL_DISTANCE+" AS distance,"
							+"strftime('%H:%M:%S',"+COL_DURATION+",'unixepoch') AS duration,"
							+"ROUND(3600*"+COL_DISTANCE+"/("+COL_DURATION+"),2) AS speed,"
							+COL_CALORIES+" AS calories "
							+"FROM " + TABLE + " ORDER BY " + COL_STARTDATE + " DESC",null);
		
	}
}
