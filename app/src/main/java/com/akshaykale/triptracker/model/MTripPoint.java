package com.akshaykale.triptracker.model;

/**
 * Created by akshay.kale on 17/08/2017.
 */

public class MTripPoint {

    public double latitude;
    public double longitude;
    public double altitude;

    public float accuracy;
    public float speed;

    public long timestamp;

    public String picture_url;

    public MTripPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = System.currentTimeMillis();
        this.altitude = 0;
        this.accuracy = 0;
        this.speed = 0;
        this.picture_url = "";
    }
}
