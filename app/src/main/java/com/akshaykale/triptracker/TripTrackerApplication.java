package com.akshaykale.triptracker;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by akshay.kale on 06/09/2017.
 */

public class TripTrackerApplication extends Application {

    static TripTrackerApplication sInstance;

    private GoogleApiHelper googleApiHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //googleApiHelper = new GoogleApiHelper(sInstance);

    }

    public static synchronized TripTrackerApplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }
    public static GoogleApiHelper getGoogleApiHelper() {
        return getsInstance().getGoogleApiHelperInstance();
    }
}
