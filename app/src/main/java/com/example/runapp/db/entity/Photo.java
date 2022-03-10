package com.example.runapp.db.entity;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "photo_table")
public class Photo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int _id;

    @ColumnInfo(name = "img")
    private Bitmap img;

    public Photo(int id, Bitmap img) {
        this._id = id;
        this.img = img;
    }

    @Ignore
    public Photo(Bitmap img) {
        this.img = img;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public int get_id() {
        return _id;
    }

    public Bitmap getImg() {
        return img;
    }


}
