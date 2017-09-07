package com.akshaykale.triptracker.model;

import com.akshaykale.swipetimeline.TimelineObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by akshay.kale on 17/08/2017.
 */

@IgnoreExtraProperties
public class MTrip implements TimelineObject {

    public String trip_id;
    public String trip_desc;
    public String trip_name;

    public long start_time;
    public long end_time;

    public float total_distance;

    public String image_url;

    public String trip_type;

    //List<MTripPoint> trip_points;

    public MTrip() {
        trip_id = "";
        trip_desc = "";
        trip_name = "";
        start_time = System.currentTimeMillis();
        end_time = 0;
        //trip_points = new ArrayList<>();
        total_distance = 0f;
        image_url = "";
        trip_type = "";
    }

    @Exclude
    @Override
    public long getTimestamp() {
        return start_time;
    }

    @Exclude
    @Override
    public String getTitle() {
        return trip_name;
    }

    @Exclude
    @Override
    public String getImageUrl() {
        return image_url.equals("")? FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString():image_url;
    }
}
