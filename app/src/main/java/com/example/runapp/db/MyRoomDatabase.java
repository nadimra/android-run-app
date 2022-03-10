package com.example.runapp.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.runapp.R;
import com.example.runapp.db.dao.PhotoDao;
import com.example.runapp.db.dao.RunDao;
import com.example.runapp.db.entity.Photo;
import com.example.runapp.db.entity.Run;
import com.example.runapp.db.entity.RunPhoto;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room database class
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
@Database(entities = {Run.class, Photo.class, RunPhoto.class}, version = 6, exportSchema = false) // drop and recreat
@TypeConverters(Converters.class)
public abstract class MyRoomDatabase extends RoomDatabase {

    public abstract RunDao runDao();
    public abstract PhotoDao photoDao();

    private static volatile MyRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // Runs db operations on separate thread
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Returns an instance of the database if it exists, otherwise it will create the database
    public static MyRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyRoomDatabase.class, "run_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(createCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback createCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                RunDao runDao = INSTANCE.runDao();
                PhotoDao photoDao = INSTANCE.photoDao();

                runDao.deleteAll();
                photoDao.deleteAll();
            });
        }
    };
}
