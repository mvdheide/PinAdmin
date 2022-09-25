package eu.centric.pinadmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.centric.pinadmin.data.DataProvider;
import eu.centric.pinadmin.data.DatabaseManager;
import eu.centric.pinadmin.util.ComputeName;
import eu.centric.pinadmin.util.DateUtil;
import eu.centric.pinadmin.util.TaskProgress;

import static eu.centric.pinadmin.data.DataProvider.TMS_COLUMN_NAME;
import static eu.centric.pinadmin.data.DataProvider.TMS_REFERENCE_COLUMN_NAME;

// todo javadocen
// todo code cleanup (commented code verwijderen)

/**
 * The type Update activity.
 * <p>
 * onedrive: onclick->onedriveFilePicker->onActivityResult->downloadMDBFile->BroadcastReceiver onComplete->startconverting
 * vpn: onclick->startVPN    ->downloadMDBFile->BroadcastReceiver onComplete->startconverting
 *
 * @author MHeide
 * @since 30-11-‎2017
 */
public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int HOOFDKETENCOLUMN = 10;
    private final static int NAAMCOLUMN = 17;
    private static final int PICKFILE_RESULT_CODE = 1234;
    private final ArrayList<Long> refid = new ArrayList<>();
    private SparseArray<String> tmsRefMap;
    private DataProvider dataProvider;
//    private BroadcastReceiver onComplete;

////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        dataProvider = DataProvider.getInstance();

        String updateText = getString(R.string.update_last_time) + ' ' + DateUtil.getReadableTimeStamp(dataProvider.getLocalDBTimeStamp()) + '.';
        ((TextView) findViewById(R.id.dbVersionTextView)).setText(updateText);

//        getSupportActionBar().setTitle("sad");

        Thread.currentThread().setContextClassLoader(com.healthmarketscience.jackcess.Database.class.getClassLoader());
        System.setProperty("com.healthmarketscience.jackcess.brokenNio", "true");
        findViewById(R.id.filePickerButton).setOnClickListener(this);
//        findViewById(R.id.vpnButton).setOnClickListener(this);
//        findViewById(R.id.onedriveButton).setOnClickListener(this);

//        onComplete = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                long downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                refid.remove(downloadID);
//                if (refid.isEmpty()) {
//                    Log.d(MainActivity.LOGNAME + "\\update\\oncre", " download is klaar");
//                    dataProvider.copyMDBFile();
//                    startConverting();
//                }
//            }
//        };
//        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(onComplete);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    private boolean isAppInstalled(final String packageName) {
//        PackageManager pm = getPackageManager();
//        boolean installed;
//        try {
//            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
//            installed = true;
//        } catch (PackageManager.NameNotFoundException e) {
//            installed = false;
//        }
//        Log.d(MainActivity.LOGNAME + "\\update\\isAppI", packageName + "is " + (installed ? "geinstalleerd" : "niet geinstalleerd"));
//
//        return installed;
//    }

    @Override
    public void onClick(View view) {
        performFileSearch();
//        switch (view.getId()) {
//            case R.id.onedriveButton:
//                if (isAppInstalled("com.microsoft.skydrive")) {
//
//                    dataProvider.startOneDrivePicker(UpdateActivity.this);
//                } else {
//                    Snackbar.make(view
//                            , "OneDrive is niet geinstalleerd"
//                            , Snackbar.LENGTH_LONG).setAction("Action", null)
//                            .show();
//                }
//
////                fileLocation = new File(getApplicationInfo().dataDir + "/ATDCTAP2.MDB");
//                break;
//            case R.id.vpnButton:
//                Snackbar.make(view
//                        , "VPN is nog niet geimplementeerd"
//                        , Snackbar.LENGTH_LONG).setAction("Action", null)
//                        .show();//                dataProvider.startVPNConnection();
////                refid.add(dataProvider.downloadMDBFile(dataProvider.getVPNUri(), this));
//                break;
//            case R.id.filePickerButton:
//                performFileSearch();
//                dataProvider.copyMDBFileFromAssets(getApplicationInfo().dataDir, getAssets());
//                startConverting();
//                break;
//        }

//        if (checkUpdateAvailable()) {
////            copyMDBFile();
//            if (dataProvider.isVPNConnected()) {
//                dataProvider.closeVPNConnection();
//            }
//        } else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
//            builder.setMessage(R.string.update_no_new_version)
//                    .setCancelable(false)
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            setResult(RESULT_OK);
//                            finish();
//                        }
//                    }).
//                    setIcon(android.R.drawable.ic_dialog_alert).
//                    setTitle(R.string.failed);
//            AlertDialog alert = builder.create();
//            alert.show();
//        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    private void performFileSearch() {

//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
//        intent = Intent.createChooser(intent, "kies");
//        startActivityForResult(intent, 123);

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        startActivityForResult(
                Intent.createChooser(chooseFile, "Choose a file"),
                PICKFILE_RESULT_CODE
        );
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////
//
//
//    public String getPathFromFilePicker(Uri uri) {
//
//        String path;
//        String[] projection = { MediaStore.Files.FileColumns.DATA };
//        android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//
//        if(cursor == null){
//            path = uri.getPath();
//        }
//        else{
//            cursor.moveToFirst();
//            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
//            path = cursor.getString(column_index);
//            cursor.close();
//        }
//
//        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
//    }
//
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 123) {
//            Log.d(MainActivity.LOGNAME + "\\update\\onActRes", "save this file: " + data.getData());
//
//            boolean saved = dataProvider.saveFile(this, data.getData());
//            Log.d(MainActivity.LOGNAME + "\\update\\onActRes", "save is " + (saved?"suucesvol":"onsuccesvol"));
//            if (saved) {
//                startConverting();
//            }
        boolean correctFileAndCopySucces = false;
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK){
            try {
                correctFileAndCopySucces = dataProvider.checkAndCopyFile(this, data.getData());
            } catch (IOException ioe) {
                Log.e(MainActivity.LOGNAME + "\\update\\onActRes","errror while coping file");
            } finally {
                Log.d(MainActivity.LOGNAME + "\\update\\onActRes","file copied");

            }

            if (correctFileAndCopySucces){
                startConverting();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
                builder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setMessage(getString(R.string.update_failed) + "bestand niet gevonden of verkeerde file geselecteerd. Probeer opnieuw").
                            setIcon(android.R.drawable.ic_dialog_alert).
                            setTitle(R.string.failed)
                        .create()
                        .show();
            }

//            File source = new File(src);
//            Log.d(MainActivity.LOGNAME + "\\update\\onActRes","src is " + source.toString());
//            String filename = content_describer.getLastPathSegment();
//            Log.d(MainActivity.LOGNAME + "\\update\\onActRes","FileName is " + filename);

//            try {
//                dataProvider.checkAndCopyFile(source);
//            } catch (IOException ioe) {
//                Log.e(MainActivity.LOGNAME + "\\update\\onActRes","fout tijdens het kopieren van het bestand");
//            } finally {
//                Log.d(MainActivity.LOGNAME + "\\update\\onActRes","FileName is gekopiereerd");
//            }
//        } else {
//            boolean correctFile = false;
//            boolean error = false;
//            try {
//                correctFile = dataProvider.checkFileName(requestCode, resultCode, data);
//            } catch (NullPointerException e) {
//                Log.e(MainActivity.LOGNAME + "\\update\\onActRes", "er is terug gekeerd van de oneDrive sesiion zonder resultaat");
//                error = true;
//            }
//            if (correctFile) {
//                refid.add(dataProvider.downloadMDBFile(dataProvider.getOneDriveUri(), this));
//                super.onActivityResult(requestCode, resultCode, data);
//            } else {
//                if (!error) {
//                    Log.d(MainActivity.LOGNAME + "\\update\\onActRes", "verkeerde file geselecteerd");
//                    super.onActivityResult(requestCode, resultCode, data);
////                Snackbar.make(findViewById(R.id.updateCoordinatorLayout), "verkeerde file geselecteerd, selecteer " + dataProvider.getMDBFileName(), Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
//                    builder.setCancelable(false).
//                            setMessage(getString(R.string.update_wrong_file_selected) + dataProvider.getMDBFileName()).
//                            setIcon(android.R.drawable.ic_dialog_alert).
//                            setTitle("Niet gelukt").
//                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    setResult(RESULT_OK);
////                                finish();
//                                }
//                            });
//                    builder.show();
////                dataProvider.startOneDrivePicker(UpdateActivity.this);
//                }
//            }
        }

    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    private void startConverting() {
        new convertDBTask().execute();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////
//
//
//    private boolean checkUpdateAvailable() {
//
//
////        Log.d(MainActivity.LOGNAME + "\\update\\checkua", "last update:" + DateUtil.parseTimeStamp(lastDBUpdateString) );
////        Log.d(MainActivity.LOGNAME + "\\update\\checkua", "last modified:" + new Date(fileLocation.lastModified()));
////        Log.d(MainActivity.LOGNAME + "\\update\\checkua", "update available:" + DateUtil.parseTimeStamp(lastDBUpdateString).after(new Date(fileLocation.lastModified())));
//
//        return true;
////        return DateUtil.parseTimeStamp(lastDBUpdateString).after(new Date(fileLocation.lastModified()));
//    }

////////////////////////////////////////////////////////////////////////////////////////////////////


    private String convertToString(final Object obj) {

        String stringValue;
        if (obj == null) {
            stringValue = "";
        } else {
            stringValue = obj.toString();
        }
        return stringValue;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    private class convertDBTask extends AsyncTask<Void, TaskProgress, Void> {

        private boolean messageChanged = true;
        private ProgressDialog convertDialog;
        private boolean updateSuccesfull = true;
        private String errorMessage = "";

////////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        protected Void doInBackground(Void... aVoid) {

            try {
                dataProvider.openMDBfile();
                readTMSPinData();
//                convertGeneralPinData();
                ConvertQuickAndDirty();
            } catch (IOException ioe) {
                Log.e(MainActivity.LOGNAME + "\\update\\doINBack", "error tijdens het updaten:" + ioe.getMessage());
                ioe.printStackTrace();
                updateSuccesfull = false;
                errorMessage = ioe.getMessage();
            }

            dataProvider.closeMDBFile();
            return null;
        }

////////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        protected void onPreExecute() {
            convertDialog = new ProgressDialog(UpdateActivity.this);
            convertDialog.setTitle(getString(R.string.update));
            convertDialog.setMessage("converting");
            convertDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            convertDialog.setCancelable(false);
            convertDialog.show();

        }

////////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            convertDialog.dismiss();
//            progressBar.setVisibility(View.GONE);

            // check of tijdens updaten niet op back is geclickt

//            SQLiteDatabase sqlitedb = DatabaseManager.getDBInstance(UpdateActivity.this, true);
//            String sqlSelectQuery = "SELECT _id,displayName FROM beanet1 ORDER BY displayName";
//            android.database.Cursor resultatenCursor = sqlitedb.rawQuery(sqlSelectQuery, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
            builder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
            if (updateSuccesfull) {
                builder.setMessage(R.string.update_succesfull).
                        setIcon(android.R.drawable.ic_dialog_info).
                        setTitle(R.string.succes);
                dataProvider.setCurrentLocalDBTimeStamp();

            } else {
                builder.setMessage(getString(R.string.update_failed) + errorMessage).
                        setIcon(android.R.drawable.ic_dialog_alert).
                        setTitle(R.string.failed);
            }


            AlertDialog alert = builder.create();
            alert.show();
        }

////////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void onProgressUpdate(TaskProgress... progress) {
//            setProgress(progress[0]);
            super.onProgressUpdate(progress);
            final int count = progress[0].getProgressCounter();
            convertDialog.setProgress(count);
            if ((messageChanged) || (count == 10)) { // door een timing probleem, werkt dit niet bij 0..
                convertDialog.setMessage(progress[0].getMessage());
                Log.d(MainActivity.LOGNAME + "\\update\\onProUP", "messageChanged:" + progress[0].getMessage());
                Log.d(MainActivity.LOGNAME + "\\update\\onProUP", "messageChanged, length:" + progress.length);
                convertDialog.setMax(progress[0].getMax());
                messageChanged = false;
            }
        }

////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Read tms.
         *
         * @throws IOException the io exception
         */
        void readTMSPinData() throws IOException {
            tmsRefMap = new SparseArray<>();
            messageChanged = true;

            Log.d(MainActivity.LOGNAME + "\\update\\rdTMSPD", "Start converting TMS");
            TaskProgress publishTask = new TaskProgress(0,
                    dataProvider.getRowCountMDBTable(DataProvider.MDBTABLE_TMS),
                    getString(R.string.update_action2_tmsnr));
            int id = 0;
            int nullCounter = 0;
            while (dataProvider.nextCursorMDBRow(DataProvider.MDBTABLE_TMS)) {
                Integer tmsREF = dataProvider.getIntMDBCell(DataProvider.MDBTABLE_TMS, TMS_REFERENCE_COLUMN_NAME);
                if (tmsREF != null) {
                    String TMSnr = dataProvider.getStringMDBCell(DataProvider.MDBTABLE_TMS, TMS_COLUMN_NAME);
                    tmsRefMap.put(tmsREF, TMSnr);
                    publishTask.setProgressCounter(id);
                    publishProgress(publishTask);
                    id++;
                } else {
                    nullCounter++;
                }
            }
            Log.d(MainActivity.LOGNAME + "\\update\\rdTMSPD", "done converting TMS ");
            Log.d(MainActivity.LOGNAME + "\\update\\rdTMSPD", "id = " + id + " en max = " + dataProvider.getRowCountMDBTable(DataProvider.MDBTABLE_TMS) + " en #null = " + nullCounter);
        }

////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Convert general pin data.
         *
         * @throws IOException the io exception
         */
        void convertGeneralPinData() throws IOException {

            messageChanged = true;


            List<String[]> insertData = new ArrayList<>();
            int numberOfColumns = DataProvider.MDB_COLUMNS.length;
            String[] insertEntry;

            // todo lezen van mdb file is trager

//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    convertDialog = new ConvertDialog(UpdateActivity.this);

//            convertDialog.setProgress(0);
//                    convertDialog.show();
//                }
//            });
//            SQLiteDatabase sqlitedb = DatabaseManager.getDBInstance(UpdateActivity.this, true);
//            try {
//                dataProvider.openNewMDBTable(GENERAL_PIN_DATA_TABLE_NAME);
            dataProvider.emptyGeneralPinDataTable();
            Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "Start inserting");
            TaskProgress publishTask = new TaskProgress(0,
                    dataProvider.getRowCountMDBTable(DataProvider.MDBTABLE_BEANET1),
                    getString(R.string.update_action3_generaldata));
//                convertDialog.setMessage("Actie 3 van 3: verwerken van adressenBeheerTD gegevens");
//                convertDialog.setMax(dataProvider.getRowCountMDBTable(TMS_PIN_DATA_TABLE_NAME));
//                firstProgressMessage = new StringBuilder().append("Actie 3 van 3: verwerken van adressenBeheerTD gegevens - ");
//                secondProgressMessage = new StringBuilder().append('/').append(dataProvider.getRowCountMDBTable(GENERAL_PIN_DATA_TABLE_NAME)).append(" verwerkt");
            int id = 0;
            ComputeName computeName;
            while (dataProvider.nextCursorMDBRow(DataProvider.MDBTABLE_BEANET1)) {
//                insertEntry = new ArrayList<>();
                insertEntry = new String[numberOfColumns + 4];// todo het kan zijn dat insertEntry opnieuw geinitieerd moet worden
                computeName = new ComputeName(dataProvider.getStringMDBCell(DataProvider.MDBTABLE_BEANET1, DataProvider.MDB_COLUMNS[HOOFDKETENCOLUMN]), dataProvider.getStringMDBCell(DataProvider.MDBTABLE_BEANET1, DataProvider.MDB_COLUMNS[NAAMCOLUMN]));
                if (!computeName.isHqAndNameEmpty()) { // don't add the empty ones
                    insertEntry[0] = "" + id;
////                    dataProvider.bindStringInsertSQLite(1, "" + id);

//                    for (int columnCounter = 2; columnCounter < (numberOfColumns + 2); columnCounter++) { // 2 vanwege de eerste twee: hoofdketencolumn en naamColumn
//                        insertEntry.add(convertToString(dataProvider.getObjectMDBCell(GENERAL_PIN_DATA_TABLE_NAME, DataProvider.MDB_COLUMNS[columnCounter - 2])));
//////                        dataProvider.bindStringInsertSQLite(columnCounter, convertToString(dataProvider.getObjectMDBCell(GENERAL_PIN_DATA_TABLE_NAME, DataProvider.MDB_COLUMNS[columnCounter - 2])));
////                        insertPinDataInSQLDB.bindString(columnCounter, convertToString(dataProvider.getObjectMDBCell(beanet1TableName, DataProvider.MDB_COLUMNS[columnCounter - 2])));
//                    }
                    for (int columnCounter = 0; columnCounter < numberOfColumns; columnCounter++) {
                        insertEntry[columnCounter + 1] = convertToString(dataProvider.getObjectMDBCell(DataProvider.MDBTABLE_BEANET1, DataProvider.MDB_COLUMNS[columnCounter]));
////                        dataProvider.bindStringInsertSQLite(columnCounter, convertToString(dataProvider.getObjectMDBCell(GENERAL_PIN_DATA_TABLE_NAME, DataProvider.MDB_COLUMNS[columnCounter - 2])));
//                        insertPinDataInSQLDB.bindString(columnCounter, convertToString(dataProvider.getObjectMDBCell(beanet1TableName, DataProvider.MDB_COLUMNS[columnCounter - 2])));
                    }
                    insertEntry[numberOfColumns + 1] = computeName.getDisplayname();
                    insertEntry[numberOfColumns + 2] = computeName.getShopNR();
                    insertEntry[numberOfColumns + 3] = tmsRefMap.get(dataProvider.getIntMDBCell(DataProvider.MDBTABLE_BEANET1, TMS_REFERENCE_COLUMN_NAME));
////                    dataProvider.bindStringInsertSQLite((numberOfColumns + 2), computeName.getDisplayname());
////                    dataProvider.bindStringInsertSQLite((numberOfColumns + 3), computeName.getShopNR());
//                    insertPinDataInSQLDB.bindString((numberOfColumns + 2), computeName.getDisplayname());
//                    insertPinDataInSQLDB.bindString((numberOfColumns + 3), computeName.getShopNR());
////                    dataProvider.bindStringInsertSQLite((numberOfColumns + 4), tmsRefMap.get(dataProvider.getIntMDBCell(GENERAL_PIN_DATA_TABLE_NAME, TMS_REFERENCE_COLUMN_NAME)));
//                    String TMSnr = tmsRefMap.get(dataProvider.getIntMDBCell(beanet1TableName, "REF"));
//                    if (TMSnr == null) {
//                        insertPinDataInSQLDB.bindNull((numberOfColumns + 4));
//                    } else {
//                        insertPinDataInSQLDB.bindString((numberOfColumns + 4), tmsRefMap.get(dataProvider.getIntMDBCell(beanet1TableName, "REF")));
//                    }
//                    insertPinDataInSQLDB.execute();
                    insertData.add(insertEntry);
////                    dataProvider.executeInsertSQLite();
                }

                publishTask.setProgressCounter(id);
                publishProgress(publishTask);
                id++;
            }
////                dataProvider.setInsertSQLiteSessionSuccesfull();
//                sqlitedb.setTransactionSuccessful();
            Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "done inserting");
            Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "id = " + id + " en max = " + dataProvider.getRowCountMDBTable(DataProvider.MDBTABLE_BEANET1));
//            } catch (Exception e) {
//                Log.e(MainActivity.LOGNAME + "\\update\\convPiAd", e.toString());
//            }
            dataProvider.insertPinDataInSQLDB(insertData, UpdateActivity.this);
//            dataProvider.quickAnDDirtyInsert(insertData, UpdateActivity.this);
        }

////////////////////////////////////////////////////////////////////////////////////////////////////


        // todo this is a quick and dirty method, convert it to use dataprovider, and through dataprovider mdbManager and DatabaseManager
        //

        /**
         * Convert rest pin data.
         *
         * @throws IOException the io exception
         */
        void ConvertQuickAndDirty() throws IOException {

//            try {
            Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "start reading mdb");

//                    createFileFromInputStream(getAssets().open("ATDCTAP2.MDB"));
//                    getAssets().open("ATDCTAP2.MDB");


            Database mdb = new DatabaseBuilder().setFile(new File(getApplicationInfo().dataDir + "/" + "ATDCTAP2.MDB"))
                    .setReadOnly(true)
                    .open();

            Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "start reading table");
            Table table = mdb.getTable("beanet1");


//                    for (Row row: table){
//                        Log.d(MainActivity.LOGNAME + "\\update\\convPiAd","row:" + row);
//                    }
//                    Cursor cursor = CursorBuilder.createCursor(table);
//
//                    for (Row row : cursor){
//
//                    }


//                    db.delete(TABLE_NOW_PLAYING, null, null);

            // Check which column holds the IDs
//                    Long index = cursor.getColumnIndex(COL_ID);


//            } catch (IOException e) {
//                Log.e(MainActivity.LOGNAME + "\\update\\convPiAd", "error:" + e.getMessage());
//            }

//            int lastID = 0;

            DatabaseManager dm = new DatabaseManager(UpdateActivity.this, false);
            SQLiteDatabase sqlitedb = dm.getWritable();
            messageChanged = true;
            TaskProgress publishTask = new TaskProgress(0, dataProvider.getRowCountMDBTable(DataProvider.MDBTABLE_BEANET1),
                    getString(R.string.update_action3_generaldata));

//            try {
            sqlitedb.delete("beanet1", null, null);

            Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "Start inserting");

            sqlitedb.beginTransaction();
            String sql = "Insert into beanet1 (_id,adres,betaalauto,CertCode,ClientName,datacommadre,datuminstall,Euro_Install_Num,Euro_Opmerkingen,FAX,GW,HoofdkantoorNaam,Installatie,InstallatieDatum,kassanum,MAC,MASK,MerchantID,Naam,nuaadres,pinpadserie,plaats,Portnumber,Postcode,PUKGSM,REF,telefoon,TID,TIDAWL,typebetaal1,verkooppuntc,displayName,shopNR,tmsNR) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            SQLiteStatement insert = sqlitedb.compileStatement(sql);
            Cursor cursor = CursorBuilder.createCursor(table);
//                firstProgressMessage = new StringBuilder().append("Actie 3 van 3: verwerken van adressenBeheerTD gegevens - ");
//                secondProgressMessage = new StringBuilder().append('/').append(table.getRowCount()).append(" verwerkt");

            int id = 0;
            ComputeName computeName;
            for (Row row : cursor) {
                computeName = new ComputeName(row.getString(DataProvider.MDB_COLUMNS[HOOFDKETENCOLUMN]), row.getString(DataProvider.MDB_COLUMNS[NAAMCOLUMN]));
//                    Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "id=" + id +
//                            ", hk="+row.getString(columns[HOOFDKETENCOLUMN]) +
//                            ", nm=" + row.getString(columns[NAAMCOLUMN]) +
//                            ", displaynaam=" + computeName.getDisplayname() +
//                            ", shopNR=" + computeName.getShopNR()
//                    );
                publishTask.setProgressCounter(id);
                publishProgress(publishTask);
                if (!computeName.isHqAndNameEmpty()) { // dont add the empty ones
                    insert.bindString(1, "" + id);
                    int numberOfColumns = DataProvider.MDB_COLUMNS.length;
                    for (int columnCounter = 2; columnCounter < (numberOfColumns + 2); columnCounter++) { // 2 vanwege de eerste twee: hoofdketencolumn en naamColumn
//                        Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "cc = " + columnCounter + " - " + columns[columnCounter-2] + " = " + convertToString(row.get(columns[columnCounter-2])) );

                        insert.bindString(columnCounter, convertToString(row.get(DataProvider.MDB_COLUMNS[columnCounter - 2])));
                    }
                    insert.bindString((numberOfColumns + 2), computeName.getDisplayname());
                    insert.bindString((numberOfColumns + 3), computeName.getShopNR());
                    String TMSnr = tmsRefMap.get(row.getInt("REF"));
                    if (TMSnr == null) {
//                        Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "id: "+id+" is null");
                        insert.bindNull((numberOfColumns + 4));
                    } else {
                        insert.bindString((numberOfColumns + 4), tmsRefMap.get(row.getInt("REF")));
                    }


                    insert.execute();
                }
                id++;

            }
            sqlitedb.setTransactionSuccessful();
            Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "done inserting");

//            } catch (Exception e) {
//                Log.e(MainActivity.LOGNAME + "\\update\\convPiAd", e.toString());
//            } finally {
            sqlitedb.endTransaction();
//            }

//            List<String> resultList = new ArrayList<>();
//            android.database.Cursor cursor = sqlitedb.rawQuery("select tmsNR from beanet1", null);
//            // looping through all rows and adding to list
//            if (cursor.moveToFirst()) {
////            Log.d(MainActivity.LOGNAME + "\\DH\\exec","first cursor");
//                do {
//                    resultList.add(cursor.getString(0));
//                } while (cursor.moveToNext());
//            }
//            Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "#tms nr"+resultList.size());
//            Log.d(MainActivity.LOGNAME + "\\update\\convPiAd", "id 123"+resultList.get(123));
//            // closing connection
//            cursor.close();

//            try {
//                mdb.close();
//            } catch (IOException e) {
//                Log.e(MainActivity.LOGNAME + "\\update\\convPiAd", e.toString());
//            }
        }

    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
