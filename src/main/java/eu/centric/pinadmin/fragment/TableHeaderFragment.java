package eu.centric.pinadmin.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eu.centric.pinadmin.MainActivity;
import eu.centric.pinadmin.R;

/**
 * A simple {@link Fragment} subclass that handles the table header buttons. They provide order
 * function: When new data for the {@link eu.centric.pinadmin.MainActivity#overviewListView} is
 * retrieved from the database, it's ordered based on the last clicked button.
 *
 * another press on the same button, change ascending <-> descending order
 * <p>
 * Activities that contain this fragment must implement the
 * {@link TableHeaderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events. And can be initialized with:
 * TableHeaderFragment tableHeaderFragment = (TableHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.tableHeaderFragment);
 *
 * <p>
 * The gui contains five buttons with the same width as columns.
 *
 * @author MHeide
 * @since 15 -2-‎2018
 */
public class TableHeaderFragment extends Fragment implements View.OnClickListener {

    /**
     * An array with the ID's of the buttons. It can be used for the findbyID and in the method
     * OnClick. The order of these ID should be the same as the layout file.
     */
    private final static int[] viewIDArray = {
            R.id.shopHeaderButton,
            R.id.placeHeaderButton,
            R.id.filiaalNRHeaderButton,
            R.id.kassaNRHeaderButton,
            R.id.tmsNRHeaderButton};
    /**
     * This object provide communication with the parent activity.
     */
    private OnFragmentInteractionListener mListener;
    /**
     * The button which is last clicked. If the Default is the {@link R.id.shopHeaderButton}.
     */
    private int buttonClicked = 0;
    /**
     * A boolean array to keep track if that column should be ordered ascending.
     */
    private boolean[] buttonAsc = new boolean[5];

    private final Button[] buttonArray = new Button[5];

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        buttonAsc = new boolean[5];
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table_header, container, false);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        for (int buttonCounter = 0; buttonCounter < 5; buttonCounter++) {
            buttonArray[buttonCounter] = view.findViewById(viewIDArray[buttonCounter]);
            buttonArray[buttonCounter].setOnClickListener(this);
        }
        Log.d(MainActivity.LOGNAME + "\\THF\\onViewCr", "onclick is set!");

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
    public void onClick(View clickedButton) {
        int buttonClickedViewID = clickedButton.getId();
        int oldButtonCLicked = buttonClicked;
        for (int buttonCounter = 0; buttonCounter < 5; buttonCounter++) {
            if (viewIDArray[buttonCounter] == buttonClickedViewID) {
                buttonClicked = buttonCounter;
                buttonAsc[buttonClicked] = buttonClicked == oldButtonCLicked && !buttonAsc[buttonClicked];
                buttonArray[buttonCounter].setPaintFlags(buttonArray[buttonCounter].getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                buttonArray[buttonCounter].setTypeface(null, Typeface.BOLD_ITALIC );
//                break;
            }
            else {
                buttonArray[buttonCounter].setPaintFlags(0);
                buttonArray[buttonCounter].setTypeface(null, Typeface.NORMAL );
            }
        }
        mListener.populateOverviewPinpadsListView();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the last pressed button. 0 = most left button, 4 is most right button
     *
     * @return the last pressed button.
     */
    public int getButtonClicked() {

        return buttonClicked;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get a boolean which indicate if the column of the last pressed button should be ascending.
     *
     * @return the boolean if the last pressed button should be ascending.
     */
    public boolean getAscend() {

        return buttonAsc[buttonClicked];
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

        void populateOverviewPinpadsListView();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
