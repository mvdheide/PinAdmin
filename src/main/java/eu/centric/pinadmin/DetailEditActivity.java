package eu.centric.pinadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.centric.pinadmin.data.DataProvider;
import eu.centric.pinadmin.util.Details;

/**
 * The DetailEditActivity offers the possibility to send a email with changes. The changes aren't
 * saved, only mailed.
 * <p>
 * In this Activity, the values are entered in EditText fields. The user can go to the previous or
 * next pinpad by pressing the buttons. (This list of pinpads is the same list as
 * overviewPinpadsListView for MainActivity). All changes are put in the mail, from all the altered
 * pinpads.
 * When a EditText contains a edited EditText, the title will be blue. When the original data is
 * set back, the blue title becomes black again.
 * <p>
 * The gui contains a column with labels and a column with EditText. At the bottom three bottoms for
 * previous or next pinpad and a button to email the changes.
 *
 * @author MHeide
 * @since 30-11-‎2017
 */
public class DetailEditActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * The dataprovider Object is a object to get/set information. It's a singleton class which act
     * as a proxy class. Only the dataprovider class has access to datasources (sqlite db, vpn,
     * onedrive, etc).
     */
    private DataProvider dataProvider;
    /**
     * The number of rows with a pair of TextView and a EditText.
     */
    private int numberOfItems;
    /**
     * This Array contains the view ID's of the EditText's. This way all editText can be found
     * with a for loop and findByID.
     */
    private int[] currentEditTextViewIDArray;
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
     * This data contains the data from the database with the details for this pinpad.
     * The keys of the Map contains groups ("algemene gegevens","contact gegevens",
     * "verbinding gegevens",etc). Each map value consist of a list of Details (each Detail
     * Object contains the title, the number for the array {@link currentEditTextViewIDArray},
     * the original and changed data).
     */
    private Map<String, List<Details>> currentPinpadDetailsMap;
    /**
     * A map to keep track of changes from multiple pinpads. When a change is made, the change is
     * added to this map. The keys of this map are the ID of a pinpad
     * ({@link filteredpinpadIDArray}[{@link position}]). The values from the map have the same
     * structure as the {@link currentPinpadDetailsMap}. So this map contains the
     * {@link currentPinpadDetailsMap} maps from every pinpad where a change is made.
     */
    private Map<Long, Map<String, List<Details>>> changedDataMultiplePinpadsMap;
    /**
     * A button to go to the previous pinpad (from {@link filteredpinpadIDArray}).
     * {@link position} is decreased by 1.
     */
    private Button previousButton;
    /**
     * A button to go to the next pinpad (from {@link filteredpinpadIDArray}).
     * {@link position} is increased by 1.
     */
    private Button nextButton;

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState the Activity previous frozen state, if there was one.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit);
        dataProvider = DataProvider.getInstance();

        // get data from de intent (from DetailShowActivity)
        Intent intent = getIntent();
        position = intent.getIntExtra(getString(R.string.position), 0);
        filteredpinpadIDArray = intent.getLongArrayExtra(getString(R.string.filteredpinpadIDArray));

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changedDataMultiplePinpadsMap = new LinkedHashMap<>();
        changeCurrentPinpadData(filteredpinpadIDArray[position]);

        // configure buttons
        previousButton = (Button) findViewById(R.id.detailEditPreviousPinpadButton);
        nextButton = (Button) findViewById(R.id.detailEditNextPinpadButton);
        // enable/disable the button with the first/last pinpad
        if (position == (filteredpinpadIDArray.length - 1)) {
            nextButton.setEnabled(false);
        }
        if (position == 0) {
            previousButton.setEnabled(false);
        }
        //set onClickListeners
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        findViewById(R.id.sendChangeButton).setOnClickListener(this);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method onCreateOptionsMenu creates/inflates the menu in the appbar.
     * the option android.R.id.home is the backbutton in the appbar. this option is added so
     * clicking on this button is the same as clicking on the back button (the one next to the
     * home button)
     *
     * @param menuItem the options menu in which you place the items
     * @return must be true to display the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Take care of popping the fragment back stack or finishing the activity as appropriate. Checks
     * first if something is changed.
     */
    @Override
    public void onBackPressed() {
        if (isSomethingChanged()) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailEditActivity.this);
            builder1.setMessage(R.string.detail_exit_with_change);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    getString(R.string.Ja),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });

            builder1.setNegativeButton(
                    getString(R.string.Nee),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder1.
                    setIcon(android.R.drawable.ic_dialog_info).
                    setTitle(R.string.Update_queston);

            builder1.create().show();
//            AlertDialog alert11 = builder1.create();
//            alert11.show();
            Log.d(MainActivity.LOGNAME + "\\detEdAc\\onBackP", "exit without sending changes");
        } else {
            Log.d(MainActivity.LOGNAME + "\\detEdAc\\onBackP", "exit without any changes");
            super.onBackPressed();
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method changeCurrentPinpadData changes the data of the information which will be
     * displayed. It checks if this pinpad has been edited, in that case the changed data will be
     * used. Else the data from the database will be used. Afterwards the method
     * {@link DetailEditActivity#setEditTexts()}
     *
     * @param pinpadID the ID of the pinpad. for example: {@link filteredpinpadIDArray}[{@link position}]
     */
    private void changeCurrentPinpadData(long pinpadID) {
        Map<String, List<Details>> previousPinpadDetailMap = changedDataMultiplePinpadsMap.get(filteredpinpadIDArray[position]);
        if (previousPinpadDetailMap == null) {
            currentPinpadDetailsMap = dataProvider.getCurrentPinpadDetailsMap(this.getApplicationContext().getAssets(), pinpadID);
        } else {
            currentPinpadDetailsMap = previousPinpadDetailMap;
        }
        setEditText();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The Method setEditTexts removes, adds, fills them with data and configure the editText fields.
     * This is done by inflating (detailrows)
     */
    private void setEditText(){
        LinearLayout detailEditScrollLinearLayout = (LinearLayout) findViewById(R.id.detailEditScrollLinearLayout);
        // remove all children of detailEditScrollLinearLayout (if there a no children, nothing happens)
        detailEditScrollLinearLayout.removeAllViews();

        // count how many textfields are needed and create a array for the view ID's
        numberOfItems = 0;
        for (Map.Entry<String, List<Details>> row : currentPinpadDetailsMap.entrySet()) {
            numberOfItems += row.getValue().size();
        }
        currentEditTextViewIDArray = new int[numberOfItems];

        // inflate the views and configure them
        LayoutInflater inflat = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int itemIDcounter = 0;
        for (Map.Entry<String, List<Details>> detailRowsPerTitle : currentPinpadDetailsMap.entrySet()) {
            // add the title of a group
            inflat.inflate(R.layout.detail_edit_title_item, detailEditScrollLinearLayout);
            ((TextView) findViewById(R.id.detailEditGroupTitle)).setText(detailRowsPerTitle.getKey());

            // create a row with a title(itemTextView) and a datafield (edittext)
            List<Details> row = detailRowsPerTitle.getValue();
            int arraylistLength = row.size();
            for (int rowCounter = 0; rowCounter < arraylistLength; rowCounter++) {
                Details rowDetails = row.get(rowCounter);
                rowDetails.setOrderingNumber(itemIDcounter);

                // inflate and configure detailRows (the textview and the edittext)
                inflat.inflate(R.layout.detail_edit_detailrow_item, detailEditScrollLinearLayout);
                TextView itemTextView = (TextView) findViewById(R.id.detailEditLabelPlaceholder);
                EditText itemEditText = (EditText) findViewById(R.id.detailEditEdittextPlaceholder);
                itemTextView.setText(rowDetails.getTitle());
                itemEditText.setText(rowDetails.getChangedData());
                currentEditTextViewIDArray[itemIDcounter] = View.generateViewId();
                itemEditText.setId(currentEditTextViewIDArray[itemIDcounter]);
                itemTextView.setId(0);
                itemEditText.addTextChangedListener(new EditTextWithTextViewListener(itemTextView
                        ,rowDetails.getOriginalData()
                        ,rowDetails.isDataChanged()));
                if (rowDetails.isDataChanged()) {
                    itemTextView.setTextColor(Color.BLUE);
                }
                itemIDcounter++;
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method is called when the user clicks on one of the buttons. The three buttons are:
     * a button for the previous pinpad,
     * a button for the next pinpad and
     * a button to send an email with the changes
     * Before the corresponding action, first by all three buttons the method
     * {@link DetailEditActivity#setChangesCurrentPinpad()} is called to safe the changes (if any).
     *
     * @param clickedButton the button on which the user clicked
     */
    @Override
    public void onClick(View clickedButton) {
        switch (clickedButton.getId()) {
            case R.id.sendChangeButton:
                setChangesCurrentPinpad();
                if (isSomethingChanged()) {
                    sendTextMail();

                } else {
                    Snackbar.make(clickedButton
                            , R.string.detail_no_change
                            , Snackbar.LENGTH_LONG).setAction("Action", null)
                            .show();
                }
                break;
            case R.id.detailEditPreviousPinpadButton:
                if (position == (filteredpinpadIDArray.length - 1)) {
                    nextButton.setEnabled(true);
                }
                setChangesCurrentPinpad();
                Log.d(MainActivity.LOGNAME + "\\detEdAc\\onClick", "previous pinpad");
                if (position != 0) {
                    position--;
                    changeCurrentPinpadData(filteredpinpadIDArray[position]);
                }
                if (position == 0) { // zero after decreasing position
                    previousButton.setEnabled(false);
                }
                break;
            case R.id.detailEditNextPinpadButton:
                if (position == 0) {
                    previousButton.setEnabled(true);
                }
                setChangesCurrentPinpad();
                Log.d(MainActivity.LOGNAME + "\\detEDAc\\onClick", "next pinpad");
                if (position != (filteredpinpadIDArray.length - 1)) {
                    position++;
                    changeCurrentPinpadData(filteredpinpadIDArray[position]);
                }
                if (position == (filteredpinpadIDArray.length - 1)) {
                    nextButton.setEnabled(false);
                }
                break;
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method isSomethingChangedAtCurrentPinpad checks if something has been changed in the
     * editTexts. This method checks only the current pinpad
     * This has been done by looping through the editText and checking if the value match
     * with the original data. So if a edittext is altered and then the original data is entered,
     * it doesn't count as a change.
     *
     * @return a boolean whether a edit text of this pinpad is changed
     */
    private boolean isSomethingChangedAtCurrentPinpad() {
        boolean resultValue = false;
        for (Map.Entry<String, List<Details>> group : currentPinpadDetailsMap.entrySet()) {
            for (Details rowDetails : group.getValue()) {
                if (rowDetails.isDataChanged(((EditText) findViewById(currentEditTextViewIDArray[rowDetails.getOrderingNumber()])).getText().toString())) {
                    Log.d(MainActivity.LOGNAME + "\\detEDAc\\isSChCP", "org:" + rowDetails.getOriginalData() + ", changed:" + rowDetails.getChangedData());
                    resultValue = true;
                }
            }
        }
        return resultValue;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method isSomethingChanged checks if something has been changed at all. This method checks
     * the current pinpad and all other edited pinpads.
     *
     * @return a boolean whether a something during this session is changed, with this pin or any other
     */
    private boolean isSomethingChanged() {

        Log.d(MainActivity.LOGNAME + "\\detEDAc\\isSomCh", "changesmap size: " + changedDataMultiplePinpadsMap.size() + "   - change on this till : " + isSomethingChangedAtCurrentPinpad());
        return changedDataMultiplePinpadsMap.size() > 0 || isSomethingChangedAtCurrentPinpad();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method setChangesCurrentPinpad() store the changed data in the detail object and if
     * something has changed, add the pinpad to the map.
     */
    private void setChangesCurrentPinpad() {

        // create a array with the detailNumber as key and the data from the editText as value
        SparseArray<String> changedDetails = new SparseArray<>();
        for (int count = 0; count < numberOfItems; count++) {
            changedDetails.put(count, ((EditText) findViewById(currentEditTextViewIDArray[count])).getText().toString().trim());
        }
        boolean changed = false;
        String changedText;
        String originalText;
        // loop through the map with details
        for (Map.Entry<String, List<Details>> detailRowsPerTitle : currentPinpadDetailsMap.entrySet()) {
            for (Details rowDetails : detailRowsPerTitle.getValue()) {
                changedText = changedDetails.get(rowDetails.getOrderingNumber());
                originalText = rowDetails.getOriginalData();
                // and check if the data from the edittext differs from the the value from the database
                if ((!((originalText == null) && ("".equals(changedText)))) && (!(changedText.equals(originalText)))) {
                    rowDetails.setChangedData(changedText);
                    Log.d(MainActivity.LOGNAME + "\\detEDAc\\setccp", filteredpinpadIDArray[position] + "- title:" + rowDetails.getTitle() + " -   org:'" + rowDetails.getOriginalData() + "' -   cha:'" + changedText + "'");
                    changed = true;
                }
            }
        }
        // add this pinpad to the map changedDataMultiplePinpadsMap to keep track of the changed pinpads
        if (changed) {
            changedDataMultiplePinpadsMap.put(filteredpinpadIDArray[position], currentPinpadDetailsMap);
            Log.d(MainActivity.LOGNAME + "\\detEDAc\\setccp", "add to list : " + filteredpinpadIDArray[position]);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method creates a email and starts the email client
     */
    private void sendTextMail() {

        StringBuilder newLine = new StringBuilder(System.getProperty("line.separator"));
        List algemeenArraylist;
        StringBuilder emailBody = new StringBuilder();

        emailBody.append(getString(R.string.pinAdminEmail_first_line)).
                append(newLine).
                append(newLine);

        // add data/rows
        for (Map.Entry<Long, Map<String, List<Details>>> pinpadDetailsMap : changedDataMultiplePinpadsMap.entrySet()) {
            // pinpad description
            algemeenArraylist = pinpadDetailsMap.getValue().get("algemene gegevens");
            emailBody.append(((Details) algemeenArraylist.get(1)).getOriginalData()) // displayname
                    .append(" te ")
                    .append(((Details) algemeenArraylist.get(2)).getOriginalData()) // plaats
                    .append(", kassa nr: ")
                    .append(((Details) algemeenArraylist.get(4)).getOriginalData()) // kassa nummer
                    .append(", TMS : ")
                    .append(((Details) algemeenArraylist.get(6)).getOriginalData()) // TMS nummer
                    .append(newLine)
                    .append("-----------------")
                    .append(newLine);

            // add the changes per pinpad with the original data
            for (Map.Entry<String, List<Details>> detailRowsPerTitle : pinpadDetailsMap.getValue().entrySet()) {
                for (Details rowDetails : detailRowsPerTitle.getValue()) {
                    if (rowDetails.isDataChanged()) {
                        emailBody.append(rowDetails.getTitle())
                                .append(" ")
                                .append(rowDetails.getChangedData())
                                .append(" was('")
                                .append(rowDetails.getOriginalData())
                                .append("')")
                                .append(newLine);
                    }
                }
            }
            emailBody.append(newLine).append(newLine);
        }
        // the end
        emailBody.append(newLine).
                append(getString(R.string.pinAdminEmail_last_line)).
                append(newLine).
                append(getString(R.string.pinAdminEmail_greet)).
                append(newLine);
        Log.d(MainActivity.LOGNAME + "\\detEDAc\\seTeMa", emailBody.toString());

        //start email Intent (asks for a email client and displays a pre-entered email with emailaddress, subject and body)
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.pinAdminEmailAddress)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.pinAdminEmailSubject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody.toString());
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.chooseEmailClient)));
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This inner class is used as a extended TextWatcher which can indicate if a EditText is
     * altered (compared to the original data) and if so, give the corresponding title (TextView) a
     * blue color.
     * It also checks if there are no more changes in this pinpad. In that case the current pinpad is
     * removed from the map with changed pinpads.
     */
    class EditTextWithTextViewListener implements TextWatcher {

        /**
         * The Textview which stands before the EditText
         */
        private final TextView title;
        /**
         * The original data from the EditText
         */
        private final String originalText;
        /**
         * A boolean to indicate if the editText is changed
         */
        private boolean changed;


        /**
         * Instantiates a new instance and copies the parameters to the global values.
         *
         * @param titleParam        The Textview which stands before the EditText
         * @param originalTextParam The original data from the EditText
         * @param changedParam      A boolean to indicate if the editText is changed
         */
        EditTextWithTextViewListener(TextView titleParam, String originalTextParam, boolean changedParam) {
            title = titleParam;
            if (originalTextParam == null) {
                originalText = "";
            } else {
                originalText = originalTextParam;
            }
            changed = changedParam;
        }

////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * This method is called to notify you that, within s, the count characters beginning at
         * start are about to be replaced by new text with length after.
         * This method isn't used, but need to be implemented by the interface.
         *
         * @param s     the text
         * @param start where in s the text is changed
         * @param count number of characters to be replaced
         * @param after length of the new text
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * This method is called to notify you that, within s, the count characters beginning at
         * start have just replaced old text that had length before.
         * It checks if the text from the EditText is different from the origanal data. If so, set
         * the title color.
         * If a the data EditText is restored (retyped) a check is made to see if there are changes,
         * if there are no more changes, than the current pinpad is removed from the map with
         * changed pinpads.
         *
         * @param s      the changed text
         * @param start  where the text is changed
         * @param before length of the text before the edit
         * @param count  number of changed characters
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (originalText.equals(s.toString())) {
                if (changed) {
                    title.setTextColor(Color.BLACK);
                    changed = false;
                    if (!isSomethingChangedAtCurrentPinpad()) {
                        changedDataMultiplePinpadsMap.remove(filteredpinpadIDArray[position]);
                        Log.e(MainActivity.LOGNAME + "\\detEDAc\\ontextc", "remove one from list : " + filteredpinpadIDArray[position]);

                    }
                }
            } else {
                if (!changed) {
                    title.setTextColor(Color.BLUE);
                    changed = true;
                }
            }
        }

////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * This method is called to notify you that, somewhere within s, the text has been changed.
         * * This method isn't used, but need to be implemented by the interface.
         *
         * @param s the changed text
         */
        @Override
        public void afterTextChanged(Editable s) {

        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}

