package com.akshaykale.triptracker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by akshay.kale on 17/08/2017.
 */

public class FirebaseDataManager {

    private final String TAG = getClass().getSimpleName();
    FirebaseDatabase database;
    String tripId;
    DatabaseReference userDbRef;
    DatabaseReference userTripsRef;
    DatabaseReference userTripsPointsRef;

    public FirebaseDataManager() {
        this.database = FirebaseDatabase.getInstance();
        this.tripId = LocalDataStorageManager.getInstance().getTrackID("");
        userDbRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userTripsRef = userDbRef.child("trips");//.child(tripId);
        userTripsPointsRef = userTripsRef.child("trip_points");
    }

    public void saveTripPoint(MTripPoint tripPoint){
        DatabaseReference newRef = userTripsPointsRef.push();
        newRef.setValue(tripPoint);
    }

    public void startTrip(){
        HashMap<String, String> map = new HashMap<>();
        map.put("trip_id",LocalDataStorageManager.getInstance().getTrackID(""));
        userTripsRef.setValue(map);

        HashMap<String, Long> map1 = new HashMap<>();
        map1.put("start_time",System.currentTimeMillis());
        //DatabaseReference newRef = userTripsRef.push();
        userTripsRef.setValue(map1);
    }

    public void endTrip(){
        HashMap<String, Long> map1 = new HashMap<>();
        map1.put("end_time",LocalDataStorageManager.getInstance().getTrackEndTime(0));
        userTripsRef.setValue(map1);
    }

    public void pushTripPoint(String tripId){

    }

    public void saveUserInfo(MUser user){
        userDbRef.setValue(user);
    }
}
