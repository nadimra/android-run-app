package com.example.runapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.runapp.R;
import com.example.runapp.services.TrackingService;
import com.example.runapp.ui.adapters.RunAdaptor;
import com.example.runapp.viewmodels.MainActivityViewModel;
import com.example.runapp.viewmodels.RunFragmentViewModel;
import com.example.runapp.viewmodels.StatisticsFragmentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Run fragment displays all the runs
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class RunFragment extends Fragment implements View.OnClickListener {
    public static final int MY_PERMISSIONS_REQUEST_PROCESS_CALLS = 1;
    private RunFragmentViewModel viewModel;
    private TextView trackingRunText;

    public RunFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_run, container, false);

        // Display all the runs from the database in the recycler view
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRun);
        recyclerView.setHasFixedSize(true);
        final RunAdaptor adapter = new RunAdaptor(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        // Assign viewmodel
        viewModel = new ViewModelProvider(requireActivity()).get(RunFragmentViewModel.class);

        // Observe all runs
        viewModel.getCurrentSortedRuns().observe(requireActivity(), run -> {
            adapter.setData(run);
        });

        // Get spinner options to sort runs correctly
        Spinner spinner = view.findViewById(R.id.sortByFilter);
        spinner.setSelection(viewModel.getCurrentSpinnerOptionPos());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                String items=spinner.getSelectedItem().toString();
                viewModel.setCurrentSpinnerOption(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        // Check if permissions have been granted
        checkPermissions();

        // Check if service is already running
        trackingRunText = (TextView) view.findViewById(R.id.trackingRunText);
        if(TrackingService.isInstanceCreated()){
            trackingRunText.setVisibility(View.VISIBLE);
        }else{
            trackingRunText.setVisibility(View.GONE);
        }

        FloatingActionButton addNewRunButton = (FloatingActionButton) view.findViewById(R.id.addNewRunButton);
        addNewRunButton.setOnClickListener(this);

        return view;
    }


    /**
     * Ensures the correct permissions have been granted before proceeding
     */
    public void checkPermissions() {
        if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) ||
                (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)) ||
                (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACTIVITY_RECOGNITION))
        ){
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("This permission is required for this app to function");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", (dialog, which) ->
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACTIVITY_RECOGNITION},
                            MY_PERMISSIONS_REQUEST_PROCESS_CALLS
                    ));
            alertDialog.show();

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACTIVITY_RECOGNITION},
                    MY_PERMISSIONS_REQUEST_PROCESS_CALLS
            );
        }
    }

    public boolean getCurrentPermissions(){
        if ((ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) ==
                        PackageManager.PERMISSION_GRANTED)){
            return true;
        }else {
            return false;
        }
    }

    /**
     * Method to handle onclicks for buttons
     * @param view
     */
    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {
                case R.id.addNewRunButton:
                    // Prepares user to start a new run
                    if(getCurrentPermissions()){
                        intent = new Intent(getActivity(), TrackingActivity.class);
                        startActivity(intent);
                    }
                break;
        }
    }

    /**
     * Checks if service is still running every time this activity is resumed
     */
    @Override
    public void onResume() {
        if(TrackingService.isInstanceCreated()){
            trackingRunText.setVisibility(View.VISIBLE);
        }else{
            trackingRunText.setVisibility(View.GONE);
        }
        super.onResume();
    }


}