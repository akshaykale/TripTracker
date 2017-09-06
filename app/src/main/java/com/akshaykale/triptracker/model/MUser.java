package com.akshaykale.triptracker.model;

/**
 * Created by akshay.kale on 06/09/2017.
 */

public class MUser {

    String display_name;
    String email;
    String photo_url;
    long last_login;

    public MUser(String name, String email) {
        this.display_name = name;
        this.email = email;
        this.last_login = System.currentTimeMillis();
    }

    public MUser(String name, String email, String url) {
        this.display_name = name;
        this.email = email;
        this.photo_url = url;
        this.last_login = System.currentTimeMillis();
    }
}
