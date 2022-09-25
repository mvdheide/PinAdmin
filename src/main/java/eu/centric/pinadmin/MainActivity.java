package eu.centric.pinadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import eu.centric.pinadmin.adapter.OverviewPinpadsCursorAdapter;
import eu.centric.pinadmin.data.DataProvider;
import eu.centric.pinadmin.fragment.FilterFragment;
import eu.centric.pinadmin.fragment.TableHeaderFragment;
import eu.centric.pinadmin.util.DateUtil;

/**
 * The MainActivity class is the entry point for this app.
 * <p>
 * It displays all pinpads in the AdressenBeheerTD database (only the basic information: Shop name,
 * place, till nr, TMS). With the filterfragment a subset can be viewed.
 * <p>
 * First is checked is the database is uptodate.
 * <p>
 * When a changes is made in the fields of the filterFragment, or when a tableheader button is
 * pressed, the overview with the pinpads information is adjusted.
 * When a pinpad is clicked, the details are shown (via the DetailShowActivity).
 * <p>
 * The gui contains a search/filter fragment (for filtering), tableHeader buttons (for ordening),
 * a listview (with the basic pin data) and a textview (displays the number of pinpads in the
 * listview).
 *
 * @author MHeide
 * @since 12-4-2017
 */
public class MainActivity extends AppCompatActivity implements FilterFragment.OnFragmentInteractionListener, TableHeaderFragment.OnFragmentInteractionListener {

    // TODO: 29-11-2017 create a throw/catch system
    // TODO: 29-11-2017 check names (methods, variables, layout files, etc)
    // todo stringbuffers init op geschatte grootte
    // todo add sources 26. check if inheritdoc is working.

    /**
     * The constant LOGNAME is used in the logcat entries as project name.
     */
    public static final String LOGNAME = "PiAdLog";
    /**
     * The constant NUMBER_OF_FILTERFIELDS. This number is used how many filter field there has to
     * be. If this value is changed, change {@link DataProvider#FILTER_FIELDS_COLUMN_NAMES},
     * add/remove an editText at {@link FilterFragment#onViewCreated(View, Bundle)} and
     * {@link R.layout#fragment_filter_fields}
     */
    public static final int NUMBER_OF_FILTERFIELDS = 3;
    /**
     * The constant NUMBER_OF_FILTERFIELDS gives the amount of days to update. When the app is
     * started, a check is preformed to check how long a database update has been done. When this is
     * more than this value, the user is asked to start the {@link UpdateActivity}.
     * It has to be negative.
     */
    private static final int AMOUNT_DAYS_TO_UPATE = -7;
    /**
     * The constant RESET_DB can be used to reset the database. If the value is true, the database
     * is reset during startup.
     */
    private static final boolean RESET_DB = false;
    /**
     * the dataprovider Object is a object to get/set information. It's a singleton class which act
     * as a proxy class. Only the dataprovider class has access to datasources (sqlite db, vpn,
     * onedrive, etc)
     */
    private DataProvider dataProvider;
    /**
     * The global listview overviewListView displays all the basic pinpad information
     * depending on the filters.
     */
    private ListView overviewListView;
    /**
     * The global filterFragment is a fragment and is used to filter the data of the
     * {@link overviewListView}. It contains {@link NUMBER_OF_FILTERFIELDS}
     * Autocomplete EditTexts.
     */
    private FilterFragment filterFragment;
    /**
     * The global TableHeaderFragment is a fragment and contains the header buttons, handles the
     * clicks and ordering of the {@link overviewListView}.
     */
    private TableHeaderFragment tableHeaderFragment;
    /**
     * This textfield is displayed in the bottom of the screen and displays the number of pinpads
     * listed in the overviewListView
     */
    private TextView overviewCounterTextView;


    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState the Activity previous frozen state, if there was one.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(MainActivity.LOGNAME + "\\Main\\Oncreate", "get the dataProvider object");
        // get the dataProvider object
        dataProvider = DataProvider.getInstance();
//        Log.d(MainActivity.LOGNAME + "\\Main\\Oncreate", "get DP instance");
        //the first time, the dataProvider.intitDataProvider() method has to be called.
        dataProvider.intitDataProvider(MainActivity.this, RESET_DB);
        Log.d(MainActivity.LOGNAME + "\\Main\\Oncreate", "continue with the rest of onCreate");

        // init global objects
        filterFragment = (FilterFragment) getSupportFragmentManager().findFragmentById(R.id.filterFieldsFragment);
        tableHeaderFragment = (TableHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.tableHeaderFragment);
        overviewListView = (ListView) findViewById(R.id.overviewListView);
        overviewCounterTextView = (TextView) findViewById(R.id.overviewCounterTextView);


        // set onClickListener on items from the listview, to go to the details
        overviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long[] filteredpinpadIDArray = getFilteredpinpadIDArray();
                Log.d(MainActivity.LOGNAME + "\\Main\\Oncreate", "clicked on id" + filteredpinpadIDArray[position]);
                Intent intent = new Intent(MainActivity.this, DetailShowActivity.class);
                intent.putExtra(getString(R.string.position), position);
                intent.putExtra(getString(R.string.filteredpinpadIDArray), filteredpinpadIDArray);
                startActivity(intent);
            }
        });

        // execute the ReadDBDataTask in the background to see if an update is recommanded
        new ReadDBDataTask().execute();
        // fill the listview
        populateOverviewPinpadsListView();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method getFilteredpinpadIDArray() loops through the {@link overviewListView} and
     * get the ID's from every pinpad entry.
     *
     * @return An array with long values, the ID's from every entry in the
     * {@link overviewListView}.
     */
    private long[] getFilteredpinpadIDArray() {

        int cursorCount = overviewListView.getAdapter().getCount();
        long[] filteredpinpadIDArray = new long[cursorCount];
        for (int counter = 0; counter < cursorCount; counter++) {
            filteredpinpadIDArray[counter] = overviewListView.getAdapter().getItemId(counter);
        }
        return filteredpinpadIDArray;
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
        inflat.inflate(R.menu.main_menu, menu);
        return true;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The hook onOptionsItemSelected is called when an item in options menu is selected.
     *
     * @param menuItem the menu item that was selected.
     * @return return return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        boolean returnValue = true;
        switch (menuItem.getItemId()) {
            case R.id.clear_menu_item: // the menu item "maak velden leeg"
                filterFragment.clearAllautoCompleteTextViews();
                filterFragment.fillFilterFieldsWithAutoCompleteData(true);
                populateOverviewPinpadsListView();
                break;
            case R.id.update_menu_item: // the menu option "update Database"
                startUpdateActivity();
                break;
            case R.id.help_menu_item: // the menu option "help"
                startHelpActivity();
                break;
            default:
                returnValue = super.onOptionsItemSelected(menuItem);
        }
        return returnValue;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method startHelpActivity starts the {@link HelpActivity}. This is done with
     * {@link #startActivity(Intent)}.
     */
    private void startHelpActivity() {
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(intent);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method startHelpActivity starts the {@link UpdateActivity}.  This is done with
     * {@link #startActivityForResult(Intent, int)}.
     */
    private void startUpdateActivity() {
        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
        startActivityForResult(intent, 1);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method fills the {@link overviewListView} with data. This depending on the
     * text entered in the filter fields and which tableHeader buttons are clicked on.
     * It also sets in the textview in the bottom of the screen, the number of pinpads in the listview
     */
    public void populateOverviewPinpadsListView() {

        Cursor overviewPinpadsCursor = dataProvider.getResultsCursor(filterFragment.getEnteredText(),
                tableHeaderFragment.getButtonClicked(),
                tableHeaderFragment.getAscend());
        // Setup cursor adapter using cursor
        OverviewPinpadsCursorAdapter overviewPinpadsAdapter = new OverviewPinpadsCursorAdapter(this, overviewPinpadsCursor);
        // Attach cursor adapter to the ListView
        overviewListView.setAdapter(overviewPinpadsAdapter);
        String numberOfPinpadString = getString(R.string.numberOfPinpadsInOverview) + overviewPinpadsAdapter.getCount();
        overviewCounterTextView.setText(numberOfPinpadString);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Called when a activity you lauched exits, giving the requestcode you started it with, the
     * result code an any additional data.
     * In this case, if {@link UpdateActivity} exits (the only activity started with
     * {@link #startActivityForResult(Intent, int)}) and the result code is
     * {@link android.app.Activity#RESULT_OK}, the filter fields are cleared and the
     * {@link overviewListView} is repopulate.
     *
     * @param requestCode the integer value supplied by startActivityForResult, allowing to identify
     *                    the source. If the result is {@link android.app.Activity#RESULT_OK},
     *                    then the filter fields are cleared and the {@link overviewListView}
     *                    is repopulate.
     * @param resultCode  the integer result code value set by the started Activity with the
     *                    setResult method.
     * @param data        the intent to pass data between activities.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(MainActivity.LOGNAME + "\\Main\\onActRes", "let's reload");
            filterFragment.clearAllautoCompleteTextViews();
            filterFragment.fillFilterFieldsWithAutoCompleteData(true);
            populateOverviewPinpadsListView();
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Display a AlertDialog with the question if the user wants to update the database, by
     * starting the {@link UpdateActivity}.
     * This dialog is displayed depending on the {@link AMOUNT_DAYS_TO_UPATE}.
     */
    private void displayUpdateDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage(R.string.main_update_longer_than_week);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                R.string.Ja,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
                        startUpdateActivity();
                    }
                });

        builder1.setNegativeButton(
                R.string.Nee,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.
                setIcon(android.R.drawable.ic_dialog_info).
                setTitle(R.string.Update_queston);

//        AlertDialog alert = builder1.create();
//        alert.show();
        builder1.create().show();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The inner class ReadDBDataTask is done in the background. This class checks at the start of
     * the app, if the database is updated longer than {@link AMOUNT_DAYS_TO_UPATE} days. Also the
     * method {@link FilterFragment#fillFilterFieldsWithAutoCompleteData(boolean)}is called to fill
     * the filter fields with autocomplete data.
     */
    private class ReadDBDataTask extends AsyncTask<Void, Void, Object> {

        /**
         * The displayUpdateDialog value is used to determine if the update dialog is displayed.
         */
        boolean displayUpdateDialog = false;


        /**
         * This Method checks if the date the database is updated, is longer than
         * {@link AMOUNT_DAYS_TO_UPATE} days.
         *
         * @param voids no params..
         * @return null..
         */
        @Override
        protected Object doInBackground(Void... voids) {

            displayUpdateDialog = DateUtil.isDateLongerThanDaysAgo(AMOUNT_DAYS_TO_UPATE, dataProvider.getLocalDBTimeStamp());
            Log.d(MainActivity.LOGNAME + "\\Main\\ReadDBT", "The timestamp of the db is "
                    + (displayUpdateDialog ? "longer" : "shorter") + " than "
                    + (-1 * AMOUNT_DAYS_TO_UPATE)
                    + " days ago.");
            return null;
        }

////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * This method is called when the method doInBackground is done. it calls
         * {@link MainActivity#displayUpdateDialog()} to ask the user to update.
         * last, it calls a method to fill the filter fields with autocomplete data
         *
         * @param result the resulting Object
         */
        @Override
        protected void onPostExecute(Object result) {
            if (displayUpdateDialog) {
                displayUpdateDialog();
            }
            super.onPostExecute(result);
            filterFragment.fillFilterFieldsWithAutoCompleteData(false);
        }
    }


}
