package com.akshaykale.triptracker.utils.firebase;

import com.akshaykale.triptracker.model.MTrip;

/**
 * Created by akshay.kale on 07/09/2017.
 */

public interface IFTripsListener {
    void onTripLoadedSuccessfully(MTrip trip);
}
