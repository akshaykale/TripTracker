package com.akshaykale.triptracker;

import java.util.ArrayList;

/**
 * Created by akshay.kale on 17/08/2017.
 */

public class MTrip {

    public String trip_id;
    public String trip_desc;
    public String trip_name;

    public long start_time;
    public long end_time;

    ArrayList<MTripPoint> trip_points;

    public MTrip() {
        trip_id = "";
        trip_desc = "";
        trip_name = "";
        start_time = System.currentTimeMillis();
        end_time = 0;
        trip_points = new ArrayList<>();
    }
}
