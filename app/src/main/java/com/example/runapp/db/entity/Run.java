package com.example.runapp.db.entity;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "run_table")
public class Run {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int _id;

    @ColumnInfo(name = "timestamp")
    private Long timestamp;

    @ColumnInfo(name = "distanceInMetres")
    private int distanceInMetres;

    @ColumnInfo(name = "img")
    private Bitmap img;

    @ColumnInfo(name = "avgSpeedInKMH")
    private Float avgSpeedInKMH;

    @ColumnInfo(name = "timeInMillis")
    private Long timeInMillis;

    @ColumnInfo(name = "caloriesBurned")
    private int caloriesBurned;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "rating")
    private float rating;

    @ColumnInfo(name = "weather")
    private String weather;

    public Run(int id,Long timestamp, int distanceInMetres,Bitmap img, Float avgSpeedInKMH,
               Long timeInMillis, int caloriesBurned, String description, float rating, String weather) {
        this._id = id;
        this.timestamp = timestamp;
        this.distanceInMetres = distanceInMetres;
        this.img = img;
        this.avgSpeedInKMH = avgSpeedInKMH;
        this.timeInMillis = timeInMillis;
        this.caloriesBurned = caloriesBurned;
        this.description = description;
        this.rating = rating;
        this.weather = weather;
    }

    @Ignore
    public Run(Long timestamp,int distanceInMetres, Bitmap img, Float avgSpeedInKMH,
               Long timeInMillis, int caloriesBurned,String description, float rating, String weather) {
        this.timestamp = timestamp;
        this.distanceInMetres = distanceInMetres;
        this.img = img;
        this.avgSpeedInKMH = avgSpeedInKMH;
        this.timeInMillis = timeInMillis;
        this.caloriesBurned = caloriesBurned;
        this.description = description;
        this.rating = rating;
        this.weather = weather;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getDistanceInMetres() {
        return distanceInMetres;
    }

    public void setDistanceInMetres(int distanceInMetres) {
        this.distanceInMetres = distanceInMetres;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public Float getAvgSpeedInKMH() {
        return avgSpeedInKMH;
    }

    public void setAvgSpeedInKMH(Float avgSpeedInKMH) {
        this.avgSpeedInKMH = avgSpeedInKMH;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(Long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getDescription() {
        return description;
    }

    public float getRating() {
        return rating;
    }

    public String getWeather() {
        return weather;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
