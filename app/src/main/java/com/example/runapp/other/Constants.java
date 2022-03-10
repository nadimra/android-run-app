package com.example.runapp.other;

import android.graphics.Color;

/**
 * Regular values to be used in the program
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class Constants {

    // Notifications
    public static String NOTIFICATION_CHANNEL_ID = "tracking_channel";
    public static String NOTIFICATION_CHANNEL_NAME = "Tracking";
    public static int NOTIFICATION_ID = 1;

    // Service Intent Actions
    public static String ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT";
    public static String ACTION_START_OR_RESUME_SERVICE = "ACTION_START_SERVICE";
    public static String ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE";
    public static String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";

    // Map Options
    public static int POLYLINE_COLOR = Color.RED;
    public static float POLYLINE_WIDTH = 8f;
    public static float MAP_ZOOM = 15f;

    // Shared Preferences
    public static String SHARED_PREFERENCES_NAME = "sharedPref";
    public static String KEY_NAME = "KEY_NAME";
    public static String KEY_WEIGHT = "KEY_WEIGHT";
    public static String KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE";

}
