package eu.centric.pinadmin.util;

/**
 * The Class ComputeName converts the columns hoofdketen and naam from the mdb file and computes the
 * columns displayname and shopNR.
 * In the mdb file the name for the shop/hq is entered on many different ways. This method is used to
 * generalize the naming of the hq and shops.
 *
 * It also strips the strings from unwanted chars (return "'\).
 * The columns hoofdketen and naam can only be entered via the constructor. And only the fields
 * displayname and shopNR get be accessed through getters.
 *
 * @author MHeide
 * @since 8-9-2017
 */
public class ComputeName {

    private final String shopNR;
    private final String displayname;
    private boolean bothEmpty = false;

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instantiates a new ComputeName and calculates the columns displayName and ShopNr.
     *
     * @param hoofdkantoorNaam the hoofdkantoor naam, a column from the mdb file
     * @param naam             the naam, a column from the mdb file
     */
    public ComputeName(final String hoofdkantoorNaam, final String naam) {

        String tempDisplayNaam;

        String tempHoofdkantoorNaam = hoofdkantoorNaam;
        if (naam == null) {
//            Log.d(MainActivity.LOGNAME + "\\CD\\CD", "naam is null");
            if (hoofdkantoorNaam == null) {
//                Log.d(MainActivity.LOGNAME + "\\CD\\CD", "naam en hknaam is null");
                displayname = "";
                shopNR = "";
                bothEmpty = true;
            } else {
//                Log.d(MainActivity.LOGNAME + "\\CD\\CD", "naam is null, hk niet");
                displayname = hoofdkantoorNaam;
                shopNR = "";
            }
        } else {
            shopNR = naam.replaceAll("[^0-9]", "").trim(); // remove 2.0,
            tempDisplayNaam = naam.replaceAll("[\\r\\n0-9\\\"\\']+", "").trim();
            if (hoofdkantoorNaam == null) {
                tempHoofdkantoorNaam = "";
            }
            if (tempDisplayNaam.toLowerCase().equals(tempHoofdkantoorNaam.toLowerCase())) {
                displayname = tempDisplayNaam;
            } else if (tempHoofdkantoorNaam.startsWith(tempDisplayNaam + ' ')) {
//                    Log.d(MainActivity.LOGNAME + "\\CD\\CD", tempHoofdkantoorNaam + " starts with '" + tempDisplayNaam+"'  wordt " + hoofdkantoorNaam);
                displayname = tempHoofdkantoorNaam;
            } else if (tempDisplayNaam.startsWith(tempHoofdkantoorNaam + ' ')) {
                displayname = tempDisplayNaam;
            } else {
                displayname = tempHoofdkantoorNaam + " - " + tempDisplayNaam;
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets shop nr. The shopnummer is extracted from the column Naam, this is done in the
     * constructor of ComputeName.
     *
     * @return the shop nr, either an empty String or an String containing only digits
     */
    public String getShopNR() {
//        if (shopNR ==null){
//            Log.d(MainActivity.LOGNAME + "\\CD\\getShop", "shopnr is null");
//        }
        return shopNR;
    }

    /**
     * Gets the displayname. The shopnummer is extracted from the column HoofdketenNaam, this is
     * done in the constructor of ComputeName.
     *
     * @return the displayname, a combination between the hq name and the shop name
     */
    public String getDisplayname() {
//        if (displayname ==null){
//            Log.d(MainActivity.LOGNAME + "\\CD\\getDispl", "displayname is null");
//        }
        return displayname;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method checks if both parameters of the constructor are emtpy (hoofdkantoorNaam and naam)
     *
     * @return the boolean if hoofdkantoorNaam and naam are null
     */
    public boolean isHqAndNameEmpty(){

        return bothEmpty;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
