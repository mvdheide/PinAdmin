package eu.centric.pinadmin.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import eu.centric.pinadmin.R;
import eu.centric.pinadmin.data.DataProvider;

/**
 * This id an adapter class between the data from the database (a cursor) and the listView.
 *
 * @author MHeide
 * @since 6-8-2017
 */
public class OverviewPinpadsCursorAdapter extends CursorAdapter {

    /**
     * An array with the ID's of the AutoCompleteFields. It can be used for the findbyID and in the
     * method OnClick. The order of these ID should be the same as the layout file.
     */
    private final static int[] viewIDArray = {
            R.id.overviewShopTextView,
            R.id.overviewPlaceTextview,
            R.id.overviewFiliaalNRTextview,
            R.id.overviewKassaNRTextView,
            R.id.overviewTMSNRTextview};

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instantiates a new OverviewPinpadsCursorAdapter.
     *
     * @param context Interface to application's global information.
     * @param cursor  The cursor from which to get the data.
     */
    public OverviewPinpadsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The newView method is used to inflate a new view and return it, you don't bind any data to the view at this point.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the correct position.
     * @param parent The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.main_listview_row, parent, false);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The bindView method is used to bind all data to a given view.
     *
     * @param view Existing view, returned earlier by newView.
     * @param context Interface to application's global information.
     * @param cursor The cursor from which to get the data. The cursor is already moved to the correct position.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Extract properties from cursor and populate fields with extracted properties
        int numberOfColumns = viewIDArray.length;
        for (int count = 0; count < numberOfColumns; count++){
            ((TextView)view.findViewById(viewIDArray[count])).setText(cursor.getString(cursor.getColumnIndexOrThrow(DataProvider.RESULT_COLUMS[count])));
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
