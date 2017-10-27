package com.akshaykale.triptracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.akshaykale.triptracker.home.HomeActivity;
import com.akshaykale.triptracker.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by akshay.kale on 17/08/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startLoginActivity();
        }else {
            startMainActivity();
        }
    }

    private void startLoginActivity() {
        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();
    }

    private void startMainActivity() {
        startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
        finish();
    }
}