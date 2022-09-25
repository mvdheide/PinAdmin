package eu.centric.pinadmin.data;

import android.content.res.AssetManager;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import eu.centric.pinadmin.MainActivity;
import eu.centric.pinadmin.util.Details;

/**
 * Create a map with the data from the database and structure of the xml file.
 * The class has only access to {@link XMLHandler}.
 * {@link DataProvider} <-> {@link XMLManager} <-> {@link XMLHandler}
 * <p>
 * This class contains a constructor (handles access to the xml file and the parsing through
 * {@link XMLHandler}) and a getter (for the resulting {@link currentPinpadDetailsMap}).
 *
 * @author MHeide
 * @since 27-10-2017
 */
class XMLManager {

    /**
     * The filename of the xml file in assets
     */
    private final String XML_ASSET_FILENAME = "details_mapping.xml";
    /**
     * This data contains the data from the database with the details for this pinpad.
     * The keys of the Map contains groups ("algemene gegevens","contact gegevens",
     * "verbinding gegevens",etc). Each map value consist of a list of Details (each Detail
     * Object contains the title, the number for the array, the original and changed data).
     */
    private Map<String, List<Details>> currentPinpadDetailsMap = new LinkedHashMap<>();

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The constructor creates a xmlHandler object and parses the xml data.
     *
     * @param assetManager the assetManager to access the xml file in asset
     * @param detailMap    a map: keys are the column names from the MDB file/database, values are the
     *                     values of that column for this current pinpad (from the database). This map
     *                     is used to populate the structure of the xml file.
     */
    XMLManager(AssetManager assetManager, Map<String, String> detailMap) {
        Log.d(MainActivity.LOGNAME + "\\xmlM\\const", "detailmap size :" + detailMap.size());
        try {
            InputStream inputStream = assetManager.open(XML_ASSET_FILENAME);
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser sp = saxParserFactory.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            XMLHandler xmlHandler = new XMLHandler(detailMap);
            xr.setContentHandler(xmlHandler);
            InputSource inputSource = new InputSource(inputStream);
            xr.parse(inputSource);
            currentPinpadDetailsMap = xmlHandler.getCurrentPinpadDetailsMap();
//            listDataHeader= xmlHandler.getListDataHeader();
        } catch (IOException e) {
            Log.e(MainActivity.LOGNAME + "\\xmlM\\const", "ioE :" + e.getMessage());
        } catch (ParserConfigurationException e) {
            Log.e(MainActivity.LOGNAME + "\\xmlM\\const", "pce :" + e.getMessage());
        } catch (SAXException e) {
            Log.e(MainActivity.LOGNAME + "\\xmlM\\const", "sax :" + e.getMessage());
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get map with the data from the database and structure of the xml file.
     *
     * @return This data contains the data from the database with the details for this pinpad.
     * The keys of the Map contains groups ("algemene gegevens","contact gegevens",
     * "verbinding gegevens",etc). Each map value consist of a list of Details (each Detail
     * Object contains the title, the number for the array, the original and changed data).
     */
    Map<String, List<Details>> getCurrentPinpadDetailsMap() {
        return currentPinpadDetailsMap;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
