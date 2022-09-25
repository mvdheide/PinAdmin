package eu.centric.pinadmin.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import eu.centric.pinadmin.MainActivity;

/**
 * This class has different date util functions. For example to convert between a date object and a
 * string, add a amount of days to a date.
 * Purpose is that only this class imports Date classes
 *
 * all methods are static.
 *
 * @author MHeide
 * @since 1-6-2017
 */
public class DateUtil {

    /**
     * this string is used in the program and database.
     */
    private static final String universalDateFormat = "yyyy-MM-dd HH:mm:ss";
    /**
     * This String is used to display time to the user
     */
    private static final String dutchDateFormat = "dd-MM-yyyy (HH:mm)";


////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Parse time stamp date with the format "yyyy-MM-dd HH:mm:ss". Converts to a {@link Date} object.
     *
     * @param timestampString the timestamp string in the format "yyyy-MM-dd HH:mm:ss".
     * @return the {@link Date} object of the given time.
     */
    private static Date parseTimeStamp(final String timestampString){
        Date resultDate=null;
        SimpleDateFormat fmt = new SimpleDateFormat(universalDateFormat, Locale.getDefault());
        try {
            resultDate = fmt.parse(timestampString);
        } catch (Exception e) {
            Log.e(MainActivity.LOGNAME + "\\DateUtil\\parsTS", "error parsing time: "+ e.toString());

        }
        return resultDate;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Adds/subtracts a amount of days  from the current time stamp. A negative number is in the past.
     * for example adjustedTimeStamp(-1) points to yesterday and adjustedTimeStamp(1) to tomorrow
     *
     * @param adjustment the amount of days from now. A negative number is in the past.
     * @return the date object pointing to the date, adjusted with the given amount of days
     */
    private static Date adjustedTimeStamp(final int adjustment){

//        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, adjustment);
        return cal.getTime();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets current time stamp and format it with "yyyy-MM-dd HH:mm:ss".
     *
     * @return the current time stamp in the format "yyyy-MM-dd HH:mm:ss"
     */
    public static String getCurrentTimeStamp() {
        SimpleDateFormat fmt = new SimpleDateFormat(universalDateFormat, Locale.getDefault());
        return fmt.format(Calendar.getInstance().getTime());
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get readable time stamp string with the format "dd-MM-yyyy (HH:mm)"
     *
     * @param timeStampString the time stamp string with the format "yyyy-MM-dd HH:mm:ss"
     * @return the string with the format "dd-MM-yyyy (HH:mm)"
     */
    public static String getReadableTimeStamp(final String timeStampString){

        Calendar cal = Calendar.getInstance();
        cal.setTime(parseTimeStamp(timeStampString));
        SimpleDateFormat fmt = new SimpleDateFormat(dutchDateFormat, Locale.getDefault());
        return fmt.format(cal.getTime());
    }

////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * The method isDateLongerThanDaysAgo checks if the the given date is longer ago compared to the
     * date for a given amount of days from now.
     *
     * @param amountDays the amount of days from now. A negative number is in the past.
     * @param date the date
     * @return if the date is longer or shorter than the amount of days from now
     */
    public static boolean isDateLongerThanDaysAgo(final int amountDays, final String date){

        return (DateUtil.adjustedTimeStamp(amountDays)).after(DateUtil.parseTimeStamp(date));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
