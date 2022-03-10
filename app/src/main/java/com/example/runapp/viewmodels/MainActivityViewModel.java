package com.example.runapp.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Database;

import com.example.runapp.R;
import com.example.runapp.db.MyRepository;
import com.example.runapp.db.entity.Run;
import com.example.runapp.other.SpinnerOptions;
import com.example.runapp.ui.MainActivity;

import java.util.List;

/**
 * Viewmodel for mainactivity (and corresponding fragments)
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class MainActivityViewModel extends AndroidViewModel {
    private int currentFragment = R.id.nav_runs;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Retains fragment when activity is destroyed
     * @return
     */
    public int getCurrentFragment(){
        return currentFragment;
    }

    /**
     * Sets a new fragment based on bottom navigation click
     * @param id
     */
    public void setCurrentFragment(int id){
        currentFragment = id;
    }



}
