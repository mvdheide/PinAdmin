package eu.centric.pinadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.List;
import java.util.Map;

import eu.centric.pinadmin.adapter.DetailsExpandableListAdapter;
import eu.centric.pinadmin.data.DataProvider;
import eu.centric.pinadmin.util.Details;

//import java.util.ArrayList;


/**
 * The Activity DetailShowActivity only displays the detailed data of a pinpad in an expanableListView.
 * The edits can be made by clicking on "Wijzigen", the {@link DetailEditActivity} is then started.
 * <p>
 * The gui contains an expandableListView and two buttons for the previous and next pinpad.
 *
 * @author MHeide
 * @since 30-11-‎2017
 */
public class DetailShowActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * The dataprovider Object is a object to get/set information. It's a singleton class which act
     * as a proxy class. Only the dataprovider class has access to datasources (sqlite db, vpn,
     * onedrive, etc).
     */
    private DataProvider dataProvider;
    /**
     * An ExpandableListView which displays all the data from the current pinpad
     */
    private ExpandableListView detailsExpanableListView;
    /**
     * the global integer position is the position in the array {@link filteredpinpadIDArray}.
     * This points to the id of the pinpad whose details are displayed.
     */
    private int position;
    /**
     * The array with long values is the list with the ID's of the pinpads. Only the pinpads whose
     * displayed in {@link MainActivity} in the {@link MainActivity#overviewPinpadsListView}.
     * The integer {@link position} points to the id of the pinpad whose details are displayed.
     */
    private long[] filteredpinpadIDArray;
    /**
     * A botton to go to the previous pinpad (from {@link filteredpinpadIDArray}).
     * {@link position} is decreased by 1.
     */
    private Button previousButton;
    /**
     * A botton to go to the next pinpad (from {@link filteredpinpadIDArray}).
     * {@link position} is increased by 1.
     */
    private Button nextButton;

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Perform initialization of all fragments and loaders. Init and configure the intent data,
     * expandableListView and the buttons
     *
     * @param savedInstanceState the Activity previous frozen state, if there was one.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_show);
        dataProvider = DataProvider.getInstance();

        // get data from the intent
        Intent intent = getIntent();
        position = intent.getIntExtra(getString(R.string.position), 0);
        filteredpinpadIDArray = intent.getLongArrayExtra(getString(R.string.filteredpinpadIDArray));
        Log.d(MainActivity.LOGNAME + "\\detShAc\\oncre", "pos and filteredpinpadIDArray[pos] :" + position + "/" + filteredpinpadIDArray[position]);

        // init and fill the exandable ListView
        detailsExpanableListView = (ExpandableListView) findViewById(R.id.detailShowExpandableListView);
        fillListView();

        // configure the buttons
        previousButton = (Button) findViewById(R.id.detailShowPreviousPinpadButton);
        nextButton = (Button) findViewById(R.id.detailShowNextPinpadButton);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        if (position == (filteredpinpadIDArray.length - 1)) {
            nextButton.setEnabled(false);
        }
        if (position == 0) {
            previousButton.setEnabled(false);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method onCreateOptionsMenu creates/inflates the menu in the appbar.
     *
     * @param menu the options menu in which you place the items
     * @return must be true to display the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflat = getMenuInflater();
        inflat.inflate(R.menu.mail_change, menu);
        return true;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method onCreateOptionsMenu creates/inflates the menu in the appbar.
     * the option android.R.id.home is the backbutton in the appbar. this option is added so
     * clicking on this button is the same as clicking on the back button (the one next to the
     * home button). It also is used for starting the activity {@link DetailEditActivity} when
     * clicked in the menu on "Wijzigen".
     *
     * @param menuItem the options menu in which you place the items
     * @return must be true to display the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        boolean returnValue = true;
        switch (menuItem.getItemId()) {
            case R.id.send_change_menu_item:
                Intent intent = new Intent(DetailShowActivity.this, DetailEditActivity.class);
//                intent.putExtra("_id", filteredpinpadIDArray[position]);
                intent.putExtra(getString(R.string.position), position);
                intent.putExtra(getString(R.string.filteredpinpadIDArray), filteredpinpadIDArray);
                startActivityForResult(intent, 1);
                break;
            case android.R.id.home:
                onBackPressed();
            default:
                returnValue = super.onOptionsItemSelected(menuItem);
                break;
        }
        return returnValue;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method fills the expandable listview with data depending on the current pinpad. Also
     * expand the first group.
     * ({@link filteredpinpadIDArray}[{@link position}])
     */
    private void fillListView() {

        Map<String, List<Details>> currentPinpadDetailsMap = dataProvider.getCurrentPinpadDetailsMap(this.getBaseContext().getAssets(), filteredpinpadIDArray[position]);
        DetailsExpandableListAdapter listAdapter = new DetailsExpandableListAdapter(this,
                currentPinpadDetailsMap);
        detailsExpanableListView.setAdapter(listAdapter);
        detailsExpanableListView.expandGroup(0); // expand "algemene gegevens"
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method is called when the user clicks on one of the buttons. The two buttons are:
     * a button for the previous and the next pinpad.
     *
     * @param clickedButton the button on which the user clicked
     */
    @Override
    public void onClick(View clickedButton) {
        switch (clickedButton.getId()) {
            case R.id.detailShowPreviousPinpadButton:
                Log.d(MainActivity.LOGNAME + "\\detShAc\\onClick", "een kassa nr -");
                if (position == (filteredpinpadIDArray.length - 1)) {
                    nextButton.setEnabled(true);
                }
                if (position != 0) {
                    position--;
                    fillListView();
                }
                if (position == 0) { // zero after decreasing position
                    previousButton.setEnabled(false);
                }
                break;
            case R.id.detailShowNextPinpadButton:
                Log.d(MainActivity.LOGNAME + "\\detShAc\\onClick", "een kassa nr +");
                if (position == 0) {
                    previousButton.setEnabled(true);
                }
                if (position != (filteredpinpadIDArray.length - 1)) {
                    position++;
                    fillListView();
                }
                if (position == (filteredpinpadIDArray.length - 1)) {
                    nextButton.setEnabled(false);
                }
                break;
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
