package com.akshaykale.triptracker.utils.firebase;

import com.akshaykale.triptracker.model.MTripPoint;

/**
 * Created by akshay.kale on 07/09/2017.
 */

public interface IFTripPointsListener {
    void onTripPointLoadedSuccessfully(MTripPoint tripPoint);
}
