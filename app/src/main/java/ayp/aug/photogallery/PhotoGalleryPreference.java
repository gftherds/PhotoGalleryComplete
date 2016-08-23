package ayp.aug.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Rawin on 19-Aug-16.
 */
public class PhotoGalleryPreference {
    private static final String TAG = "PhotoGalleryPref";

    protected static final String PREF_SEARCH_KEY = "PhotoGalleryPref";
    protected static final String PREF_LAST_ID = "PREF_LAST_ID";

    public static SharedPreferences mySharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getStoredSearchKey(Context context) {
        return mySharedPref(context).getString(PREF_SEARCH_KEY, null);
    }

    public static void setStoredSearchKey(Context context, String key) {
        mySharedPref(context).edit()
                .putString(PREF_SEARCH_KEY, key)
                .apply();
    }

    public static String getStoredLastId(Context context) {
        return mySharedPref(context).getString(PREF_LAST_ID, null);
    }

    public static void setStoredLastId(Context context, String lastId) {
        mySharedPref(context).edit()
                .putString(PREF_LAST_ID, lastId)
                .apply();
    }
}
