package eu.centric.pinadmin.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.centric.pinadmin.MainActivity;
import eu.centric.pinadmin.util.DateUtil;
import eu.centric.pinadmin.util.Details;

// todo javadocen
// todo code cleanup (commented code verwijderen)

/**
 * The type Data handler.
 * @author MHeide
 * @since 17-4-2017
 */
public class DataProvider {
    /**
     * The constant MDB_COLUMNS.
     */
    public static final String[] MDB_COLUMNS = {"adres", "betaalauto", "CertCode", "ClientName", "datacommadre", "datuminstall", "Euro_Install_Num", "Euro_Opmerkingen", "FAX", "GW", "HoofdkantoorNaam", "Installatie", "InstallatieDatum", "kassanum", "MAC", "MASK", "MerchantID", "Naam", "nuaadres", "pinpadserie", "plaats", "Portnumber", "Postcode", "PUKGSM", "REF", "telefoon", "TID", "TIDAWL", "typebetaal1", "verkooppuntc"};
    /**
     * The constant RESULT_COLUMS.
     */
    public static final String[] RESULT_COLUMS = {"displayName", "plaats", "shopNR", "kassanum", "tmsNR"};
    /**
     * The constant TMS_REFERENCE_COLUMN_NAME.
     */
    public final static String TMS_REFERENCE_COLUMN_NAME = "REF";
    /**
     * The constant TMS_COLUMN_NAME.
     */
    public final static String TMS_COLUMN_NAME = "TMSNUM";
    /**
     * The constant MDBTABLE_TMS.
     */
    public static final int MDBTABLE_TMS = 0;
    /**
     * The constant MDBTABLE_BEANET1.
     */
    public static final int MDBTABLE_BEANET1 = 1;
    /**
     * The Mdb table names.
     */
    static final String[] MDB_TABLE_NAMES = {"TMSID", "beanet1"};
    private static final String[] FILTER_FIELDS_COLUMN_NAMES = {"displayName", "plaats", "tmsNR"};
    private static final String CALCULATED_COLUMNS = "displayName,shopNR,tmsNR";
    private MDBManager mdbConnection;
    private DatabaseManager sqLiteDBConnection;
    private Context mainContext;

////////////////////////////////////////////////////////////////////////////////////////////////////

    private DataProvider() {
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static DataProvider getInstance() {
        return SingletonHelper.singletonInstance;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Intit data provider.
     *
     * @param context the context
     * @param newDB   the new db
     */
    public void intitDataProvider(Context context, final boolean newDB) {
        mainContext = context;
        Log.d(MainActivity.LOGNAME + "\\DP\\init", "start sql conn");
        sqLiteDBConnection = new DatabaseManager(mainContext, newDB);
        Log.d(MainActivity.LOGNAME + "\\DP\\init", "start mdb conn");
        mdbConnection = new MDBManager(context.getApplicationInfo().dataDir);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Insert.
     *
     * @param insertData the insertPinDataInSQLDB data
     */
    public void insertPinDataInSQLDB(List<String[]> insertData, Context context) {
        // todo db instance eerst stoppen, starten voor insertPinDataInSQLDB en daarna weer starten

        final int mdbColums = MDB_COLUMNS.length;
        StringBuilder query = new StringBuilder(415);
        query.append("Insert into ").
                append(MDB_TABLE_NAMES[MDBTABLE_BEANET1]).
                append(" (_id,");
//    for(int columnCounter = 0; columnCounter<mdbColums;columnCounter++){
//        query.append(MDB_COLUMNS[columnCounter]).append(',');
//    }
        for (String MDB_COLUMN : MDB_COLUMNS) {
            query.append(MDB_COLUMN).append(',');
        }
        query.append(CALCULATED_COLUMNS).append(") values(");
        int questionMarksMinusOne = mdbColums + 3; // 4 extra columns, maar er wordt na de for loop een ? toegevoegd
        for (int columnCounter = 0; columnCounter < questionMarksMinusOne; columnCounter++) {
            query.append('?').append(',');
        }
        query.append("?)");
//    String sqlQuery = "Insert into beanet1 (_id,adres,betaalauto,CertCode,ClientName,datacommadre,datuminstall,Euro_Install_Num,Euro_Opmerkingen,FAX,GW,HoofdkantoorNaam,Installatie,InstallatieDatum,kassanum,MAC,MASK,MerchantID,Naam,nuaadres,pinpadserie,plaats,Portnumber,Postcode,PUKGSM,REF,telefoon,TID,TIDAWL,typebetaal1,verkooppuntc,displayName,shopNR,tmsNR) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        // todo split dbinstance van DatabaseManager in readableDataBaseManager en writeableDataBaseManager
//        sqLiteDBConnection.close();
//        sqLiteDBConnection = new DatabaseManager(context, false);
        sqLiteDBConnection.insertSQL(query.toString(), insertData, questionMarksMinusOne + 1);
//        sqLiteDBConnection.insertSQL(query.toString(), insertData, questionMarksMinusOne + 1);
//        sqLiteDBConnection.close();
        Log.d(MainActivity.LOGNAME + "\\DH\\instertinDB", "done inserting clean insert");
//        sqLiteDBConnection = new DatabaseManager(mainContext, false);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    /**
//     * Quick an d dirty insertPinDataInSQLDB.
//     *
//     * @param insertData the insertPinDataInSQLDB data
//     * @param context    the context
//     */
//    public void quickAnDDirtyInsert(List<String[]> insertData, final Context context) {
//
//        final int mdbColums = MDB_COLUMNS.length;
//        StringBuilder query = new StringBuilder(415);
//        query.append("Insert into ").
//                append(MDB_TABLE_NAMES[MDBTABLE_BEANET1]).
//                append(" (_id,");
////    for(int columnCounter = 0; columnCounter<mdbColums;columnCounter++){
////        query.append(MDB_COLUMNS[columnCounter]).append(',');
////    }
//        for (String MDB_COLUMN : MDB_COLUMNS) {
//            query.append(MDB_COLUMN).append(',');
//        }
//        query.append(CALCULATED_COLUMNS).append(") values(");
//        int questionMarksMinusOne = mdbColums + 3; // 4 extra columns, maar er wordt na de for loop een ? toegevoegd
//        for (int columnCounter = 0; columnCounter < questionMarksMinusOne; columnCounter++) {
//            query.append('?').append(',');
//        }
//        query.append("?)");
//
//        DatabaseManager dm = new DatabaseManager(context, false);
//        SQLiteDatabase writableDatabase = dm.getWritable();
//
//        writableDatabase.beginTransaction();
//        SQLiteStatement insertPinDataInSQLDB = writableDatabase.compileStatement(query.toString());
//        writableDatabase.beginTransaction();
//
//        for (String[] insertEntry : insertData) {
//            for (int columnCounter = 0; columnCounter < (questionMarksMinusOne + 1); columnCounter++) {
//                String value = insertEntry[columnCounter];
//                if (value == null) {
//                    insertPinDataInSQLDB.bindNull(columnCounter + 1);
//                } else {
//                    insertPinDataInSQLDB.bindString(columnCounter + 1, value);
//                }
//            }
//            insertPinDataInSQLDB.execute();
//        }
//        writableDatabase.setTransactionSuccessful();
//        writableDatabase.endTransaction();
//    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Empty general pin data table.
     */
    public void emptyGeneralPinDataTable() {
        sqLiteDBConnection.emptySQLiteTable(MDB_TABLE_NAMES[MDBTABLE_BEANET1]);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Haal resultaten cursor cursor.
     *
     * @param selectedItems the selected items
     * @param columnNR      the column nr
     * @param ascend        the ascend
     * @return the cursor
     */
    public Cursor getResultsCursor(final String[] selectedItems, final int columnNR, final boolean ascend) {

        StringBuilder sqlSelectQuery = new StringBuilder("SELECT _id ");
        for (String column : RESULT_COLUMS) {
            sqlSelectQuery.append(',').
                    append(column);
        }
        sqlSelectQuery.append(" FROM ").append(MDB_TABLE_NAMES[MDBTABLE_BEANET1]);
        sqlSelectQuery.append(makeWhereQuery(selectedItems, FILTER_FIELDS_COLUMN_NAMES, MainActivity.NUMBER_OF_FILTERFIELDS + 1)); // 1 meer als toegestaan
        sqlSelectQuery.append(" ORDER BY ").append(RESULT_COLUMS[columnNR]).append(ascend ? " DESC" : " ASC");
        Log.d(MainActivity.LOGNAME + "\\DH\\haalResCur", "resultatenQuery: " + sqlSelectQuery);

        //        Log.d(MainActivity.LOGNAME + "\\DH\\haalResCus","aantal in resultatenQuery: " + t.getCount());

        return sqLiteDBConnection.executeQueryForCursor(sqlSelectQuery.toString(), null);
//            return sqLiteDBConnection.rawQuery(sqlSelectQuery.toString(), null);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets item list.
     *
     * @param initiater     the initiater
     * @param selectedItems the selected items
     * @return the item list
     */
//for LinkedDataFilter
    public List<String> getItemList(final int initiater, final String[] selectedItems) {

        StringBuilder resultQuery = new StringBuilder("SELECT DISTINCT RTRIM(").append(FILTER_FIELDS_COLUMN_NAMES[initiater]).append(") FROM ").append(MDB_TABLE_NAMES[MDBTABLE_BEANET1]);
        resultQuery.append(makeWhereQuery(selectedItems, FILTER_FIELDS_COLUMN_NAMES, initiater)).append(" ORDER BY ").append(FILTER_FIELDS_COLUMN_NAMES[initiater]);
        Log.d(MainActivity.LOGNAME + "\\DH\\getItem", "Query item" + initiater + " : " + resultQuery);
        return sqLiteDBConnection.executeQueryForList(resultQuery.toString(), null);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    //for LinkedDataFilter
    // todo change number_of_filterfields to columnNames.length
    private String makeWhereQuery(final String[] selctedItems, final String[] columnNames, final int initiater) {

        StringBuilder whereQuery = new StringBuilder(" WHERE ");
        int whereCount = 0;

        for (int count = 0; count < MainActivity.NUMBER_OF_FILTERFIELDS; count++) {
            if (count != initiater) {
                if (!"".equals(selctedItems[count])) {
                    if (whereCount > 0) {
                        whereQuery.append("AND ").append(columnNames[count]).append(" LIKE '%").append(selctedItems[count]).append("%' ");
                    } else {
                        whereQuery.append(columnNames[count]).append(" LIKE '%").append(selctedItems[count]).append("%' ");
                    }

                    whereCount++;
                }
            }
        }
        if (whereCount == 0) {
            whereQuery = new StringBuilder("");
        }
        return whereQuery.toString();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets details.
     *
     * @param id the id
     * @return the details
     */
    private Map<String, String> getDetails(long id) {

        StringBuilder query = new StringBuilder("SELECT ");
        int columnLength = MDB_COLUMNS.length;
        for (int count = 0; count < columnLength - 1; count++) {
            query.append(MDB_COLUMNS[count]);
            query.append(',');
        }

        query.append(CALCULATED_COLUMNS).append(" FROM ").append(MDB_TABLE_NAMES[MDBTABLE_BEANET1]).append(" where _id=").append(id);
        Cursor cursor = sqLiteDBConnection.executeQueryForCursor(query.toString(), null);
        // looping through all rows and adding to list
        Map<String, String> map = new HashMap<>();
        cursor.moveToFirst();
        for (int count = 0; count < cursor.getColumnCount(); count++) {
            map.put(cursor.getColumnName(count), cursor.getString(count));
//            Log.d(MainActivity.LOGNAME + "\\DH\\getDetails", "key :" + cursor.getColumnName(count) + ", value :" + cursor.getString(count));
        }

        // closing connection
        cursor.close();
        return map;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets local db time stamp.
     *
     * @return the local db time stamp
     */
    public String getLocalDBTimeStamp() {
        String restultDate = "";

        String selectQuery = "SELECT value FROM prefs where key=\"timeStamp\"";
        Cursor cursor = sqLiteDBConnection.executeQueryForCursor(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            if (cursor.getString(0) != null) {
                restultDate = cursor.getString(0);
            }
        }
        // closing connection
        cursor.close();
        return restultDate;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets current local db time stamp.
     */
    public void setCurrentLocalDBTimeStamp() {
        ContentValues data = new ContentValues();
        data.put("value", DateUtil.getCurrentTimeStamp());
        sqLiteDBConnection.updateTable("prefs", data, "key=\"timeStamp\"", null);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Open md bfile.
     *
     * @throws IOException the io exception
     */
    public void openMDBfile() throws IOException {
        mdbConnection.openMDBFile();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Close mdb file.
     */
    public void closeMDBFile() {
        mdbConnection.closeMDBFile();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets row count mdb table.
     *
     * @param tableNumber the table number
     * @return the row count mdb table
     */
    public int getRowCountMDBTable(final int tableNumber) {

        return mdbConnection.getRowCountMDBTable(tableNumber);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Next cursor mdb row boolean.
     *
     * @param tableNumber the table number
     * @return the boolean
     * @throws IOException the io exception
     */
    public boolean nextCursorMDBRow(final int tableNumber) throws IOException {
        return mdbConnection.nextCursorMDBRow(tableNumber);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets int mdb cell.
     *
     * @param tableNumber the table number
     * @param columnName  the column name
     * @return the int mdb cell
     * @throws IOException the io exception
     */
    public Integer getIntMDBCell(final int tableNumber, String columnName) throws IOException {
        return mdbConnection.getIntMDBCell(tableNumber, columnName);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets string mdb cell.
     *
     * @param tableNumber the table number
     * @param columnName  the column name
     * @return the string mdb cell
     * @throws IOException the io exception
     */
    public String getStringMDBCell(final int tableNumber, String columnName) throws IOException {
        return mdbConnection.getStringMDBCell(tableNumber, columnName);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets object mdb cell.
     *
     * @param tableNumber the table number
     * @param columnName  the column name
     * @return the object mdb cell
     * @throws IOException the io exception
     */
    public Object getObjectMDBCell(final int tableNumber, String columnName) throws IOException {
        return mdbConnection.getObjectMDBCell(tableNumber, columnName);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets current pinpad details map.
     *
     * @param assets        the assets
     * @param currentPinpad the current pinpad
     * @return the current pinpad details map
     */
    public Map<String, List<Details>> getCurrentPinpadDetailsMap(AssetManager assets, long currentPinpad) {
        return (new XMLManager(assets, getDetails(currentPinpad))).getCurrentPinpadDetailsMap();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkAndCopyFile(Context updateContext, Uri content_describer) throws IOException {

        boolean correctFileAndCopySucces = false;


        Cursor returnCursor = updateContext.getContentResolver().query(content_describer, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String filename = returnCursor.getString(nameIndex);
        returnCursor.close();
        Log.d(MainActivity.LOGNAME + "\\DH\\cheAndCoFi", "filename: " + filename);



        if (MDBManager.mdbFileName.equals(filename)){
            correctFileAndCopySucces = mdbConnection.fileCopy(updateContext, content_describer);
        }

        return correctFileAndCopySucces;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class SingletonHelper {

        private static final DataProvider singletonInstance = new DataProvider();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}

