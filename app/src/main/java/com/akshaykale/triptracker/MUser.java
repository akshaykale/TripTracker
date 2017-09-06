package com.akshaykale.triptracker;

/**
 * Created by akshay.kale on 06/09/2017.
 */

class MUser {

    String display_name;
    String email;
    String photo_url;
    long acount_created;

    public MUser(String name, String email) {
        this.display_name = name;
        this.email = email;
        this.acount_created = System.currentTimeMillis();
    }

    public MUser(String name, String email, String url) {
        this.display_name = name;
        this.email = email;
        this.photo_url = url;
        this.acount_created = System.currentTimeMillis();
    }
}
