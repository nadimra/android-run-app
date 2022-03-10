package com.example.runapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.runapp.R;
import com.example.runapp.db.entity.Run;
import com.example.runapp.other.Constants;
import com.example.runapp.other.NetworkChangeReceiver;
import com.example.runapp.other.TrackingUtility;
import com.example.runapp.services.TrackingService;
import com.example.runapp.viewmodels.TrackingActivityViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private MapView mapView;
    private boolean isTracking = false;
    private Button btnStartService;
    private Button btnToggleRun;
    private Button btnFinishRun;
    private TextView stopWatchText;
    private ArrayList<ArrayList<LatLng>> pathPoints = new ArrayList<ArrayList<LatLng>>();
    private Boolean firstTime;
    private long finalTimeInMillis;
    private long finalStillTimeInMillis;
    private TrackingActivityViewModel viewModel;
    private ImageView imagePlaceholder;
    private NetworkChangeReceiver networkReceiver = new NetworkChangeReceiver();

    // Define connection and communication between service
    private TrackingService.MyBinder myService = null;
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = (TrackingService.MyBinder) service;
            subscribeToObservers();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(TrackingActivityViewModel.class);

        // Initiate mapview
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Assign UI elements
        btnStartService = findViewById(R.id.startService);
        btnToggleRun = findViewById(R.id.startToggleButton);
        btnFinishRun = findViewById(R.id.finishButton);
        stopWatchText = findViewById(R.id.timerText);

        // Create button listeners
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonStartServiceClick();
            }
        });
        btnToggleRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleButtonClick();
            }
        });
        btnFinishRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopClick();
            }
        });

        // Check if the user is already in a run when this activity is entered
        if(TrackingService.isInstanceCreated()){
            onButtonStartServiceClick();
        }else{
            // Set up activity for new run
            toggleUiState(false);
        }

        // Listens network changes
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    /**
     * Subscribes to changes of LiveData objects
     */
    private void  subscribeToObservers(){
        firstTime = true;

        // Initiates the toggle button text
        if(myService.getIsTracking().getValue()){
            btnToggleRun.setText(R.string.stop);
        }else{
            btnToggleRun.setText(R.string.start);
        }

        // Checks if the service was set to be destroyed from a notification
        final Observer<Boolean> isKilledObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean i) {
                if(i){
                    killService();
                    toggleUiState(false);
                }
            }
        };
        myService.getIsServiceKilled().observe(this,isKilledObserver);

        // Adds polylines to map to show the user movement and move camera
        final Observer<ArrayList<ArrayList<LatLng>>> pathPointsObserver = new Observer<ArrayList<ArrayList<LatLng>>>() {
            @Override
            public void onChanged(ArrayList<ArrayList<LatLng>> i) {
                pathPoints.clear();
                pathPoints.addAll(i);
                if(firstTime){
                    addAllPolylines();
                    firstTime = false;
                }else{
                    addLatestPolyline();
                }
                moveCameraToUser();
            }
        };
        myService.getPathPoints().observe(this,pathPointsObserver);

        // Checks whether user movement is being tracked or not
        final Observer<Boolean> isTrackingObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean i) {
                updateTracking(i);
                toggleStartStopButton(i);
            }
        };
        myService.getIsTracking().observe(this,isTrackingObserver);

        // Updates stopwatch time
        final Observer<Long> timeInMillisObserver = new Observer<Long>() {
            @Override
            public void onChanged(Long i) {
                String timeFormat = TrackingUtility.millisFormatted(i,true);
                stopWatchText.setText(timeFormat);
            }
        };
        myService.getTimeInMillis().observe(this, timeInMillisObserver);

    }

    /**
     * Adds all polylines to the pathPoints list to display them after screen rotations or if the use re-enters the app
     */
    private void addAllPolylines() {
        for (ArrayList<LatLng> polyline: pathPoints){
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(Constants.POLYLINE_COLOR)
                    .width(Constants.POLYLINE_WIDTH)
                    .addAll(polyline);
            map.addPolyline(polylineOptions);
        }
    }

    /**
     * Draws a polyline between the two latest points.
     */
    private void addLatestPolyline() {
        // only add polyline if we have at least two elements in the last polyline
        if(map!=null){
            if (!pathPoints.isEmpty() && pathPoints.get(pathPoints.size()-1).size() > 1) {
                ArrayList<LatLng> lastPath = pathPoints.get(pathPoints.size()-1);
                LatLng preLastLatLng = lastPath.get(lastPath.size()-2);
                LatLng lastLatLng = lastPath.get(lastPath.size()-1);
                PolylineOptions polylineOptions = new PolylineOptions()
                        .color(Constants.POLYLINE_COLOR)
                        .width(Constants.POLYLINE_WIDTH)
                        .add(preLastLatLng)
                        .add(lastLatLng);

                map.addPolyline(polylineOptions);
            }
        }

    }

    /**
     * Will move the camera to the user's location.
     */
    private void moveCameraToUser() {
        if(map != null){
            if (!pathPoints.isEmpty() && !pathPoints.get(pathPoints.size()-1).isEmpty()) {
                ArrayList<LatLng> lastPath = pathPoints.get(pathPoints.size()-1);
                map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                lastPath.get(lastPath.size()-1),
                                Constants.MAP_ZOOM
                        )
                );
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //addAllPolylines();
        moveCameraToUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        // Rebinds to the service and adds all the polylines again
        if(myService!=null) {
            if(TrackingService.isInstanceCreated()){
                bindService(new Intent(TrackingActivity.this, TrackingService.class), serviceConnection, Context.BIND_AUTO_CREATE);
                addAllPolylines();
            }else{
                toggleUiState(false);
            }
        }
    }

    /**
     * Updates UI view whether a not a run is occurring at the time
     * @param runCreated true if run is already created, false otherwise
     */
    public void toggleUiState(boolean runCreated){
        if(runCreated){
            btnStartService.setVisibility(View.GONE);
            btnToggleRun.setVisibility(View.VISIBLE);
            btnFinishRun.setVisibility(View.VISIBLE);
            stopWatchText.setVisibility(View.VISIBLE);
        }else{
            btnStartService.setVisibility(View.VISIBLE);
            btnToggleRun.setVisibility(View.GONE);
            btnFinishRun.setVisibility(View.GONE);
            stopWatchText.setVisibility(View.GONE);
        }
    }

    /**
     * Updates the toggle button text to the correct text depending if the run is started / stopped
     * @param isTracking true is the user is tracking the run
     */
    public void toggleStartStopButton(boolean isTracking){
        if(isTracking){
            btnToggleRun.setText(R.string.stop);
        }else{
            btnToggleRun.setText(R.string.start);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

        // Check if service wasn't killed from notification, if not, unbind the service
        if(TrackingService.isInstanceCreated()) {
            if(myService!=null){
                unbindService(serviceConnection);
            }
        }
    }


    /**
     * Call binder methods when toggle button is clicked
     * This button is only shown when service is running
     */
    public void onToggleButtonClick(){
        if(!isTracking){
            if(myService!=null){
                myService.continueRunningService();
            }
        }else{
            if(myService!=null){
                myService.pauseRunningService();
            }
        }
    }

    /**
     * Creates a new run and starts a service
     */
    public void onButtonStartServiceClick(){
        // Create a new service and bind it
        Intent serviceIntent = new Intent(TrackingActivity.this, TrackingService.class);
        startService(serviceIntent);
        bindService(new Intent(TrackingActivity.this, TrackingService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        toggleUiState(true);
        if(map!=null){
            map.clear();
        }
    }

    /**
     * Stops the service
     */
    public void onStopClick(){
        finalTimeInMillis = myService.getTimeInMillis().getValue();
        finalStillTimeInMillis = myService.getStillTimeInMillis().getValue();

        if(myService!= null) {
            unbindService(serviceConnection);
            stopService(new Intent(TrackingActivity.this, TrackingService.class));
            myService = null;
            //map.clear();
        }
        saveRun();
    }

    /**
     * Kills the service if instructed to do so by the service variable
     */
    public void killService(){
        if(TrackingService.isInstanceCreated()) {
            if(myService!= null) {
                unbindService(serviceConnection);
                stopService(new Intent(TrackingActivity.this, TrackingService.class));
                myService = null;
                //map.clear();
            }
        }
    }

    /**
     * Gets all relevant information about the current run and loads
     * information to runSummary fragment
     */
    public void saveRun(){
        // Get snapshot of the map
        zoomToWholeTrack();
        map.snapshot(callback);

        // Calculate the distance in meters
        int distanceInMeters = 0;
        for (ArrayList<LatLng> polyline: pathPoints) {
            distanceInMeters = Math.round(TrackingUtility.calculatePolylineLength(polyline));
        }
        // Calculate average speed
        float avgSpeedInKMH = Math.round((distanceInMeters / 1000f) / (finalTimeInMillis / 1000f / 60 / 60) * 10) / 10f;

        // Calculate calories burned
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Float weight = sharedPreferences.getFloat(Constants.KEY_WEIGHT,0);
        int caloriesBurned = Math.round((distanceInMeters / 1000f) * weight);

        // Get timestamp of the run
        Long timestamp = Calendar.getInstance().getTimeInMillis();

        // Send all information to new fragment
        Bundle bundle = new Bundle();
        bundle.putLong("timestamp", timestamp);
        bundle.putLong("timeInMillis",finalTimeInMillis);
        bundle.putLong("stillTimeInMillis",finalStillTimeInMillis);
        bundle.putFloat("avgSpeedInKMH",avgSpeedInKMH);
        bundle.putInt("caloriesBurned",caloriesBurned);
        bundle.putInt("distanceInMeters",distanceInMeters);

        viewModel.resetPhotoList();
        Fragment fragment = new RunSummaryFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,fragment).commit();

    }

    GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
        @Override
        public void onSnapshotReady(Bitmap snapshot) {
            // Callback is called from the main thread, so we can modify the ImageView safely.
            // Use the same bitmap for the following snapshots.
            imagePlaceholder = findViewById(R.id.imgPlaceholder);
            imagePlaceholder.setImageBitmap(snapshot);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            snapshot.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            viewModel.setByteArray(byteArray);
        }
    };


    /**
     * Zooms out until the whole track is visible. Used to make a screenshot of the
     * MapView to save it in the database
     */
    private void zoomToWholeTrack() {
        boolean includedPoints = false;
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (ArrayList<LatLng> polyline : pathPoints) {
            for (LatLng point : polyline) {
                bounds.include(point);
                includedPoints = true;
            }
        }
        int width = mapView.getWidth();
        int height = mapView.getHeight();

        if(includedPoints){
            map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                            bounds.build(),
                            width,
                            height,
                            Math.round(height * 0.05f)
                    )
            );
        }

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        Log.d("g53mdp", "TrackingActivity onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("g53mdp", "TrackingActivity onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkReceiver);
        super.onDestroy();
    }
    /**
     * Updates the tracking variable and the UI accordingly
     */
    private void updateTracking(Boolean isTracking) {
        this.isTracking = isTracking;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}