package eu.centric.pinadmin.data;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Table;

import java.io.IOException;

// todo javadocen

/**
 *
 * @author MHeide
 * @since 4-12-2018
 */
class MDBTableHandler {

    private final Table table;
    private Cursor cursor;

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instantiates a new Mdb table handler.
     *
     * @param tableParam the table param
     * @throws IOException the io exception
     */
    MDBTableHandler(final Table tableParam) throws IOException {

        table = tableParam;
        cursor = CursorBuilder.createCursor(table);
        cursor.beforeFirst();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets row count mdb table.
     *
     * @return the row count mdb table
     */
    int getRowCountMDBTable() {

        return table.getRowCount();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Next cursor mdb row boolean.
     *
     * @return the boolean
     * @throws IOException the io exception
     */
    boolean nextCursorMDBRow() throws IOException {

        return cursor.moveToNextRow();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets int mdb cell.
     *
     * @param columnName the column name
     * @return the int mdb cell
     * @throws IOException the io exception
     */
    Integer getIntMDBCell(final String columnName) throws IOException {

        return (Integer) cursor.getCurrentRow().get(columnName);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets string mdb cell.
     *
     * @param columnName the column name
     * @return the string mdb cell
     * @throws IOException the io exception
     */
    String getStringMDBCell(final String columnName) throws IOException {

        return (String) cursor.getCurrentRow().get(columnName);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets object mdb cell.
     *
     * @param columnName the column name
     * @return the object mdb cell
     * @throws IOException the io exception
     */
    Object getObjectMDBCell(final String columnName) throws IOException {

        return cursor.getCurrentRow().get(columnName);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
}
