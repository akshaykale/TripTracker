package com.akshaykale.triptracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akshaykale.swipetimeline.TimelineFragment;
import com.akshaykale.swipetimeline.TimelineGroupType;
import com.akshaykale.swipetimeline.TimelineObject;
import com.akshaykale.swipetimeline.TimelineObjectClickListener;
import com.akshaykale.triptracker.R;
import com.akshaykale.triptracker.TripDetailsActivity;
import com.akshaykale.triptracker.model.MTrip;
import com.akshaykale.triptracker.utils.firebase.FirebaseDataManager;
import com.akshaykale.triptracker.utils.firebase.IFTripsListener;

import butterknife.ButterKnife;

/**
 * Created by akshay.kale on 07/09/2017.
 */

public class TripTimelineFragment extends Fragment implements TimelineObjectClickListener, IFTripsListener {

    private final String TAG = getClass().getSimpleName();

    public static TripTimelineFragment getInstance(){
        return new TripTimelineFragment();
    }

    private TimelineGroupType timelineGroupType = TimelineGroupType.MONTH;

    TimelineFragment timelineFragment;

    FirebaseDataManager firebaseDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_timeline, container, false);
        ButterKnife.bind(this,view);

        firebaseDataManager = new FirebaseDataManager();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpTimelineFragment();

        firebaseDataManager.getAllTrips(this);
    }

    private void setUpTimelineFragment() {
        timelineFragment = new TimelineFragment();
        timelineFragment.addOnClickListener(this);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_timeline, timelineFragment);
        transaction.commit();

        //Clear previous data
        timelineFragment.clearData();
    }

    @Override
    public void onTimelineObjectClicked(TimelineObject timelineObject) {
        MTrip trip = (MTrip) timelineObject;
        Intent intent = new Intent(getActivity(), TripDetailsActivity.class);
        intent.putExtra("tripid",trip.trip_id);
        startActivity(intent);
    }

    @Override
    public void onTimelineObjectLongClicked(TimelineObject timelineObject) {

    }

    @Override
    public void onTripLoadedSuccessfully(MTrip trip) {
        if (trip == null)
            return;
        timelineFragment.addSingleObject(trip, timelineGroupType);
    }
}
