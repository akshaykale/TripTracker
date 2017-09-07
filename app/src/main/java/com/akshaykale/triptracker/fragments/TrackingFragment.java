package com.akshaykale.triptracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akshaykale.triptracker.R;
import com.akshaykale.triptracker.model.MTrip;
import com.akshaykale.triptracker.model.MTripPoint;
import com.akshaykale.triptracker.utils.firebase.FirebaseDataManager;
import com.akshaykale.triptracker.utils.firebase.IFTripPointsListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by akshay.kale on 17/08/2017.
 */

public class TrackingFragment extends Fragment implements OnMapReadyCallback, IFTripPointsListener {

    public static final String KEY_TRIP_ID = "trip_id";

    private GoogleMap mGoogleMap;

    @BindView(R.id.map_tracking_frag)
    MapView mMapView;

    private String tripId = null;

    private MTrip mTrip = new MTrip();

    private FirebaseDataManager firebaseDataManager;

    PolylineOptions routePolylineOptions;

    public static TrackingFragment getInstance(String trip_id){
        TrackingFragment trackingFragment = new TrackingFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TRIP_ID, trip_id);
        trackingFragment.setArguments(args);
        return trackingFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);
        ButterKnife.bind(this, view);

        tripId = getArguments().getString(KEY_TRIP_ID);

        firebaseDataManager = new FirebaseDataManager();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        //Get the data from Firebase DB
        firebaseDataManager.getAllTripPoints(tripId, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mGoogleMap != null){
            return;
        }
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        routePolylineOptions = new PolylineOptions();
        routePolylineOptions.geodesic(true);
    }

    @Override
    public void onTripPointLoadedSuccessfully(MTripPoint tripPoint) {
        if (tripPoint == null)
            return;
        LatLng latLng = new LatLng(tripPoint.latitude,tripPoint.longitude);
        routePolylineOptions.add();

        mGoogleMap.addPolyline(routePolylineOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
    }
}