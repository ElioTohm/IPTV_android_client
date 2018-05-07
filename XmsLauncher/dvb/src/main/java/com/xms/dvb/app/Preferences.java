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
    private static String STARTING_URL = "STARTING_URL";
    private static String PORT = "PORT";
    private static String NUMBER_OF_CHANNELS = "NUMBER_OF_CHANNELS";
    private static String URI_HOPE = "URI_HOPE";
    private static String PORT_HOPE = "PORT_HOPE";

    /**
     * init Preference Manager
     * @param context
     */
    @SuppressLint("CommitPrefEdits")
    static void init(Context context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

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

    public static String getStartingUrl () {
        return sPreferences.getString(STARTING_URL, "");
    }

    public static int getPORT () {
        return sPreferences.getInt(PORT, 0);
    }

    public static int getNumberOfChannels () {
        return sPreferences.getInt(NUMBER_OF_CHANNELS, 0);
    }

    public static int getUriHope () {
        return sPreferences.getInt(URI_HOPE, 0);
    }

    public static int getPortHope () {
        return sPreferences.getInt(PORT_HOPE, 0);
    }

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

    public static void setStartingUrl (String value) {
        if(sPreferences != null) {
            sPreferences.edit().putString(STARTING_URL, value).commit();
        }
    }

    public static void setPORT (int value) {
        if(sPreferences != null) {
            sPreferences.edit().putInt(PORT, value).commit();
        }
    }

    public static void setNumberOfChannels (int value) {
        if(sPreferences != null) {
            sPreferences.edit().putInt(NUMBER_OF_CHANNELS, value).commit();
        }
    }

    public static void setUriHope (int value) {
        if(sPreferences != null) {
            sPreferences.edit().putInt(URI_HOPE, value).commit();
        }
    }

    public static void setPortHope (int value) {
        if(sPreferences != null) {
            sPreferences.edit().putInt(PORT_HOPE, value).commit();
        }
    }
}
