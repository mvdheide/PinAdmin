package eu.centric.pinadmin.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.centric.pinadmin.R;


/**
 * This class is an adapter class between the data from the database and the autocompleteTextViews
 * (Filter Fields Fragment).
 * The FilterFieldsAutoCompleteAdapter extends the ArrayAdapter class to look the input of the
 * autocomplete is inside the strings, rather than at the start of a string.
 *
 * @author MHeide
 * @since 12-04-‎2017
 */
public class FilterFieldsAutoCompleteAdapter extends ArrayAdapter<String> {

    /**
     * The context is an interface to global information about an application environment.
     * The context is needed here for inflating the autocomplete options.
     */
    private final Context context;
    /**
     * a boolean which indicate if the list contains strings with lower case characters
     * (and uppercase chars).
     * If the strings are only uppercase, use false. This is faster because not every string has to
     * be changed to lowercase.
     */
    private final boolean mixedUPLowerCase;
    /**
     * the complete list with autocomplete data. This list stays with this
     */
    private final List<String> items;
    /**
     * the list with autocomplete data. This is a temporary list for compairing the strings
     */
    private final List<String> tempItems;
    /**
     * The filtered list, which contains the strings that contain the typed text
     */
    private final List<String> suggestions;

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    private final Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults returnValue;
            if (constraint != null) {
                suggestions.clear();
                if (mixedUPLowerCase) {
                    for (String suggestionString : tempItems) {
                        if (suggestionString.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(suggestionString);
                        }
                    }
                } else {
                    for (String suggestionString : tempItems) {
                        if (suggestionString.contains(constraint.toString().toUpperCase())) {
                            suggestions.add(suggestionString);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                returnValue = filterResults;
            } else {
                returnValue = new FilterResults();
            }
            return returnValue;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> filterList = (ArrayList<String>) results.values;
            if (results.count > 0) {
                clear();
                for (String suggestionString : filterList) {
                    add(suggestionString);
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return resultValue.toString();
        }
    };

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * the constructor for this class.
     *
     * @param context The context is an interface to global information about an application environment.
     * @param resource The resource ID for a layout file containing a layout to use when instantiating views.
     * @param textViewResourceId  The id of the TextView within the layout resource to be populated.
     * @param itemsParam The list with autocomplete data.
     * @param mixedUPLowerCaseParam A boolean which indicate if the list contains strings with lower
     *                              case characters (and uppercase chars). If the strings are only
     *                              uppercase, use false.
     */
    public FilterFieldsAutoCompleteAdapter(Context context, int resource, int textViewResourceId, List<String> itemsParam, boolean mixedUPLowerCaseParam) {
        super(context, resource, textViewResourceId, itemsParam);
        this.context = context;
        mixedUPLowerCase = mixedUPLowerCaseParam;

        items = itemsParam;

        tempItems = new ArrayList<>(items); // this makes the difference.
        suggestions = new ArrayList<>();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     * Returns a view for every Suggestion.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_filter_auto_complete_textview, parent, false);
        }
        String suggestionString = items.get(position);
        if (suggestionString != null) {
            TextView suggestionTextView = view.findViewById(R.id.autocompleteSuggestionTextView);
            if (suggestionTextView != null)
                suggestionTextView.setText(suggestionString);

        }
        return view;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return itemFilter;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
