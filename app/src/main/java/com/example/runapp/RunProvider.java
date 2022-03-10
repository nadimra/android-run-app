package com.example.runapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.runapp.db.MyRoomDatabase;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Allows other apps to access run database content
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class RunProvider extends ContentProvider{
     private static final UriMatcher uriMatcher;

     // Setup uri's for different tables
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RunProviderContract.AUTHORITY, "run", 1);
        uriMatcher.addURI(RunProviderContract.AUTHORITY, "run/#", 2);
        uriMatcher.addURI(RunProviderContract.AUTHORITY, "*", 3);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(Uri uri) {
        String contentType;

        if (uri.getLastPathSegment()==null) {
            contentType = RunProviderContract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = RunProviderContract.CONTENT_TYPE_SINGLE;
        }

        return contentType;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException("not implemented");
    }


    /**
     * Returns a cursor based on the query
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(getContext());
        switch(uriMatcher.match(uri)) {
            case 1:
                // Display all runs
                Cursor runs = null;
                Future<Cursor> run;
                run = MyRoomDatabase.databaseWriteExecutor.submit(new RunProvider.RunsCallable(db));
                try {
                    runs =  run.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return runs;
            default:
                return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        throw new UnsupportedOperationException("not implemented");
    }

    private static class RunsCallable implements Callable<Cursor> {
        MyRoomDatabase db;
        public RunsCallable(MyRoomDatabase db) {
            this.db = db;
        }
        @Override
        public Cursor call() throws Exception {
            return db.runDao().getAllRunsSortedByDateContentProvider();
        }
    }

}


