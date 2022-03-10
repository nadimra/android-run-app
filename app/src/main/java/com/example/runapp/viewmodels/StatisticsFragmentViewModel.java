package com.example.runapp.viewmodels;

import android.app.Application;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.runapp.db.MyRepository;
import com.example.runapp.db.entity.Run;
import com.example.runapp.other.DayTuple;

import java.util.List;

public class StatisticsFragmentViewModel extends AndroidViewModel {
    private MyRepository repository;

    //Daily stats
    private LiveData<Integer> dailyDistance;
    private LiveData<Long> dailyTotalTime;
    private LiveData<Long> dailyCalories;
    private LiveData<Float> dailyAvgSpeed;

    //Weekly stats
    private LiveData<Integer> weeklyDistance;
    private LiveData<Long> weeklyTotalTime;
    private LiveData<Long> weeklyCalories;
    private LiveData<Float> weeklyAvgSpeed;

    //All time stats
    private LiveData<Integer> allTimeDistance;
    private LiveData<Long> allTimeTotalTime;
    private LiveData<Long> allTimeCalories;
    private LiveData<Float> allTimeAvgSpeed;

    private LiveData<List<Run>> runsSortedByDate;
    private LiveData<List<DayTuple>> totalDistancePerDay;

    public StatisticsFragmentViewModel(@NonNull Application application) {
        super(application);
        repository = new MyRepository(application);
        runsSortedByDate = repository.getAllRunsSortedByDate();

        // All time stats
        allTimeDistance= repository.getTotalDistance();
        allTimeTotalTime= repository.getTotalTimeInMillis();
        allTimeCalories = repository.getTotalCaloriesBurned();
        allTimeAvgSpeed = repository.getTotalAvgSpeed();

        // Daily stats
        dailyDistance= repository.getWeeklyDistance();
        dailyTotalTime= repository.getWeeklyTimeInMillis();
        dailyCalories = repository.getWeeklyCaloriesBurned();
        dailyAvgSpeed = repository.getWeeklyAvgSpeed();

        // Weekly stats
        weeklyDistance= repository.getDailyDistance();
        weeklyTotalTime= repository.getDailyTimeInMillis();
        weeklyCalories = repository.getDailyCaloriesBurned();
        weeklyAvgSpeed = repository.getDailyAvgSpeed();

        // List of distances each day
        totalDistancePerDay = repository.getTotalDistancePerDay();
    }

    public LiveData<Integer> getDailyDistance() {
        return dailyDistance;
    }

    public LiveData<Long> getDailyTotalTime() {
        return dailyTotalTime;
    }

    public LiveData<Long> getDailyCalories() {
        return dailyCalories;
    }

    public LiveData<Float> getDailyAvgSpeed() {
        return dailyAvgSpeed;
    }

    public LiveData<Integer> getAllTimeDistance() {
        return allTimeDistance;
    }

    public LiveData<Long> getAllTimeTotalTime() {
        return allTimeTotalTime;
    }

    public LiveData<Long> getAllTimeCalories() {
        return allTimeCalories;
    }

    public LiveData<Float> getAllTimeAvgSpeed() {
        return allTimeAvgSpeed;
    }

    public LiveData<Integer> getWeeklyDistance() {
        return weeklyDistance;
    }

    public LiveData<Long> getWeeklyTotalTime() {
        return weeklyTotalTime;
    }

    public LiveData<Long> getWeeklyCalories() {
        return weeklyCalories;
    }

    public LiveData<Float> getWeeklyAvgSpeed() {
        return weeklyAvgSpeed;
    }

    public LiveData<List<Run>> getRunsSortedByDate() {
        return runsSortedByDate;
    }

    public LiveData<List<DayTuple>> getTotalDistancePerDay() {
        return totalDistancePerDay;
    }

    public void setDailyDistance(LiveData<Integer> dailyDistance) {
        this.dailyDistance = dailyDistance;
    }

    public void setDailyTotalTime(LiveData<Long> dailyTotalTime) {
        this.dailyTotalTime = dailyTotalTime;
    }

    public void setDailyCalories(LiveData<Long> dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    public void setDailyAvgSpeed(LiveData<Float> dailyAvgSpeed) {
        this.dailyAvgSpeed = dailyAvgSpeed;
    }

    public void setWeeklyDistance(LiveData<Integer> weeklyDistance) {
        this.weeklyDistance = weeklyDistance;
    }

    public void setWeeklyTotalTime(LiveData<Long> weeklyTotalTime) {
        this.weeklyTotalTime = weeklyTotalTime;
    }

    public void setWeeklyCalories(LiveData<Long> weeklyCalories) {
        this.weeklyCalories = weeklyCalories;
    }

    public void setWeeklyAvgSpeed(LiveData<Float> weeklyAvgSpeed) {
        this.weeklyAvgSpeed = weeklyAvgSpeed;
    }

    public void setAllTimeDistance(LiveData<Integer> allTimeDistance) {
        this.allTimeDistance = allTimeDistance;
    }

    public void setAllTimeTotalTime(LiveData<Long> allTimeTotalTime) {
        this.allTimeTotalTime = allTimeTotalTime;
    }

    public void setAllTimeCalories(LiveData<Long> allTimeCalories) {
        this.allTimeCalories = allTimeCalories;
    }

    public void setAllTimeAvgSpeed(LiveData<Float> allTimeAvgSpeed) {
        this.allTimeAvgSpeed = allTimeAvgSpeed;
    }

    public void setRunsSortedByDate(LiveData<List<Run>> runsSortedByDate) {
        this.runsSortedByDate = runsSortedByDate;
    }

    public void setTotalDistancePerDay(LiveData<List<DayTuple>> totalDistancePerDay) {
        this.totalDistancePerDay = totalDistancePerDay;
    }
}
