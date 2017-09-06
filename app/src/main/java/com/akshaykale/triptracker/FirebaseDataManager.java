package com.akshaykale.triptracker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by akshay.kale on 17/08/2017.
 */

public class FirebaseDataManager {

    private final String TAG = getClass().getSimpleName();
    FirebaseDatabase database;
    DatabaseReference userDbRef;
    DatabaseReference userTripsRef;

    public FirebaseDataManager() {
        this.database = FirebaseDatabase.getInstance();
        userDbRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userTripsRef = userDbRef.child("trips");//.child(tripId);
    }

    public void createTrip(MTrip trip){
        DatabaseReference ref = userTripsRef.push();
        //save tripId
        LocalDataStorageManager.getInstance().currentTripId(ref.getKey());
        trip.trip_id = ref.getKey();
        ref.setValue(trip);
    }

    public void endCurrentTrip(String tripId){
        LocalDataStorageManager.getInstance().currentTripId(""); //current tripId should be null
        userTripsRef.child(tripId).child("end_time").setValue(System.currentTimeMillis());
    }

    public void pushTripPoint(String tripId, MTripPoint tripPoint){
        if (tripId.equals(""))
            return;
        DatabaseReference ref = userTripsRef.child(tripId).child("trip_points").push();
        ref.setValue(tripPoint);
    }

    public void saveUserInfo(MUser user){
        userDbRef.setValue(user);
    }

    public void setTripName(String tripId, String name){
        if (tripId.equals(""))
            return;
        userTripsRef.child(tripId).child("trip_name").setValue(name);
    }

    public void setTripDesc(String tripId, String desc){
        if (tripId.equals(""))
            return;
        userTripsRef.child(tripId).child("trip_desc").setValue(desc);
    }
}
