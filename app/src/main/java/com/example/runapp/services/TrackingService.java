package com.example.runapp.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.runapp.R;
import com.example.runapp.other.Constants;
import com.example.runapp.other.TrackingUtility;
import com.example.runapp.ui.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.NotificationManager.IMPORTANCE_LOW;


/**
 * Service to track a run
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class TrackingService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private static TrackingService instance = null;
    private final IBinder binder = new MyBinder();
    LocationManager locationManager;
    LocationListener locationListener;

    private MutableLiveData<Boolean> isTracking = new MutableLiveData<Boolean>();
    private MutableLiveData<ArrayList<ArrayList<LatLng>>> pathPoints = new MutableLiveData<>();
    private MutableLiveData<Long> timeInMillis = new MutableLiveData<Long>();
    private MutableLiveData<Long> timeInMillisStill = new MutableLiveData<Long>();
    private MutableLiveData<Integer> timeInSeconds = new MutableLiveData<Integer>();
    private MutableLiveData<Integer> timeInSecondsPrevious = new MutableLiveData<Integer>();

    private boolean isFirstRun = true;

    private NotificationCompat.Builder curNotification;

    // Variables needed to determine if user is still
    public GoogleApiClient mApiClient;
    private final String TRANSITIONS_RECEIVER_ACTION = "TRANSITIONS_RECEIVER_ACTION";
    private PendingIntent mActivityTransitionsPendingIntent;
    private TransitionsReceiver mTransitionsReceiver;
    private int currentActivity;

    private MutableLiveData<Boolean> isServiceKilled = new MutableLiveData<Boolean>();

    /**
     * Check if service is already running
     * @return
     */
    public static boolean isInstanceCreated() {
        return instance != null;
    }

    /**
     * Set the initial values of live data objects
     */
    private void postInitialValues(){
        isTracking.postValue(false);
        ArrayList<ArrayList<LatLng>> emptyList = new ArrayList();
        pathPoints.setValue(emptyList);
        timeInMillis.setValue(0L);
        timeInMillisStill.setValue(0L);
        isServiceKilled.postValue(false);
    }

    /**
     * Create an empty list and add it to the list of paths
     */
    private void addEmptyPolyLine(){
        if(pathPoints!=null){
            // Adds a new path
            ArrayList<ArrayList<LatLng>> cur = pathPoints.getValue();
            cur.add(new ArrayList<>());
            pathPoints.postValue(cur);
        }else{
            // Creates an empty list of lists
            ArrayList emptySet = new ArrayList();
            ArrayList emptyPolyLine = new ArrayList();
            emptySet.add(emptyPolyLine);
            pathPoints.postValue(emptySet);
        }
    }

    /**
     * This adds the location to the last list of pathPoints.
     * @param location current user location
     */
    private void addPathPoint(Location location) {
        LatLng pos = new LatLng(location.getLatitude(),location.getLongitude());
        ArrayList<ArrayList<LatLng>> cur = pathPoints.getValue();
        cur.get(cur.size()-1).add(pos);
        pathPoints.postValue(cur);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Handles creation of service
     */
    @Override
    public void onCreate() {
        postInitialValues();
        curNotification = baseNotificationBuilder();
        instance = this;
        startForegroundService();

        // Connect to google API
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();

        super.onCreate();
    }

    /**
     * Handles destruction of service. Resets the variables
     */
    @Override
    public void onDestroy() {
        instance=null;
        stopListeningToLocation();
        stopListeningToDetections();
        stopTimer();
        stopForeground(true);
        super.onDestroy();
    }

    /**
     * Called when StartService is called
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        if (intent.getAction() == Constants.ACTION_PAUSE_SERVICE) {
            servicePauseRunningService();
        } else if (intent.getAction() == Constants.ACTION_START_OR_RESUME_SERVICE) {
            serviceContinueRunningService();
        } else if (intent.getAction() == Constants.ACTION_STOP_SERVICE) {
            // Occurs when service is killed from notification
            isServiceKilled.postValue(true);
            stopSelf();
        }
        return START_STICKY;
    }

    /**
     * Called when Google API connects
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        mTransitionsReceiver = new TransitionsReceiver();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Class supports communication with the tracking activity
     */
    public class MyBinder extends Binder implements IInterface {

        @Override
        public IBinder asBinder() {
            return this;
        }

        public void pauseRunningService(){
            servicePauseRunningService();
        }

        public void continueRunningService(){
            serviceContinueRunningService();
        }

        /**
         * @return isTracking to indicate if the service is currently tracking the run
         */
        public MutableLiveData<Boolean> getIsTracking(){
            return isTracking;
        }

        /**
         * @return isServiceKilled to indicate if the service is killed or not
         */
        public MutableLiveData<Boolean> getIsServiceKilled(){
            return isServiceKilled;
        }

        /**
         * @return timeInMillis to indicate the time for the current run
         */
        public MutableLiveData<Long> getTimeInMillis(){
            return timeInMillis;
        }

        /**
         * @return timeInMillisStill to indicate the time the user was still
         */
        public MutableLiveData<Long> getStillTimeInMillis(){
            return timeInMillisStill;
        }


        /**
         * @return pathpoints to display the paths the user has taken during a run
         */
        public MutableLiveData<ArrayList<ArrayList<LatLng>>> getPathPoints(){
            return pathPoints;
        }



    }

    /**
     * This allows the service to keep tracking the user location and time whenever
     * the user clicks the toggle start button
     */
    public void serviceContinueRunningService(){
        startTracking();
        addEmptyPolyLine();
        startListeningToLocation();
        startListeningToDetections();
        if(isFirstRun){
            isFirstRun = false;
        }
        initialiseTimer();
    }

    /**
     * This stops the service from tracking the location and time when
     * the user clicks the toggle stop button
     */
    public void servicePauseRunningService(){
        pauseTracking();
        stopListeningToLocation();
        stopListeningToDetections();
        stopTimer();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * Starts this service as a foreground service and creates the necessary notification
     */
    public void startForegroundService() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }
        startForeground(Constants.NOTIFICATION_ID,curNotification.build());

    }

    /**
     * Creates a base notification template
     * @return notification template
     */
    public NotificationCompat.Builder baseNotificationBuilder(){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                .setContentTitle("RunApp")
                .setContentText(TrackingUtility.millisFormatted(timeInMillis.getValue(),false))
                .setContentIntent(getMainActivityPendingIntent());
        return notificationBuilder;
    }

    /**
     * Creates a pending intent that allows the user to return to the tracking activity when a notification is clicked
     * @return
     */
    private PendingIntent getMainActivityPendingIntent(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION_SHOW_TRACKING_FRAGMENT);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

    /**
     * Creates a notification channel
     * @param notificationManager
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Updates the action buttons of the notification and adds the quit button
     */
    @SuppressLint("RestrictedApi")
    private void updateNotificationTrackingState(Boolean isTracking) {
        String notificationActionText;
        PendingIntent pendingIntent;
        PendingIntent pendingIntentQuit;

        Intent quitIntent = new Intent(this, TrackingService.class);
        quitIntent.setAction(Constants.ACTION_STOP_SERVICE);
        pendingIntentQuit = PendingIntent.getService(this,1,quitIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Check if the run is paused or not and assign the correct action
        if(isTracking){
            notificationActionText = "Pause";
            Intent pauseIntent = new Intent(this, TrackingService.class);
            pauseIntent.setAction(Constants.ACTION_PAUSE_SERVICE);
            pendingIntent = PendingIntent.getService(this,1,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
            notificationActionText = "Resume";
            Intent resumeIntent = new Intent(this, TrackingService.class);
            resumeIntent.setAction(Constants.ACTION_START_OR_RESUME_SERVICE);
            pendingIntent = PendingIntent.getService(this,1,resumeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Clear the current actions of the notification and re-add the appropriate actions
        curNotification.mActions.clear();
        curNotification = baseNotificationBuilder();
        curNotification.addAction(R.drawable.ic_baseline_directions_run_24,notificationActionText,pendingIntent);
        curNotification.addAction(R.drawable.ic_baseline_directions_run_24,"Quit",pendingIntentQuit);
        notificationManager.notify(Constants.NOTIFICATION_ID, curNotification.build());

    }

    /**
     * Start tracking a run
     */
    private void startTracking(){
        isTracking.postValue(true);
        // Update the notification buttons
        updateNotificationTrackingState(true);
    }

    /**
     * Pause a run
     */
    private void pauseTracking(){
        isTracking.postValue(false);
        // Update the notification buttons
        updateNotificationTrackingState(false);
    }

    /**
     * Listen to phone movements
     */
    private void startListeningToDetections(){
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient,3000,mActivityTransitionsPendingIntent);
        registerReceiver(mTransitionsReceiver, new IntentFilter(TRANSITIONS_RECEIVER_ACTION));
    }

    /**
     * Stop listening to phone movements
     */
    private void stopListeningToDetections(){
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient,mActivityTransitionsPendingIntent);

        // Unregister to the broadcast receiver if it doesn't exist
        try {
            unregisterReceiver(mTransitionsReceiver);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listens to user location and adds a path point every time the location changes
     */
    private void startListeningToLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                addPathPoint(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5, // minimum time interval between updates
                    5, // minimum distance between updates, in metres
                    locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            Log.d("g53mdp", e.toString());
        }
    }

    /**
     * Stop listening for location updates
     */
    private void stopListeningToLocation(){
        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);

        }
    }

    // Initialise variables for timer
    private Counter counter;
    public long totalTime = 0L;
    public long totalTimeStill = 0L;

    /**
     * Create a new counter object and start timer
     */
    private void initialiseTimer(){
        counter = new Counter();
        startTimer();
    }

    /**
     * Start timer
     */
    private void startTimer(){
        counter.startRunning();
    }

    /**
     * Stop timer
     */
    private void stopTimer(){
        if(counter!=null){
            counter.stopRunning();
        }
    }

    /**
     * Acts as a stopwatch for a run
     */
    protected class Counter extends Thread implements Runnable {
        public boolean running = false;
        public long timeStarted = 0L;
        public long lapTime = 0L;
        public long timeStartedStill = 0L;
        public long lapTimeStill = 0L;

        public Counter() {
            this.start();
        }

        public void run() {
            // While the user is tracking a run, update the timer
            while(this.running) {
                // Calculate time in millis and seconds
                lapTime = System.currentTimeMillis() - timeStarted;
                timeInMillis.postValue(totalTime+lapTime);
                double tempMillis = timeInMillis.getValue()/1000;
                int tempSeconds = (int) Math.floor(tempMillis);
                timeInSeconds.postValue(tempSeconds);

                // Update notification after every second
                if(timeInSeconds.getValue() != timeInSecondsPrevious.getValue()){
                    NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    curNotification.setContentText(TrackingUtility.millisFormatted(timeInMillis.getValue(),false));
                    notificationManager.notify(Constants.NOTIFICATION_ID, curNotification.build());
                    timeInSecondsPrevious.postValue(timeInSeconds.getValue());
                }

                // Update the amount of time the user is idle
                if(currentActivity==DetectedActivity.STILL){
                    lapTimeStill = System.currentTimeMillis() - timeStartedStill;
                    timeInMillisStill.postValue(totalTimeStill+lapTimeStill);
                }

                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Accumulate the total times and reset the relevant variables if the user continues running in the future
         */
        public void stopRunning() {
            totalTime = totalTime + lapTime;
            timeInMillis.postValue(totalTime);
            lapTime = 0L;
            stopStillTimer();
            running = false;
        }
        /**
         * Accumulate the still time and reset the relevant variables
         */
        public void stopStillTimer(){
            currentActivity = DetectedActivity.UNKNOWN;
            totalTimeStill = totalTimeStill + lapTimeStill;
            timeInMillisStill.postValue(totalTimeStill);
            lapTimeStill = 0L;
        }

        /**
         * Starts the still timer and changes the current movement activity
         */
        public void startStillTimer(){
            timeStartedStill = System.currentTimeMillis();
            currentActivity = DetectedActivity.STILL;
        }

        /**
         * Method to start the timer again
         */
        public void startRunning(){
            timeStarted = System.currentTimeMillis();
            running = true;
            startStillTimer();
        }

    }

    /**
     * Handles intents from from the Transitions API.
     */
    public class TransitionsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            // Detect what is the most likeliest activity that the user is doing
            // Check if the new activity is different to the previously saved activity
            DetectedActivity probableActivity = result.getMostProbableActivity();
            if(currentActivity != probableActivity.getType()){
                handleDetectedActivity(probableActivity);
            }
        }

        /**
         * Starts/Stops the still timer depending on if the user is currently idle
         * @param probableActivities
         */
        private void handleDetectedActivity(DetectedActivity probableActivities) {
            switch(probableActivities.getType()) {
                case DetectedActivity.STILL: {
                    counter.startStillTimer();
                    break;
                }
                default: {
                    counter.stopStillTimer();
                    break;
                }
                /**
                 case DetectedActivity.IN_VEHICLE: {
                 Log.d(TAG, "handleDetectedActivity: IN_VEHICLE");
                 transitions.add("IN_VEHICLE");
                 currentActivityText.setText(transitions.toString());
                 break;
                 }
                 case DetectedActivity.ON_BICYCLE: {
                 Log.d(TAG, "handleDetectedActivity: ON_BICYCLE");
                 transitions.add("ON_BICYCLE");
                 currentActivityText.setText(transitions.toString());
                 break;
                 }
                 case DetectedActivity.RUNNING: {
                 Log.d(TAG, "handleDetectedActivity: RUNNING");
                 transitions.add("RUNNING");
                 currentActivityText.setText(transitions.toString());
                 break;
                 }
                 case DetectedActivity.WALKING: {
                 Log.d(TAG, "handleDetectedActivity: WALKING");
                 transitions.add("WALKING");
                 currentActivityText.setText(transitions.toString());
                 break;
                 }
                 **/
            }
        }
    }


}
