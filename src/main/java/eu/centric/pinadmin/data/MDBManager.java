package eu.centric.pinadmin.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import eu.centric.pinadmin.MainActivity;
import eu.centric.pinadmin.R;

// todo javadocen
// todo code cleanup (commented code verwijderen)

/**
 * @author MHeide
 * @since 1 -12-2017
 */
class MDBManager {

    /**
     * The Mdb file name.
     */
    static final String mdbFileName = "ATDCTAP2.MDB";
    /**
     * The Tms ref sa.
     */


    private final String dataDir;
    /**
     * The Mdb.
     */
    private Database mdb = null;
    private MDBTableHandler[] tables;

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instantiates a new Mdb manager.
     *
     * @param dataDirParam the data dir param
     */
    MDBManager(final String dataDirParam) {
        dataDir = dataDirParam;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Open data base.
     *
     * @throws IOException the io exception
     */
    void openMDBFile() throws IOException {

        try {
            File mdbFile = new File(dataDir + "/" + mdbFileName);
            Log.d(MainActivity.LOGNAME + "\\update\\opendb", "opening file with mod date:" + new Date(mdbFile.lastModified()));
            mdb = new DatabaseBuilder().setFile(mdbFile)
                    .setReadOnly(true)
                    .open();
        } catch (IOException e) {
            Log.e(MainActivity.LOGNAME + "\\update\\opendb", "error opening access file:" + e.getMessage());
        }
        final int numberOfTables = DataProvider.MDB_TABLE_NAMES.length;
        tables = new MDBTableHandler[numberOfTables];
        for (int counter = 0; counter < numberOfTables; counter++) {
            tables[counter] = new MDBTableHandler(mdb.getTable(DataProvider.MDB_TABLE_NAMES[counter]));
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Close access file.
     */
    void closeMDBFile() {

        try {
            mdb.close();
        } catch (IOException e) {
            Log.e(MainActivity.LOGNAME + "\\update\\closeAcc", "error closing access file" + e.toString());
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets row count mdb table.
     *
     * @param tableNumber the table number
     * @return the row count mdb table
     */
    int getRowCountMDBTable(final int tableNumber) {

        return tables[tableNumber].getRowCountMDBTable();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Next cursor mdb row boolean.
     *
     * @param tableNumber the table number
     * @return the boolean
     * @throws IOException the io exception
     */
    boolean nextCursorMDBRow(final int tableNumber) throws IOException {

        return tables[tableNumber].nextCursorMDBRow();
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
    Integer getIntMDBCell(final int tableNumber, final String columnName) throws IOException {

        return tables[tableNumber].getIntMDBCell(columnName);
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
    String getStringMDBCell(final int tableNumber, final String columnName) throws IOException {

        return tables[tableNumber].getStringMDBCell(columnName);
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
    Object getObjectMDBCell(final int tableNumber, final String columnName) throws IOException {

        return tables[tableNumber].getObjectMDBCell(columnName);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    boolean fileCopy(Context updateContext, Uri content_describer) throws IOException {

        boolean copySucces;

        Log.d(MainActivity.LOGNAME + "\\update\\fileCopy", "start coping MDB file");

        ProgressDialog convertDialog = new ProgressDialog(updateContext);
        convertDialog.setTitle(updateContext.getString(R.string.update));
        convertDialog.setMessage(updateContext.getString(R.string.update_action1_download_copy));
        convertDialog.setMax(100);
        convertDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        convertDialog.setCancelable(false);
        convertDialog.show();

        InputStream in = null;
        OutputStream out = null;
        try {
            // open the user-picked file for reading:
            in = updateContext.getContentResolver().openInputStream(content_describer);
            // open the output-file:
            out = new FileOutputStream(new File(dataDir + File.separator + mdbFileName));
            // copy the content:
            byte[] buffer = new byte[1024];
            int len;
            convertDialog.setProgress(50);
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            copySucces = true;
            // Contents are copied!
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null){
                out.close();
            }
            Log.d(MainActivity.LOGNAME + "\\update\\copyMDB", "db is gekopieert van:" + content_describer.getLastPathSegment() + ", naar:" + out);
        }
        convertDialog.dismiss();
        return copySucces;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
