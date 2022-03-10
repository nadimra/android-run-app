package com.example.runapp.db;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.runapp.db.dao.PhotoDao;
import com.example.runapp.db.dao.RunDao;
import com.example.runapp.db.entity.Photo;
import com.example.runapp.db.entity.Run;
import com.example.runapp.db.entity.RunPhoto;
import com.example.runapp.other.DayTuple;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Repository handling the work with photos and runs.
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class MyRepository {
    private static RunDao runDao;
    private static PhotoDao photoDao;

    public MyRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        runDao = db.runDao();
        photoDao = db.photoDao();
    }

    /**
     * Insert run instance into the database
     * @param run entity
     * @return id of run
     */
    public long insertRun(Run run){
        Callable<Long> insertCallable = () -> runDao.insertRun(run);
        long rowId = 0;
        Future<Long> future = MyRoomDatabase.databaseWriteExecutor.submit(insertCallable);
        try {
            rowId = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return rowId;
    }

    /**
     * Insert photo instance into the database
     * @param photo entity
     * @return id of photo
     */
    public long insertPhoto(Photo photo) {
        Callable<Long> insertCallable = () -> photoDao.insertPhoto(photo);
        long rowId = 0;

        Future<Long> future = MyRoomDatabase.databaseWriteExecutor.submit(insertCallable);
        try {
            rowId = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return rowId;
    }

    /**
     * Insert photo id with corresponding run id
     * @param runPhoto entity
     */
    public void insertRunPhoto(RunPhoto runPhoto) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            runDao.insertRunPhoto(runPhoto);
        });
    }

    /**
     * Deletes all elements from run table
     */
    public void deleteAll(){
        runDao.deleteAll();
    }

    /**
     * Deletes a specific run
     * @param run
     */
    public void deleteRun(Run run){
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            runDao.deleteRun(run);
        });
    }

    /**
     * Get all runs stored in the database sorted by date
     * @return all runs
     */
    public LiveData<List<Run>> getAllRunsSortedByDate(){
        return runDao.getAllRunsSortedByDate();
    }

    /**
     * Get all runs stored in the database sorted by duration
     * @return all runs
     */
    public LiveData<List<Run>> getAllRunsSortedByTimeInMillis(){
        return runDao.getAllRunsSortedByTimeInMillis();
    }

    /**
     * Get all runs stored in the database sorted by distance travelled
     * @return all runs
     */
    public LiveData<List<Run>> getAllRunsSortedByDistance(){
        return runDao.getAllRunsSortedByDistance();
    }

    /**
     * Get all runs stored in the database sorted by date
     * @return all runs
     */
    public LiveData<List<Run>> getAllRunsSortedByCaloriesBurned(){
        return runDao.getAllRunsSortedByCaloriesBurned();
    }

    /**
     * Get all runs stored in the database sorted by average speed
     * @return all runs
     */
    public LiveData<List<Run>> getAllRunsSortedByAvgSpeed(){
        return runDao.getAllRunsSortedByAvgSpeed();
    }

    /**
     * Get total distance travelled today
     * @return distance travelled
     */
    public LiveData<Integer> getDailyDistance(){
        return runDao.getDailyDistance();
    }

    /**
     * Get total time travelled today
     * @return amount of time
     */
    public LiveData<Long> getDailyTimeInMillis(){
        return runDao.getDailyTimeInMillis();
    }

    /**
     * Get average speed travelled today
     * @return average speed in kmh
     */
    public LiveData<Float> getDailyAvgSpeed(){
        return runDao.getDailyAvgSpeed();
    }

    /**
     * Get total calories burned today
     * @return calories burned
     */
    public LiveData<Long> getDailyCaloriesBurned(){
        return runDao.getDailyCaloriesBurned();
    }

    /**
     * Get total distance travelled this week
     * @return distance travelled this week
     */
    public LiveData<Integer> getWeeklyDistance(){
        return runDao.getWeeklyDistance();
    }

    /**
     * Get total time travelled this week
     * @return time in millisecs travelled this week
     */
    public LiveData<Long> getWeeklyTimeInMillis(){
        return runDao.getWeeklyTimeInMillis();
    }

    /**
     * Get average speed travelled this week
     * @return average speed in kmh
     */
    public LiveData<Float> getWeeklyAvgSpeed(){
        return runDao.getWeeklyAvgSpeed();
    }

    /**
     * Get total calories burned this week
     * @return calories burned
     */
    public LiveData<Long> getWeeklyCaloriesBurned(){
        return runDao.getWeeklyCaloriesBurned();
    }

    /**
     * Get total distance of all time
     * @return distance travelled
     */
    public LiveData<Integer> getTotalDistance(){
        return runDao.getTotalDistance();
    }

    /**
     * Get total time of all time
     * @return time in milliseconds
     */
    public LiveData<Long> getTotalTimeInMillis(){
        return runDao.getTotalTimeInMillis();
    }

    /**
     * Get average speed of all time
     * @return average speed in kmh
     */
    public LiveData<Float> getTotalAvgSpeed(){
        return runDao.getTotalAvgSpeed();
    }

    /**
     * Get total total distance of all time
     * @return distance travelled
     */
    public LiveData<Long> getTotalCaloriesBurned(){
        return runDao.getTotalCaloriesBurned();
    }

    /**
     * Get total total distance per day
     * @return list of distance travelled for each day
     */
    public LiveData<List<DayTuple>> getTotalDistancePerDay(){
        return runDao.getTotalDistancePerDay();
    }

    /**
     * Returns a specific run given an id
     * @param run_id
     * @return run entity
     */
    public Run getRun(int run_id){
        Callable<Run> insertCallable = () ->runDao.selectById(run_id);
        Run run = null;

        Future<Run> future = MyRoomDatabase.databaseWriteExecutor.submit(insertCallable);
        try {
            run = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return run;
    }

    /**
     * Returns a list of photos which were uploaded for a specific run
     * @return list of photos
     */
    public LiveData<List<Bitmap>> getRunPhotos(int run_id) throws ExecutionException, InterruptedException {
        Future<LiveData<List<Bitmap>>> listOfPhotos;
        listOfPhotos = MyRoomDatabase.databaseWriteExecutor.submit(new RunPhotosCallable(run_id));
        return listOfPhotos.get();
    }

    /**
     * Callable class to return the list of photos
     */
    private static class RunPhotosCallable implements Callable<LiveData<List<Bitmap>>> {
        int i;
        public RunPhotosCallable(int i) {
            this.i = i;
        }

        @Override
        public LiveData<List<Bitmap>> call() throws Exception {
            return runDao.getRunPhotos(i);
        }
    }
}
