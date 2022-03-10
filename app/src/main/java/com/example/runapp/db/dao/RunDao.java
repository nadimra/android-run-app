package com.example.runapp.db.dao;

import android.database.Cursor;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.runapp.db.entity.Run;
import com.example.runapp.db.entity.RunPhoto;
import com.example.runapp.other.DayTuple;

import java.sql.Date;
import java.util.List;

@Dao
public interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRun(Run run);

    @Query("DELETE FROM run_table")
    void deleteAll();

    @Delete
    void deleteRun(Run run);

    @Query("SELECT * FROM run_table ORDER BY timestamp DESC")
    LiveData<List<Run>> getAllRunsSortedByDate();

    @Query("SELECT * FROM run_table ORDER BY timestamp DESC")
    Cursor getAllRunsSortedByDateContentProvider();

    @Query("SELECT * FROM run_table ORDER BY timeInMillis DESC")
    LiveData<List<Run>> getAllRunsSortedByTimeInMillis();

    @Query("SELECT * FROM run_table ORDER BY caloriesBurned DESC")
    LiveData<List<Run>> getAllRunsSortedByCaloriesBurned();

    @Query("SELECT * FROM run_table ORDER BY distanceInMetres DESC")
    LiveData<List<Run>> getAllRunsSortedByDistance();

    @Query("SELECT * FROM run_table ORDER BY avgSpeedInKMH DESC")
    LiveData<List<Run>> getAllRunsSortedByAvgSpeed();

    @Query("SELECT COALESCE(SUM(timeInMillis),0) FROM run_table WHERE strftime('%j', datetime(timestamp/1000, 'unixepoch')) = strftime('%j', 'now')")
    LiveData<Long> getDailyTimeInMillis();

    @Query("SELECT COALESCE(SUM(distanceInMetres),0) FROM run_table WHERE strftime('%j', datetime(timestamp/1000, 'unixepoch')) = strftime('%j', 'now')")
    LiveData<Integer> getDailyDistance();

    @Query("SELECT COALESCE(AVG(avgSpeedInKMH),0) FROM run_table WHERE strftime('%j', datetime(timestamp/1000, 'unixepoch')) = strftime('%j', 'now')")
    LiveData<Float> getDailyAvgSpeed();

    @Query("SELECT COALESCE(SUM(caloriesBurned),0) FROM run_table WHERE strftime('%j', datetime(timestamp/1000, 'unixepoch')) = strftime('%j', 'now')")
    LiveData<Long> getDailyCaloriesBurned();

    @Query("SELECT COALESCE(SUM(timeInMillis),0) FROM run_table WHERE strftime('%W', datetime(timestamp/1000, 'unixepoch')) = strftime('%W', 'now')")
    LiveData<Long> getWeeklyTimeInMillis();

    @Query("SELECT COALESCE(SUM(distanceInMetres),0) FROM run_table WHERE strftime('%W', datetime(timestamp/1000, 'unixepoch')) = strftime('%W', 'now')")
    LiveData<Integer> getWeeklyDistance();

    @Query("SELECT COALESCE(AVG(avgSpeedInKMH),0) FROM run_table WHERE strftime('%W', datetime(timestamp/1000, 'unixepoch')) = strftime('%W', 'now')")
    LiveData<Float> getWeeklyAvgSpeed();

    @Query("SELECT COALESCE(SUM(caloriesBurned),0) FROM run_table WHERE strftime('%W', datetime(timestamp/1000, 'unixepoch')) = strftime('%W', 'now')")
    LiveData<Long> getWeeklyCaloriesBurned();

    @Query("SELECT COALESCE(SUM(timeInMillis),0) FROM run_table")
    LiveData<Long> getTotalTimeInMillis();

    @Query("SELECT COALESCE(SUM(distanceInMetres),0) FROM run_table")
    LiveData<Integer> getTotalDistance();

    @Query("SELECT COALESCE(AVG(avgSpeedInKMH),0) FROM run_table")
    LiveData<Float> getTotalAvgSpeed();

    @Query("SELECT COALESCE(SUM(caloriesBurned),0) FROM run_table")
    LiveData<Long> getTotalCaloriesBurned();

    @Query("SELECT SUM(distanceInMetres) AS dist,strftime('%w', datetime(timestamp/1000, 'unixepoch')) AS day " +
            "from run_table WHERE strftime('%W', datetime(timestamp/1000, 'unixepoch')) = strftime('%W', 'now') " +
            "GROUP BY strftime('%w', datetime(timestamp/1000, 'unixepoch'))")
    LiveData<List<DayTuple>> getTotalDistancePerDay();

    @Query("SELECT * FROM run_table WHERE _id"+" = :id")
    Run selectById(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertRunPhoto(RunPhoto runPhoto);

    @Query("SELECT img FROM photo_table JOIN run_photo_table ON (run_photo_table.photo_id = photo_table._id) "
            + "WHERE run_photo_table.run_id"+"= :runId ")
    LiveData<List<Bitmap>> getRunPhotos(int runId);

}
