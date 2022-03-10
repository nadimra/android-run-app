package com.example.runapp.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.runapp.db.entity.Photo;
import com.example.runapp.db.entity.Run;

@Dao
public interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPhoto(Photo photo);

    @Query("DELETE FROM photo_table")
    void deleteAll();

}
