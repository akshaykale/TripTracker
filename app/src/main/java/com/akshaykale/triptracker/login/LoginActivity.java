package com.akshaykale.triptracker.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.akshaykale.triptracker.MainActivity;
import com.akshaykale.triptracker.R;
import com.akshaykale.triptracker.model.MUser;
import com.akshaykale.triptracker.utils.firebase.FirebaseDataManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by akshaykale on 2017/08/14.
 */

public class LoginActivity extends Activity implements FacebookCallback<LoginResult> {

    private final String TAG = getClass().getSimpleName();

    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;

    private ProgressDialog progressDialog;

    FirebaseDataManager firebaseDataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.setPadding(30,30,10,30);

        loginButton.registerCallback(mCallbackManager, this);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating with server.");
    }

    private void handleFacebookAccessToken(AccessToken token) {
        progressDialog.show();

        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            if (user != null){
                                //save user
                                new FirebaseDataManager().saveUserInfo(new MUser(
                                        user.getDisplayName(),
                                        user.getEmail(),
                                        user.getPhotoUrl().toString()
                                ));
                                launchMainActivity();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.hide();

                        }

                        // ...
                    }
                });
    }

    private void launchMainActivity() {



        progressDialog.dismiss();
        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "facebook:onSuccess:" + loginResult);
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "facebook:onCancel");
    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "facebook:onError", error);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

