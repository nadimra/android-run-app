package com.example.runapp;

import android.net.Uri;

/**
 * Contract for run database
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class RunProviderContract {
    public static final String AUTHORITY = "uk.ac.nott.cs.runs";

    public static final Uri RUN_URI = Uri.parse("content://"+AUTHORITY+"/run/");
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");

    public static final String RUN_ID = "_id";
    public static final String RUN_TIMESTAMP = "timestamp";
    public static final String RUN_DISTANCE_IN_METRES = "distanceInMetres";
    public static final String RUN_IMG = "img";
    public static final String RUN_AVG_SPEED_IN_KMH = "avgSpeedInKMH";
    public static final String RUN_TIME_IN_MILLIS = "timeInMillis";
    public static final String RUN_CALORIES_BURNED = "caloriesBurned";
    public static final String RUN_DESCRIPTION = "description";
    public static final String RUN_RATING = "rating";
    public static final String RUN_WEATHER = "weather";

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/RunProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/RunProvider.data.text";
}
