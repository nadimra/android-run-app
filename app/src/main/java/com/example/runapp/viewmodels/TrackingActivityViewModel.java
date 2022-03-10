package com.example.runapp.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.runapp.db.MyRepository;
import com.example.runapp.db.entity.Photo;
import com.example.runapp.db.entity.Run;
import com.example.runapp.db.entity.RunPhoto;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Viewmodel for tracking activity
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class TrackingActivityViewModel extends AndroidViewModel {
    private MyRepository repository;
    private Long timestamp;
    private Long timeInMillis;
    private Long stillTimeInMillis;
    private Float avgSpeedInKMH;
    private int caloriesBurned;
    private int distanceInMeters;
    private Date date;
    private Time time;
    private ArrayList<Uri> imageList;
    private MutableLiveData<Bitmap> currentBitmap;

    private byte[] byteArray;

    public TrackingActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new MyRepository(application);
        imageList = new ArrayList<Uri>();
        currentBitmap = new MutableLiveData<>();
    }

    public long insertRun(Run run){
        return repository.insertRun(run);
    }

    public long insertPhoto(Photo photo){
       return repository.insertPhoto(photo);
    }

    public void insertRunPhoto(RunPhoto runPhoto){
        repository.insertRunPhoto(runPhoto);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public Long getStillTimeInMillis() {
        return stillTimeInMillis;
    }

    public Float getAvgSpeedInKMH() {
        return avgSpeedInKMH;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public int getDistanceInMeters() {
        return distanceInMeters;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimeInMillis(Long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public void setStillTimeInMillis(Long stillTimeInMillis) {
        this.stillTimeInMillis = stillTimeInMillis;
    }

    public void setAvgSpeedInKMH(Float avgSpeedInKMH) {
        this.avgSpeedInKMH = avgSpeedInKMH;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public void setDistanceInMeters(int distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void resetPhotoList(){
        imageList.clear();
    }

    public void addNewPhoto(Uri image){
        imageList.add(image);
    }

    public ArrayList<Uri> getPhotoList(){
        return imageList;
    }

    /**
     * Sets map image
     * @param byteArray
     */
    public void setByteArray(byte[] byteArray){
        this.byteArray = byteArray;
    }

    public byte[] getByteArray(){
        return byteArray;
    }
}
