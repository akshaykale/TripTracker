package com.akshaykale.triptracker.utils.firebase;

import com.akshaykale.triptracker.model.MTrip;
import com.akshaykale.triptracker.model.MTripPoint;
import com.akshaykale.triptracker.model.MUser;
import com.akshaykale.triptracker.utils.LocalDataStorageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

    public void getAllTrips(final IFTripsListener ifTripsListener) {
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MTrip trip = dataSnapshot.getValue(MTrip.class);
                ifTripsListener.onTripLoadedSuccessfully(trip);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        userTripsRef.addChildEventListener(childListener);
    }

    public void getAllTripPoints(String tripId, final IFTripPointsListener ifTripPointsListener) {
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MTripPoint tripPoint = dataSnapshot.getValue(MTripPoint.class);
                ifTripPointsListener.onTripPointLoadedSuccessfully(tripPoint);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        userTripsRef.child(tripId).child("trip_points").addChildEventListener(childListener);
    }
}
