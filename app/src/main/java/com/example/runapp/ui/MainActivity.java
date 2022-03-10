package com.example.runapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.runapp.R;
import com.example.runapp.other.Constants;
import com.example.runapp.viewmodels.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main activity that hosts several fragments
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        bottomNavigation=findViewById(R.id.bottom_nav);
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavMethod);

        // Initialise to correct fragment on the creation of the activity
        initFragment();
    }

    /**
     * Handles the navigation of different fragments when the user clicks on the bottom navigation
     */
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment=null;

                    // Gets the id of the clicked menu item
                    // Updates viewmodel variable and sets the current fragment
                    switch(item.getItemId()){
                        case R.id.nav_runs:
                            fragment = new RunFragment();
                            viewModel.setCurrentFragment(R.id.nav_runs);
                            break;
                        case R.id.nav_statistics:
                            fragment = new StatisticsFragment();
                            viewModel.setCurrentFragment(R.id.nav_statistics);
                            break;
                        case R.id.nav_challenge:
                            fragment = new ChallengeFragment();
                            viewModel.setCurrentFragment(R.id.nav_challenge);
                            break;
                        case R.id.nav_settings:
                            fragment = new SettingsFragment();
                            viewModel.setCurrentFragment(R.id.nav_settings);
                            break;
                    }
                    // Replace the current fragment with the new fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                    return true;
                }
            };

    /**
     * Initialise to the correct fragment
     */
    public void initFragment(){
        // Check if the user is entering the app for the first time
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Boolean firstTime = sharedPreferences.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE,true);
        if(firstTime){
            // Save setup fragment
            viewModel.setCurrentFragment(R.id.nav_setup);
        }
        int currentFragment = viewModel.getCurrentFragment();
        Fragment fragment=null;

        // Load correct fragment based on viewmodel result
        switch(currentFragment){
            case R.id.nav_runs:
                fragment = new RunFragment();
                break;
            case R.id.nav_statistics:
                fragment = new StatisticsFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_challenge:
                    fragment = new ChallengeFragment();
                break;
            case R.id.nav_setup:
                androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);
                bottomNavigation.setVisibility(View.GONE);
                fragment = new SetupFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }

    /**
     * Checks if the activity was launched from the notification
     * @param intent
     */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        navigateToTrackingActivityIfNeeded(intent);
    }

    /**
     * Navigate to the tracking activity from restart of the app
     * @param intent
     */
    private void navigateToTrackingActivityIfNeeded(Intent intent) {
        if(intent.getAction() == Constants.ACTION_SHOW_TRACKING_FRAGMENT) {
            intent = new Intent(MainActivity.this, TrackingActivity.class);
            startActivity(intent);
        }
    }
}