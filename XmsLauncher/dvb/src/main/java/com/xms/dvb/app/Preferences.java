package com.xms.dvb.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private static SharedPreferences sPreferences;
    private static String SERVER_URL = "SERVER_URL";
    private static String LAST_CHANNEL = "LAST_CHANNEL";

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

    private static int getInt(String key, int defaultValue) {
        return sPreferences.getInt(key, defaultValue);
    }

    /**
     * Pass static key SERVER_URL
     * @return String preference
     */
    public static String getServerUrl () {
        return getString(SERVER_URL, "");
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

    public static void setLastChannel (int value) {
        if(sPreferences != null) {
            sPreferences.edit().putInt(LAST_CHANNEL, value).commit();
        }
    }

    public static int getLastChannel () {
        return getInt(LAST_CHANNEL, 0);
    }
}
