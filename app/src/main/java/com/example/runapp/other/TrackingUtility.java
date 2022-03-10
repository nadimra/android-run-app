package com.example.runapp.other;

import android.app.ActivityManager;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Useful functions related to the tracking of runs
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class TrackingUtility {


    /**
     * Static method to convert milliseconds into hh:mm:ss format
     * @param millis
     * @param includeMillis true if milliseconds is included in return statement
     * @return string format of the milliseconds
     */
    public static String millisFormatted(Long millis,boolean includeMillis)
    {
        String hoursString;
        String minutesString;
        String secondsString;
        String millisString;
        String formattedString;
        long milliseconds = millis;

        // Calculate hours
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        milliseconds -= TimeUnit.HOURS.toMillis(hours);
        if(hours<10){
            hoursString = "0"+hours;
        }else{
            hoursString = ""+hours;
        }
        // Calculate minutes
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes);
        if(minutes<10){
            minutesString = "0"+minutes;
        }else{
            minutesString = ""+minutes;
        }
        // Calculate seconds
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds);
        if(seconds<10){
            secondsString = "0"+seconds;
        }else{
            secondsString = ""+seconds;
        }
        // Calculate milliseconds
        milliseconds /= 10;
        if(milliseconds<10){
            millisString = "0"+milliseconds;
        }else{
            millisString = ""+milliseconds;
        }

        // Return string in correct format
        if(includeMillis){
            formattedString = hoursString +":"+minutesString+":"+secondsString+"."+millisString;
        }else{
            formattedString = hoursString +":"+minutesString+":"+secondsString;
        }
        return formattedString;
    }



    /**
     * Calculates the length of a specific polyline
     */
    public static Float calculatePolylineLength(ArrayList<LatLng> polyline) {
        float distance = 0f;
        for (int i=0; i< (polyline.size() - 2) ; i++) {
            LatLng pos1 = polyline.get(i);
            LatLng pos2 = polyline.get(i+1);
            float[] result = new float[1];
            Location.distanceBetween(pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude, result);
            distance += result[0];
        }
        return distance;
    }

}
