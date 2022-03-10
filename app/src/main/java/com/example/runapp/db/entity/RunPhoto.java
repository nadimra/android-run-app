package com.example.runapp.db.entity;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "run_photo_table")
public class RunPhoto {

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setRun_id(int run_id) {
        this.run_id = run_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public int get_id() {
        return _id;
    }

    public int getRun_id() {
        return run_id;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int _id;

    @ColumnInfo(name = "run_id")
    private int run_id;

    @ColumnInfo(name = "photo_id")
    private int photo_id;

    public RunPhoto(int id, int run_id, int photo_id) {
        this._id = id;
        this.run_id = run_id;
        this.photo_id = photo_id;
    }

    @Ignore
    public RunPhoto(int run_id, int photo_id) {
        this.run_id = run_id;
        this.photo_id = photo_id;
    }
}