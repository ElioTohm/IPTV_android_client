package xms.com.smarttv.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private static SharedPreferences sPreferences;
    private static String SERVER_URL = "SERVER_URL";
    private static String NOTIFICATION_PORT = ":3000";

    /**
     * init Preference Manager
     * @param context
     */
    @SuppressLint("CommitPrefEdits")
    static void init(Context context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get String Preferences by
     * @param key
     * @param defaultValue
     * @return
     */
    private static String getString(String key, String defaultValue) {
        return sPreferences.getString(key, defaultValue);
    }

    /**
     * Pass static key SERVER_URL
     * @return String preference
     */
    public static String getServerUrl () {
        return getString(SERVER_URL, "");
    }

    /**
     * Pass static key SERVER_URL addd port number
     * @return String preference
     */
    public  static String getNotificationPort () {
        return getServerUrl() + NOTIFICATION_PORT;
    }

    /**
     * add SERVER_URL preference value by commit
     * @param value
     */
    public static void setServerUrl(String value) {
        if (sPreferences != null) {
            sPreferences.edit().putString(SERVER_URL, value).commit();
        }
    }
}
