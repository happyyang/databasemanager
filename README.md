databasemanager
===============

A simple SQLite Database manager that handles threading/synchronization issues

If you're just using this class as it currently is, just copy/paste the source into a new java file in your Andriod project, then extend DatabaseManager (instead of SQLiteOpenHelper) for each database you want on-disk

A very similar implentation of this is included in my Android SQLite Library found at http://androidsqlitelibrary.com/

I personally don't recommend using Android's SQLiteDatabase and SQLiteOpenHelper classes directly. There are quite a few other options, such as greenDao, sugarOrm, OrmLite for Android, JQL (mine), and more. I'm working on compiling a list of options with some basic about pages and how-to guides on my forum at http://forum.jakar.co/android-sqlite/1396714536/ (which will soon be moved to the above site) so that you can compare and choose which is best for you.

Basic usage:
```
MyDatabaseManager dbManager = new MyDatabaseManager(context);//this opens the database
SQLiteDatabase db = dbManager.getDb();
//Later, I will make DatabaseManager interface with SQLiteDatabase directly, just so nobody uses it wrong.
db.someFunction();//use the SQLiteDatabase instance as you normally would.
//close the database when you're completely done.
//for activities, opening (instantiating) in onStart and closing in onStop is a good practice because it prevents from leaking connections.
//if you need it in onCreate, then open it there and conditionally open in onStart, then always call close in onStop and conditionally in onDestroy (if onStop was skipped).
```
