package nl.oberon.tmobile.watchapp.common.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 *
 * A simple helper class to save preferences
 *
 */
public class PreferenceHelper {

    private static final String DEFAULT_NAME = "default";
    private static final String CAPI_TOKEN = "token";
    private static final String HAS_SYNC_ONCE = "has synced";

    /**
     * Save token in preferences
     * @param context
     * @param token
     */
    public static void saveToken(Context context, String token) {
        getSharedPreference(context).edit()
                .putString(CAPI_TOKEN, token)
                .apply();
    }

    /**
     * @param context
     * @return the access token from the preferences
     */
    public static String getToken(Context context){
        return getSharedPreference(context).getString(CAPI_TOKEN, null);
    }

    /**
     * @param context
     * @return wether the app has synced atleast once
     */
    public static boolean hasSynced(Context context){
        return getSharedPreference(context).getBoolean(HAS_SYNC_ONCE, false);
    }

    public static void setHasSynced(Context context, boolean hasSynced){
        getSharedPreference(context).edit()
                .putBoolean(HAS_SYNC_ONCE, hasSynced)
                .apply();
    }

    /**
     * deletes/resets the preferences
     *
     * @param context
     */
    public static void resetPreference(Context context){
        saveToken(context, null);
        setHasSynced(context, false);
    }

    /**
     * returns a default sharedpreferences object to write and read to.
     * @param context
     * @return
     */
    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
    }
}
