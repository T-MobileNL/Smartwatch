package nl.oberon.tmobile.watchapp.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 *
 * Helper class for various intent purposes
 *
 */
public class IntentUtil {

    /**
     * Opens the specified activity
     * @param context
     * @param clazz
     * @param <T>
     */
    public static <T extends Activity> void openActivity(Context context, Class<T> clazz) {
        context.startActivity(new Intent(context, clazz));
    }

    /**
     * opens the given url, does not do any checking on the url
     * @param context
     * @param url
     */
    public static void openUrl(Context context, String url){
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

}
