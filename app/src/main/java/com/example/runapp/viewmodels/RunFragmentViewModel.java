package com.example.runapp.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.runapp.R;
import com.example.runapp.db.MyRepository;
import com.example.runapp.db.entity.Run;
import com.example.runapp.other.SpinnerOptions;

import java.util.List;

/**
 * Viewmodel for run fragment
 */
public class RunFragmentViewModel extends AndroidViewModel {
    private MyRepository repository;
    private LiveData<List<Run>> runsSortedByAvgSpeed;
    private LiveData<List<Run>> runsSortedByDate;
    private LiveData<List<Run>> runsSortedByDistance;
    private LiveData<List<Run>> runsSortedByCaloriesBurned;
    private LiveData<List<Run>> runsSortedByTimeInMillis;
    private MediatorLiveData<List<Run>> runs;
    private MutableLiveData<SpinnerOptions> currentSpinnerOption;
    private int currentSpinnerOptionPos = 0;

    public RunFragmentViewModel(@NonNull Application application) {
        super(application);
        repository = new MyRepository(application);

        // Get database query results
        runsSortedByAvgSpeed= repository.getAllRunsSortedByAvgSpeed();
        runsSortedByDate= repository.getAllRunsSortedByDate();
        runsSortedByDistance= repository.getAllRunsSortedByDistance();
        runsSortedByCaloriesBurned= repository.getAllRunsSortedByCaloriesBurned();
        runsSortedByTimeInMillis= repository.getAllRunsSortedByTimeInMillis();

        // Initialise spinner options
        if (currentSpinnerOption == null) {
            currentSpinnerOption = new MutableLiveData<SpinnerOptions>();
            currentSpinnerOption.setValue(SpinnerOptions.DATE);
        }

        // Adds all live data variables to mediator variable which combines everything
        runs = new MediatorLiveData<>();
        runs.addSource(getAllRunsSortedByTimeInMillis(), value -> {
            runs.setValue(value);
        });
        runs.addSource(getAllRunsSortedByDistance(), value -> {
            runs.setValue(value);
        });
        runs.addSource(getAllRunsSortedByAvgSpeed(), value -> {
            runs.setValue(value);
        });
        runs.addSource(getAllRunsSortedByCaloriesBurned(), value -> {
            runs.setValue(value);
        });
        runs.addSource(getAllRunsSortedByDate(), value -> {
            runs.setValue(value);
        });
        Log.d("g53mdp","run view model created");
    }

    public LiveData<List<Run>> getAllRunsSortedByAvgSpeed() {
        return runsSortedByAvgSpeed;
    }
    public LiveData<List<Run>> getAllRunsSortedByDate() {
        return runsSortedByDate;
    }
    public LiveData<List<Run>> getAllRunsSortedByDistance() {
        return runsSortedByDistance;
    }
    public LiveData<List<Run>> getAllRunsSortedByCaloriesBurned() { return runsSortedByCaloriesBurned; }
    public LiveData<List<Run>> getAllRunsSortedByTimeInMillis() { return runsSortedByTimeInMillis; }

    /**
     * Returns live data of chosen live data object from spinner
     * @return sorted runs
     */
    public MediatorLiveData<List<Run>> getCurrentSortedRuns(){ return runs;}

    /**
     * Sets the value of the mediator variable based on the spinner option
     * @param pos
     */
    public void setCurrentSpinnerOption(int pos) {
        currentSpinnerOptionPos = pos;
        if(runs!=null){
            switch (pos) {
                case 0:
                    currentSpinnerOption.setValue(SpinnerOptions.DATE);
                    if(runsSortedByDate.getValue()!= null){
                        runs.setValue(runsSortedByDate.getValue());
                    }
                    break;
                case 1:
                    currentSpinnerOption.setValue(SpinnerOptions.TIME_IN_MILLIS);
                    if(runsSortedByTimeInMillis.getValue()!= null){
                        runs.setValue(runsSortedByTimeInMillis.getValue());
                    }
                    break;
                case 2:
                    currentSpinnerOption.setValue(SpinnerOptions.DISTANCE);
                    if(runsSortedByDistance.getValue()!= null){
                        runs.setValue(runsSortedByDistance.getValue());
                    }
                    break;
                case 3:
                    currentSpinnerOption.setValue(SpinnerOptions.AVG_SPEED);
                    if(runsSortedByAvgSpeed.getValue()!= null){
                        runs.setValue(runsSortedByAvgSpeed.getValue());
                    }
                    break;
                case 4:
                    currentSpinnerOption.setValue(SpinnerOptions.CALORIES_BURNED);
                    if(runsSortedByCaloriesBurned.getValue()!= null){
                        runs.setValue(runsSortedByCaloriesBurned.getValue());
                    }
                    break;
            }
        }
    }

    public int getCurrentSpinnerOptionPos(){
        return currentSpinnerOptionPos;
    }

}
