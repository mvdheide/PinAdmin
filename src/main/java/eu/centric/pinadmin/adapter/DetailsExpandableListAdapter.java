package eu.centric.pinadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.centric.pinadmin.R;
import eu.centric.pinadmin.util.Details;


/**
 * This is an adapter class between the data from the database (currentPinpadDetailsMap) and an
 * expandable listview.
 * It overrides the class BaseExpandableListAdapter and add the funcionality to use
 * {@link currentPinpadDetailsMap} as data input.
 *
 * @author MHeide
 * @since 25-10-2017
 */
public class DetailsExpandableListAdapter extends BaseExpandableListAdapter{

    /**
     *  Interface to application's global information.
     */
    private final Context context;
    /**
     * a list with the titles from {@link currentPinpadDetailsMap}
     * (The bold Strings in the expandable listview: "algemene gegevens","contact gegevens",
     * "verbinding gegevens",etc). These are the keys of currentPinpadDetailsMap.
     */
    private final List<String> groupTitleList;
    /**
     * the data from the database with the details for this pinpad.
     * The keys of the Map contains groups ("algemene gegevens","contact gegevens",
     * "verbinding gegevens",etc). Each map value consist of a list of Details (each Detail
     * Object contains the title, the number for the array {@link currentEditTextViewIDArray},
     * the original and changed data).
     */
    private final Map<String ,List<Details>> currentPinpadDetailsMap;

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instantiates a new Custom expandable list adapter.
     *
     * @param contextParam  Interface to application's global information.
     * @param currentPinpadDetailsMapParam  contains the data from the database with the details for this pinpad.
     * The keys of the Map contains groups ("algemene gegevens","contact gegevens",
     * "verbinding gegevens",etc). Each map value consist of a list of Details (each Detail
     * Object contains the title, the number for the array {@link currentEditTextViewIDArray},
     * the original and changed data).
     */
    public DetailsExpandableListAdapter(final Context contextParam,
                                        final Map<String, List<Details>> currentPinpadDetailsMapParam){
//        Log.d(MainActivity.LOGNAME + "\\costumela\\const", "start" + contextParam.toString() + '-' + listDataChildParam.size()+'-'+listDataHeaderParam.size()+'-'+dataFieldParam+'-'+titleFieldParam);

        context = contextParam;
        currentPinpadDetailsMap = currentPinpadDetailsMapParam;
        groupTitleList = extractTitles();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGroupCount() {
        return groupTitleList.size();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        int size = 0;
        if (currentPinpadDetailsMap != null){
            size = currentPinpadDetailsMap.get(groupTitleList.get(groupPosition)).size();
        }
        return size;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getGroup(int groupPosition) {
        return groupTitleList.get(groupPosition);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return currentPinpadDetailsMap.get(groupTitleList.get(groupPosition)).get(childPosition);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     * Set the title of every group.
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = getGroup(groupPosition).toString();
        if (convertView == null){
            LayoutInflater inflat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflat.inflate(R.layout.detail_show_title_item, null);
        }
        TextView txtListHeader = convertView.findViewById(R.id.detailHeaderTextView);
        txtListHeader.setText(headerTitle);
        return convertView;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     * Inflate a row from the expandable listview and sets the data (from the detail obeject).
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String title = ((Details)getChild(groupPosition, childPosition)).getTitle();
        String originalData = ((Details)getChild(groupPosition, childPosition)).getOriginalData();

        if (convertView == null){
            LayoutInflater inflat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflat.inflate(R.layout.detail_show_detailrow_item, null);
        }
        ((TextView) convertView.findViewById(R.id.detailShowTitleItemTextView)).setText(title);
        ((TextView) convertView.findViewById(R.id.detailShowDataItemTextView)).setText(originalData);
        return convertView;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method creates a list with the titles from {@link currentPinpadDetailsMap}
     * (The bold Strings in the expandable listview: "algemene gegevens","contact gegevens",
     * "verbinding gegevens",etc). These are the keys of currentPinpadDetailsMap.
     *
     * @return returns a list with group titles
     */
    private List<String> extractTitles() {

        List<String> titles = new ArrayList<>();
        for (Map.Entry<String, List<Details>> detailRowsPerTitle : currentPinpadDetailsMap.entrySet()) {
            titles.add(detailRowsPerTitle.getKey());
        }
        return titles;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}