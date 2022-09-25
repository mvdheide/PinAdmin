package eu.centric.pinadmin.data;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.centric.pinadmin.util.Details;

/**
 * This class contains rules to parse the xml file. The parsing is done in the class {@link XMLManager}
 * In this xml file 4 different qnames are used:
 * group : an element containing a collection item elements.
 * item : an element containing one database cell/one Details object.
 * name : an attribute of item, containing the name to be displayed.
 * column : an attribute of item, containing the name of the corresponding column in the database.
 *
 * @author MHeide
 * @since 26-10-2017
 */
class XMLHandler extends DefaultHandler {

    /**
     * a map: keys are the column names from the MDB file/database, values are the values of that
     * column for this current pinpad (from the database). This map is used to populate the
     * structure of the xml file.
     */
    private final Map<String, String> detailMap;
    /**
     * This data contains the data from the database with the details for this pinpad.
     * The keys of the Map contains groups ("algemene gegevens","contact gegevens",
     * "verbinding gegevens",etc). Each map value consist of a list of Details (each Detail
     * Object contains the title, the number for the array, the original and changed data).
     */
    private final Map<String, List<Details>> currentPinpadDetailsMap = new LinkedHashMap<>();
    /**
     * A flag to see if the current parsed text is a valid elementName or data: True is data,
     * false is an element.
     */
    private boolean currentElement = false;
    /**
     * The data from the current element
     */
    private String currentValue = "";
    /**
     * this list contains Details objects: the items from one group element.
     */
    private List<Details> groupList;
    /**
     * An array used for the creation of a Details object. The first value is the title/name, the
     * second is the original data and the third is the changed data (but because nothing is changed
     * yet, this value will be the same as the second).
     */
    private String[] detailArray = new String[3];
    /**
     * the currentGroup is an String for the name from a collection of item elements (for example
     * "algemene gegevens","contact gegevens", "verbinding gegevens",etc)
     */
    private String currentGroup = "";

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instantiates a new Detail data xml handler.
     *
     * @param detailMapParam a map: keys are the column names from the MDB file/database, values are
     *                       the values of that column for this current pinpad (from the database).
     *                       This map is used to populate the structure of the xml file.
     */
    XMLHandler(final Map<String, String> detailMapParam) {

        detailMap = detailMapParam;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Receive notification of the start of an element. Allocates new tree nodes (item and groups)
     *
     * @param uri        The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
     * @param localName  The local name (without prefix), or the empty string if Namespace processing is not being performed.
     * @param qName      The qualified name (with prefix), or the empty string if qualified names are not available.
     * @param attributes The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
     */
    @Override
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) {

        currentElement = true;
        if (qName.equals("group")) {
            groupList = new ArrayList<>();
            currentGroup = attributes.getValue(0);
//            listDataHeader.add(currentGroup);
        } else if (qName.equals("item")) {
            detailArray = new String[3];
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Receive notification of the end of an element. Set the data to the array/list/map.
     * In this xml file 4 different qnames used:
     * group : an element containing a collection item elements. In this method the lists are added
     * to the the {@link currentPinpadDetailsMap}
     * item : an element containing one database cell/one Details object. In this method the Details
     * object created, based on the {@link detailArray} and added to the {@link groupList}.
     * name : an attribute of item containing the name to be displayed. In this method the
     * {@link detailArray} field title is set.
     * column : an attribute of item containing the name of the corresponding column in the database.
     * In this method the {@link detailArray} originaldata and changeddata field is set
     * with data from the database, using the {@link detailMap}
     *
     * @param uri       The Namespace URI, or the empty string if the element has no Namespace URI or if
     *                  Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace processing
     *                  is not being performed.
     * @param qName     The qualified name (with prefix), or the empty string if qualified names are not
     *                  available.
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        currentElement = false;
        switch (qName) {
            case "name":
                detailArray[0] = currentValue.trim();
//                Log.d(MainActivity.LOGNAME + "\\ddXMKH\\end", "item name :");
                break;
            case "column":
                String column = detailMap.get(currentValue.trim());
                if (column == null) {
                    column = "";
                } else {
                    column = column.trim();
                }
                // because there are no changes yet, the origanl data and the changed data are both set to the same value.
                detailArray[1] = column;
                detailArray[2] = column;
//                Log.d(MainActivity.LOGNAME + "\\ddXMKH\\end", "column :" + currentValue.trim() + '/' + detailMap.get(currentValue.trim()));
                break;
            case "item":
                groupList.add(new Details(detailArray[0], detailArray[1], detailArray[2]));
//                Log.d(MainActivity.LOGNAME + "\\ddXMKH\\end", "itemtitle en column :" + detailArray[0] + '/' + detailArray[1]);
                break;
            case "group":
                currentPinpadDetailsMap.put(currentGroup, groupList);
//                Log.d(MainActivity.LOGNAME + "\\ddXMKH\\end", "group title :" + currentGroup);
                break;
            default:
                break;
        }

        currentValue = "";
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Receive notification of character data inside an element.
     *
     * @param ch     The characters.
     * @param start  The start position in the character array.
     * @param length The number of characters to use from the character array.
     */
    @Override
    public void characters(char ch[], int start, int length) {
        if (currentElement) {
            currentValue = currentValue + new String(ch, start, length);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the map with the data of the current pinpad. It has data from the database and structure
     * from the xml file.
     *
     * @return a map with the data from the database with the details for this pinpad.
     * The keys of the Map contains groups ("algemene gegevens","contact gegevens",
     * "verbinding gegevens",etc). Each map value consist of a list of Details (each Detail
     * Object contains the title, the number for the array, the original and changed data).
     */
    Map<String, List<Details>> getCurrentPinpadDetailsMap() {
        return currentPinpadDetailsMap;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
