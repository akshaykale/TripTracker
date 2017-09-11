package com.akshaykale.triptracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.akshaykale.triptracker.fragments.TrackingFragment;

/**
 * Created by akshay.kale on 07/09/2017.
 */

public class TripDetailsActivity extends AppCompatActivity{

    private String tripId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        tripId = getIntent().getStringExtra("tripid");

        loadFragment();
    }

    private void loadFragment() {
        if (tripId.equals(""))
            return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_trip_details_tracking, TrackingFragment.getInstance(tripId, true));
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
