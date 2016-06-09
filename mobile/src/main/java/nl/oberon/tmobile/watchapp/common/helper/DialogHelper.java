package nl.oberon.tmobile.watchapp.common.helper;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import nl.oberon.tmobile.watchapp.BuildConfig;
import nl.oberon.tmobile.watchapp.R;

/**
 * Helper class for displaying dialogs
 */
public class DialogHelper {

    /**
     *  Displays an alert dialog with the message of of the throwable error.
     * prints the stacktrace if in debug mode
     *
     * @param context
     * @param t
     */
    public static void displayEror(Context context, Throwable t) {
        if (BuildConfig.DEBUG) t.printStackTrace();
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.error_title))
                .setMessage(t.getMessage())
                .create()
                .show();
    }

}
