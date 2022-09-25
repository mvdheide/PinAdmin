package eu.centric.pinadmin.util;


/**
 * This class is used to store a single data field. It has three fields:
 * - the title:  the description or label. it comes from the xml file
 * - original data: the value from the data base
 * - changed data: the same as the original data until the user change the value
 *
 * @author MHeide
 * @since 15 -1-2018
 */
public class Details {

    /**
     * the title/description/label.
     */
    private final String title;
    /**
     * the original data (the value from the database).
     */
    private final String originalData;
    /**
     * When the user has changed the value of this Detail, it is stored in this field. The field is
     * initialized with the same value as {@link Details#originalData}.
     */
    private String changedData;
    /**
     * the number to keep track of the order of the details. It can be used in a for loop.
     */
    private int orderingNumber;

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instantiates a new Details.
     *
     * @param titleParam        the title param
     * @param originalDataParam the original data param
     * @param changedDataParam  the changed data param
     */
    public Details(final String titleParam, final String originalDataParam, final String changedDataParam) {
        title = titleParam;
        originalData = originalDataParam;
        changedData = changedDataParam;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets order number.
     *
     * @return the detail number
     */
    public int getOrderingNumber() {
        return orderingNumber;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets order number.
     *
     * @param orderingNumberParam the detail number param
     */
    public void setOrderingNumber(int orderingNumberParam) {
        orderingNumber = orderingNumberParam;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the title of this Detail.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets changed data.
     *
     * @return the changed data or the same value as the original data if the user hasn't changed it.
     */
    public String getChangedData() {
        return changedData;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The value of this Detail is changed.
     *
     * @param changedDataParam the changed data
     */
    public void setChangedData(String changedDataParam) {
        changedData = changedDataParam;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets original data. It has the value from the database.
     *
     * @return the original data
     */
    public String getOriginalData() {
        return originalData;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if the user has changed the value of this Detail. The original data is compared to the
     * changed data.
     *
     * @return the boolean if the data is changed
     */
    public boolean isDataChanged() {
        boolean result = false;
        if ((!((originalData == null) && ("".equals(changedData)))) && (!(changedData.equals(originalData)))) {
            result = true;
        }
//        Log.d(MainActivity.LOGNAME + "\\Details\\isDChan","result = " + result + ", original = '" + originalData + "', changed = '" + changedData + "'");
        return result;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if the user has changed the value of this Detail. The original data is compared to the
     * a given string.
     *
     * @param changedDataParam the value the be compared to the original data
     * @return the boolean if the data is changed
     */
    public boolean isDataChanged(String changedDataParam) {
        boolean result = false;
        if ((!((originalData == null) && ("".equals(changedDataParam)))) && (!(changedDataParam.equals(originalData)))) {
            result = true;
        }
//        Log.d(MainActivity.LOGNAME + "\\Details\\isDChan","result = " + result + ", original = '" + originalData + "', changed = '" + changedData + "'");
        return result;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
