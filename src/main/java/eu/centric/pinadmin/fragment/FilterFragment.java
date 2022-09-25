package eu.centric.pinadmin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import eu.centric.pinadmin.MainActivity;
import eu.centric.pinadmin.R;
import eu.centric.pinadmin.adapter.FilterFieldsAutoCompleteAdapter;
import eu.centric.pinadmin.data.DataProvider;

import static eu.centric.pinadmin.MainActivity.NUMBER_OF_FILTERFIELDS;


/**
 * A simple {@link Fragment} subclass that handles the filtering textfields. The
 * {@link eu.centric.pinadmin.MainActivity#overviewListView} is filtered, based on the entered text
 * in the textfields. If the content of a textfield is altered, the
 * {@link eu.centric.pinadmin.MainActivity#overviewListView} is filled with new data (based on the
 * three entered texts) and the autocomplete data of the other two autocomplete textfields is updated.
 *
 * Activities that contain this fragment must implement the
 * {@link FilterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events. And can be initialized with:
 * FilterFragment filterFragment = (FilterFragment) getSupportFragmentManager().findFragmentById(R.id.filterFieldsFragment);
 *
 * The gui contains of 3 autoCompleteTextViews.
 *
 */
public class FilterFragment extends Fragment implements TextWatcher {

    /**
     * An Array with the 3 autocomplete textfields
     */
    private AutoCompleteTextView[] autoCompleteTextViews;
    /**
     * This object provide communication with the parent activity.
     */
    private OnFragmentInteractionListener mListener;
    /**
     * the dataprovider Object is a object to get/set information. It's a singleton class which act
     * as a proxy class. Only the dataprovider class has access to datasources (sqlite db, vpn,
     * onedrive, etc)
     */
    private DataProvider dataProvider;
    /**
     * An array with the ID's of the AutoCompleteFields. It can be used for the findbyID and in the
     * method OnClick. The order of these ID should be the same as the layout file.
     */
    private final static int[] viewIDArray = {
            R.id.FilterFieldFirstTextView,
            R.id.FilterFieldSecondTextView,
            R.id.FilterFieldThirdTextView};

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataProvider = DataProvider.getInstance();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_filter_fields, container, false);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        autoCompleteTextViews = new AutoCompleteTextView[NUMBER_OF_FILTERFIELDS];

        for (int count = 0; count < NUMBER_OF_FILTERFIELDS; count++) {
            autoCompleteTextViews[count] = view.findViewById(viewIDArray[count]);
            autoCompleteTextViews[count].setThreshold(2);
            autoCompleteTextViews[count].addTextChangedListener(this);
        }
        super.onViewCreated(view, savedInstanceState);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterTextChanged(Editable editable) {

        Log.d(MainActivity.LOGNAME + "\\LTFF\\afterTC", "textChanged: " + editable);
        fillFilterFieldsWithAutoCompleteData(false);
        mListener.populateOverviewPinpadsListView();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Clear all autoComplete text views.
     */
    public void clearAllautoCompleteTextViews() {
        for (int count = 0; count < NUMBER_OF_FILTERFIELDS; count++) {
            autoCompleteTextViews[count].removeTextChangedListener(this);
            autoCompleteTextViews[count].setText("");
            autoCompleteTextViews[count].addTextChangedListener(this);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fill the aucomplete textfield with autocomplete textfields. This data is based on the other
     * two autocomplete textfields. If the clearAll parameter is true, all three autocomplete are
     * filled, this can be used for example when initializing. If the clearAll parameter is false,
     * the other two are filled.
     *
     * @param clearAll the clearAll is a boolean to indicate if all the trhee textfields should be
     *                 filled, or the two others textfields.
     */
    public void fillFilterFieldsWithAutoCompleteData(boolean clearAll) {

        for (int count = 0; count < NUMBER_OF_FILTERFIELDS; count++) {
            if (!autoCompleteTextViews[count].hasFocus() || clearAll) {
                autoCompleteTextViews[count].setAdapter(new FilterFieldsAutoCompleteAdapter(getContext(), R.layout.fragment_filter_fields, R.id.autocompleteSuggestionTextView, dataProvider.getItemList(count, getEnteredText()), true));
//                Log.d(MainActivity.LOGNAME + "\\ff\\fillAC","itemlist: " + dataProvider.getItemList(count,getEnteredText()));
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get an Array with the entered text of the three textfields.
     *
     * @return the string with 3 Strings of the entered text.
     */
    public String[] getEnteredText() {
        String[] selectedOptions = new String[NUMBER_OF_FILTERFIELDS];
        for (int count = 0; count < NUMBER_OF_FILTERFIELDS; count++) {
            selectedOptions[count] = autoCompleteTextViews[count].getText().toString();
        }
        return selectedOptions;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     *
     * Populate overview pinpads list view.
     */
    public interface OnFragmentInteractionListener {
     /**
      * Populate overview pinpads list view.
     */
        void populateOverviewPinpadsListView();

    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
