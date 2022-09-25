package eu.centric.pinadmin.util;

/**
 * This class is used to display the progress. A TaskProgress object is passed to the method
 * {@link android.os.AsyncTask#publishProgress(Object[])}
 *
 * @author MHeide
 * @since 29 -12-2017
 */
public class TaskProgress {

    /**
     * The maximum for the {@link progressCounter}.
     */
    private final int max;
    /**
     * The message which is displayed in the progressbar.
     */
    private final String message;
    /**
     * the current value of the progress.
     */
    private int progressCounter;

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instantiates a new Task progress.
     *
     * @param startProgressCounterParam The start value of the progress counter param
     * @param maxParam                  The maximum for the counter to count.
     * @param messageParam              The message which is displayed in the progressbar.
     */
    public TaskProgress(final int startProgressCounterParam, final int maxParam, final String messageParam) {
        progressCounter = startProgressCounterParam;
        max = maxParam;
        message = messageParam;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets progress counter.
     *
     * @return the progress counter
     */
    public int getProgressCounter() {
        return progressCounter;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Update the current progress.
     *
     * @param progressParam the progress
     */
    public void setProgressCounter(final int progressParam) {
        progressCounter = progressParam;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the message which is displayed in the progressbar.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets maximum value of the {@link progressCounter}.
     *
     * @return the maximum of {@link progressCounter}
     */
    public int getMax() {
        return max;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
