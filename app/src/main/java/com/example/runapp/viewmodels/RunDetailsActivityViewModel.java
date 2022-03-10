package com.example.runapp.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.runapp.db.MyRepository;
import com.example.runapp.db.entity.Run;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Viewmodel for the run details fragment
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class RunDetailsActivityViewModel extends AndroidViewModel {
    private MyRepository repository;
    private int currentRunId;
    private Run currentRun;

    public RunDetailsActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new MyRepository(application);
    }

    /**
     * Sets the current run being inspected based on the id
     * @param run_id
     */
    public void setRun(int run_id){
        currentRun =  repository.getRun(run_id);
    }

    public Run getRun(){
        return currentRun;
    }

    public int getCurrentRunId(){
        return currentRunId;
    }

    public void setCurrentRunId(int run_id){
        currentRunId = run_id;
    }

    /**
     * Returns a list of bitmaps of all the photos for a specific run
     * @param run_id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public LiveData<List<Bitmap>> getRunPhotos(int run_id) throws ExecutionException, InterruptedException {
        return repository.getRunPhotos(run_id);
    }

    public void deleteRun(Run run){
        repository.deleteRun(run);
    }
}
