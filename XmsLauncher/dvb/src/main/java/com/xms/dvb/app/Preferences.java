package com.xms.dvb.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private static SharedPreferences sPreferences;
    private static String SERVER_URL = "SERVER_URL";
    private static String XML_FILE_NAME = "XML_FILE_NAME";
    private static String LAST_CHANNEL = "LAST_CHANNEL";
    private static String XML_VERSION = "XML_VERSION";

    /**
     * init Preference Manager
     * @param context
     */
    @SuppressLint("CommitPrefEdits")
    static void init(Context context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Pass static key SERVER_URL
     * @return String preference
     */
    public static String getServerUrl () {
        return sPreferences.getString(SERVER_URL, "");
    }

    public static int getLastChannel () {
        return sPreferences.getInt(LAST_CHANNEL, 0);
    }

    public static String getXmlVersion () {
        return sPreferences.getString(XML_VERSION, "");
    }

    public static String getXmlFileName () {
        return sPreferences.getString(XML_FILE_NAME, "");
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

    public static void setXmlVersion (String value) {
        if(sPreferences != null) {
            sPreferences.edit().putString(XML_VERSION, value).commit();
        }
    }

    public static void setXmlFileName(String value) {
        if(sPreferences != null) {
            sPreferences.edit().putString(XML_FILE_NAME, value).commit();
        }
    }
}
