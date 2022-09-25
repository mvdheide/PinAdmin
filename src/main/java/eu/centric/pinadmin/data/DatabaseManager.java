package eu.centric.pinadmin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import eu.centric.pinadmin.MainActivity;

// todo javadocen
// todo code cleanup (commented code verwijderen)
/**
 * @author MHeide
 * @since 17-5-2017
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mabTD.db";
    //    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;

    //    private SQLiteDatabase readableDatabase;
//    private SQLiteDatabase dbReadInstance;
    private final String localDataDir;
    private final Context myContext;

    public DatabaseManager(Context context, final boolean newDB) {
        super(context, DATABASE_NAME, null, 1);


        localDataDir = context.getApplicationInfo().dataDir;
        myContext = context;
        if (newDB) {
            deleteDataBase();
            copyDataBase();
        }
        if (!checkDBexist()) {
            copyDataBase();
        }
        readableDatabase = getReadableDatabase();
//        writableDatabase = getWritableDatabase();

        Log.d(MainActivity.LOGNAME + "\\DbM\\getInst", "open: " + readableDatabase.isOpen());
        Log.d(MainActivity.LOGNAME + "\\DbM\\getInst", "readonly: " + readableDatabase.isReadOnly());
        Log.d(MainActivity.LOGNAME + "\\DbM\\getInst", "locked: " + readableDatabase.isDbLockedByCurrentThread());

    }

//    public void close(){
//        readableDatabase.close();
//        writableDatabase.close();
//        super.close();
//    }

//    private static synchronized DatabaseManager getDbManagerInstance(Context context) {
//        if (dbManagerInstance == null) {
//            dbInit(context);
//        }
////        Log.d(MainActivity.LOGNAME + "\\DbM\\DM","open: " + readableDatabase.isOpen());
////        Log.d(MainActivity.LOGNAME + "\\DbM\\DM","readonly: " + readableDatabase.isReadOnly());
////        Log.d(MainActivity.LOGNAME + "\\DbM\\DM","locked: " + readableDatabase.isDbLockedByCurrentThread());
//        return dbManagerInstance;
//    }

//    /**
//     * Gets db instance.
//     *
//     * @param context the context
//     * @param newDB   the new db
//     * @return the db instance
//     */
//    SQLiteDatabase getDBInstance(Context context, final boolean newDB) {
//
//        return readableDatabase;
//    }

//    private static synchronized void dbInit() {
////        myContext = context;
//
//
////        localDataDir = context.getApplicationInfo().dataDir;
////        myContext = context;
//    }

    private boolean checkDBexist() {

        File file = new File(localDataDir + "/databases/" + DATABASE_NAME);
        Log.d(MainActivity.LOGNAME + "\\DbM\\checkDBex", "db bestaat " + (file.exists()?"wel":"niet"));
        return file.exists();
    }

    //
//Copies your database from your local assets-folder to the just created empty database in the system folder
    private void copyDataBase() {

        try {
            //check if dir exist
            File userDBDir = new File(localDataDir + "/databases/");
            if (!userDBDir.exists()){
                Log.d(MainActivity.LOGNAME + "\\DbM\\copy", "db dir bestaat niet en wordt aangemaakt.");
                userDBDir.mkdir();
            } else {
                Log.d(MainActivity.LOGNAME + "\\DbM\\copy", "db dir bestaat");
            }
//                this.close();
            Log.d(MainActivity.LOGNAME + "\\DbM\\copy", "db wordt gekopieert.");
            String outFileName = localDataDir + "/databases/" + DATABASE_NAME;
            OutputStream myOutput = new FileOutputStream(outFileName);
            InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myInput.close();
            myOutput.flush();
            myOutput.close();
//            Log.d(MainActivity.LOGNAME + "\\DbM\\copy", "db wordt opnieuw geiniteerd");
//            dbInit(myContext);
            Log.d(MainActivity.LOGNAME + "\\DbM\\copy", "db is gekopieert van:" + myInput + ", naar:" + outFileName);
        } catch (IOException e) {
            Log.e(MainActivity.LOGNAME + "\\DbM\\copy", e.toString());
        }
    }

    //delete database
    private void deleteDataBase() {

        if (checkDBexist()) {
            new File(localDataDir + "/databases/" + DATABASE_NAME).delete();
//            file.delete();
            Log.d(MainActivity.LOGNAME + "\\DbM\\delete", "delete database file.");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

//    //Check database already exist or not
//    private boolean checkDataBaseExist()
//    {
//        String fileName = localDataDir + "/databases/"+ DATABASE_NAME;
//
//        File file = new File(fileName);
//        boolean checkDB = false;
//        try
//        {
//            checkDB = file.exists();
//        }
//        catch(SQLiteException e)
//        {
//            Log.e(MainActivity.LOGNAME + "\\DbM\\check", e.toString());
//        }
////        Log.d(MainActivity.LOGNAME + "\\DbM\\check", "db exists : " + checkDB);
//        return checkDB;
//    }


    List<String> executeQueryForList(final String sqlSelectQuery, final String[] args) {

        List<String> resultList = new ArrayList<>();
        Cursor cursor = readableDatabase.rawQuery(sqlSelectQuery, args);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
//            Log.d(MainActivity.LOGNAME + "\\DH\\exec","first cursor");
            do {
                resultList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        return resultList;
    }

    Cursor executeQueryForCursor(String sqlSelectQuery, String[] selectionArgs) {
        return readableDatabase.rawQuery(sqlSelectQuery, selectionArgs);
    }

    void emptySQLiteTable(final String tableName) {
        readableDatabase.close();
        writableDatabase = getWritableDatabase();
        writableDatabase.delete(tableName, null, null);
        writableDatabase.close();
        readableDatabase = getReadableDatabase();

    }

    void updateTable(String table, ContentValues values, String whereClause, String[] whereArgs) {
        readableDatabase.close();
        writableDatabase = getWritableDatabase();
        writableDatabase.update(table, values, whereClause, whereArgs);
        writableDatabase.close();
        readableDatabase = getReadableDatabase();
    }

    void insertSQL(final String SQLQuery, final List<String[]> insertData, final int numberOfColumns) {

        readableDatabase.close();
// todo new instance of db
//        SQLiteDatabase sqlitedb = DatabaseManager.getDBInstance(context, true);
        writableDatabase = getWritableDatabase();

        writableDatabase.beginTransaction();
        SQLiteStatement insert = writableDatabase.compileStatement(SQLQuery);
        writableDatabase.beginTransaction();

        for (String[] insertEntry : insertData) {
            for (int columnCounter = 0; columnCounter < numberOfColumns; columnCounter++) {
                String value = insertEntry[columnCounter];
                if (value == null) {
                    insert.bindNull(columnCounter + 1);
                } else {
                    insert.bindString(columnCounter + 1, value);
                }
            }
            insert.execute();
        }
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        writableDatabase.close();
        readableDatabase = getReadableDatabase();
    }

    public SQLiteDatabase getWritable() {

        return getWritableDatabase();

    }

}
