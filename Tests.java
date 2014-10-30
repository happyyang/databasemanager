public class Tests {
 /**this simply extends DatabaseManager, implements onCreate, onUpgrade and the super constructor.
 * It doesn't do anything
 */
  private static class Database extends DatabaseManager { public Database(Context context, String name, int version) {super(context, name, version);public void onCreate(SQLiteDatabase db) {}public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {} }


  public testDuplication(String tag){
        HashMap<SQLiteDatabase, SQLiteDatabase> hashMap = new HashMap<SQLiteDatabase, SQLiteDatabase>(30);
        for (int i=0;i<10;i++){
            hashMap.put(new Database(this,"one",1).getDb(),new Database(this,"one",1).getDb());
            hashMap.put(new Database(this,"two",1).getDb(),new Database(this,"two",1).getDb());
            hashMap.put(new Database(this,"three",1).getDb(),new Database(this,"three",1).getDb());
        }
        Log.e(tag,"SQLite Objects in hashmap:"+hashMap.toString());
        Log.e(tag,"size: "+hashMap.size());
        //this will print 3 objects, then 'size: 3'
        //this proves that even though we created 30 different DatabaseManagers,
        //all the SQLiteDatabase objects were the same
  }
  public testThreading(){
    //this test coming soon
  }
  


}
