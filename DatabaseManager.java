package com.androidsqlitelibrary.databasehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.ConcurrentHashMap;

/**
 * If distributing: Keep my notes that are self-promoting. Keep or improve the other notes.4
 * If distributing (to programmers) in a way that the notes cannot be read, please include a readme file and provide
 * a link to http://androidsqlitelibrary.com
 * You don't have to keep the self-promotion stuff and you don't have to keep the link in a readme, but I would appreciate it.
 * If you ever need help with this code, contact me at support@androidsqlitelibrary.com (or support@jakar.co )
 * 
 * Do not sell this. but use it as much as you want. There are no implied or express warranties with this code. 
 *
 * This is a simple database manager class which makes threading/synchronization super easy.
 *
 *	Instantiate this class once in each thread that uses the database. 
 *  <br>Make sure to call {@link #close()} on every opened instance of this class
 *  <br>If it is closed, the call {@link #open()} before using again.
 * <br><br>Call {@link #getDb()} to get an instance of the underlying SQLiteDatabse class (which is synchronized)
 *
 * I also implement this system (well, it's very similar) in my <a href="http://androidslitelibrary.com">Android SQLite Libray</a> at http://androidslitelibrary.com
 * 
 *
 */
public class DatabaseManager {
	/** A helper class to manage connection counting. 
	* Your SQLiteOpenHelper class will do everything it normally would and will function as 
	* Android documentations says. This subclass of your subclass just manages the connection counter4
	*/
	static private class DBSQLiteOpenHelper extends MySQLiteOpenHelper {
		   private int counter;
			private static final Object lockObject = new Object();
		 
			public JQLOpenHelper(Context context, String name, int version) {
				super(context, name, null, version);
			}

			public void addConnection(){
				synchronized (lockObject){
					counter++;
				}

			}
			public void removeConnection(){

				synchronized (lockObject){
					counter--;
					if (counter<0)counter = 0;
				}
			}
			public int getCounter() {
				synchronized (lockObject){
					return counter;
				}
			}
	}

    private static final ConcurrentHashMap<String,JQLOpenHelper> dbMap = new ConcurrentHashMap<String, DBSQLiteOpenHelper>();

    private static final Object lockObject = new Object();


    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase db;
    private Context context;

    private boolean hasActiveTransaction = false;

    /** Instantiate a new DB Helper. 
	 *<br> SQLiteOpenHelpers are statically cached so they will be reused for concurrency
     *
     * @param context Any {@link android.content.Context} belonging to your package.
     * @param name The database name. This may be anything you like. Adding a file extension is not required and any file extension you would like to use is fine.
     * @param version the database version.
     */
    public JQLDatabase(Context context, String name, int version) {
        String dbPath = context.getApplicationContext().getDatabasePath(name).getAbsolutePath();
        synchronized (lockObject) {
            sqLiteOpenHelper = dbMap.get(dbPath);
            if (sqLiteOpenHelper==null) {
				//change MySQLiteOpenHelper above (for the extend) to your subclass of MySQLiteOpenHelper
                sqLiteOpenHelper = new DBSQLiteOpenHelper(context, name, version, this);
                dbMap.put(dbPath,sqLiteOpenHelper);
            }
			//SQLiteOpenHelper class caches the database, so this will be the same SQLiteDatabase object
            db = sqLiteOpenHelper.getWritableDatabase();
        }
        this.context = context.getApplicationContext();
    }
	public getDb(){
		return db;
	}

    /** Check if the underlying SQLiteDatabase is open
     *
     * @return whether the DB is open or not
     */
    public boolean isOpen(){
        return (db!=null&&db.isOpen());
    }


    /** Lowers the DB counter by 1 for any {@link SQLiteOpenHelper} objects referencing the same DB on disk
     *  <br />If the new counter is 0, then the database will be closed.
     *  <br /><br />This needs to be called before application exit.
     * <br />If the counter is 0, then any further use of this object will cause crashes until another is instantiated or you call {@link #reOpen()}
     * <br />Primarily, you would get NullPointerExceptions from other methods of this class after calling this method if the new counter is 0
     *
     * @return true if the underlying {@link android.database.sqlite.SQLiteDatabase} is closed (counter is 0), and false otherwise (counter > 0)
     */
    public boolean close(){
        sqLiteOpenHelper.removeConnection();
        if (sqLiteOpenHelper.getCounter()==0){
            synchronized (lockObject){
                if (db.inTransaction())db.endTransaction();
                if (db.isOpen())db.close();
                db = null;
            }
            return true;
        }
        return false;
    }
	/** Increments the internal db counter by one and opens the db if needed
	*
	*/
	public void open(){
		sqLiteOpenHelper.addConnection();
		if (db==null||!db.isOpen()){
	            synchronized (lockObject){
	                db = sqLiteOpenHelper.getWritableDatabase();
	            }
		} 
	}
}
