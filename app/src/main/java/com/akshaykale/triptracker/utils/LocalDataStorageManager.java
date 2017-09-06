package com.akshaykale.triptracker.utils;

import android.content.SharedPreferences;
import android.net.Uri;

import com.akshaykale.triptracker.TripTrackerApplication;

/**
 * Created by akshay.kale on 23/05/2017.
 */

public class LocalDataStorageManager {

    private final String TAG = getClass().getSimpleName();
    private static LocalDataStorageManager sInstance;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Uri imageUrl;

    private LocalDataStorageManager(){
        sharedPreferences = TripTrackerApplication.getAppContext().
                getSharedPreferences(TAG, TripTrackerApplication.getAppContext().MODE_PRIVATE);

        editor = sharedPreferences.edit();

    }

    public static LocalDataStorageManager getInstance(){
        if(sInstance == null){
            sInstance = new LocalDataStorageManager();
        }
        return sInstance;
    }

    public void clear(){
        editor.clear();
        editor.apply();
    }

    public String getTrackID(String x){
        return sharedPreferences.getString("tracking_id",x);
    }
    public void setTrackID(String tid){
        editor.putString("tracking_id", tid);
        editor.apply();
    }

    public long getTrackStartTime(long x){
        return sharedPreferences.getLong("tracking_start_time",x);
    }
    public void setTrackStartTime(long tid){
        editor.putLong("tracking_start_time", tid);
        editor.apply();
    }

    public long getTrackEndTime(long x){
        return sharedPreferences.getLong("tracking_end_time",x);
    }
    public void setTrackEndTime(long tid){
        editor.putLong("tracking_end_time", tid);
        editor.apply();
    }

    public void currentTripId(String key) {
        editor.putString("trip_id", key);
        editor.apply();
    }
    public String currentTripId(){
        return sharedPreferences.getString("trip_id","");
    }
}
