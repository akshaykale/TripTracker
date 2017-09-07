package com.akshaykale.triptracker;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.akshaykale.triptracker.fragments.HomeFragment;
import com.akshaykale.triptracker.fragments.TrackingFragment;
import com.akshaykale.triptracker.fragments.TripTimelineFragment;
import com.akshaykale.triptracker.location.LocationRequestHelper;
import com.akshaykale.triptracker.location.LocationResultHelper;
import com.akshaykale.triptracker.location.LocationUpdatesBroadcastReceiver;
import com.akshaykale.triptracker.model.MTrip;
import com.akshaykale.triptracker.utils.LocalDataStorageManager;
import com.akshaykale.triptracker.utils.firebase.FirebaseDataManager;
import com.akshaykale.triptracker.utils.ui.BottomNavigationViewEx;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent. 2 Min
     */
    private static final long UPDATE_INTERVAL = 30 * 1000;
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    /**
     * Set the minimum displacement between location updates in meters // 5 meters
     */
    private static final long SMALLEST_DISPLACEMENT = 1;
    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 3;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    /**
     * The entry point to Google Play Services.
     */
    private GoogleApiClient mGoogleApiClient;
    private FirebaseDataManager firebaseDataManager;
    private int currentFragment = R.id.action_home;

    // UI Widgets.
    private Switch switchTripStatus = null;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_nav_menu)
    BottomNavigationViewEx bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //Bottom menu
        bottomNavigationView.enableAnimation(true);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.setTextVisibility(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Check if the user revoked runtime permissions.
        if (!checkPermissions()) {
            requestPermissions();
        }

        firebaseDataManager = new FirebaseDataManager();

        buildGoogleApiClient();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    /**
     * Builds {@link GoogleApiClient}, enabling automatic lifecycle management using
     * {@link GoogleApiClient.Builder#enableAutoManage(android.support.v4.app.FragmentActivity,
     * int, GoogleApiClient.OnConnectionFailedListener)}. I.e., GoogleApiClient connects in
     * {@link AppCompatActivity#onStart}, or if onStart() has already happened, it connects
     * immediately, and disconnects automatically in {@link AppCompatActivity#onStop}.
     */
    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //updateButtonsState(LocationRequestHelper.getRequesting(this));
        //mLocationUpdatesResultView.setText(LocationResultHelper.getSavedLocationResult(this));
    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem toggelSwitch = menu.findItem(R.id.action_trip_switch);
        switchTripStatus = (Switch) toggelSwitch.getActionView();
        switchTripStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showSnackbar("Switch=> "+b);
                if (b && !LocationRequestHelper.getRequesting(getApplicationContext())){
                    requestLocationUpdates();
                }else if (!b && LocationRequestHelper.getRequesting(getApplicationContext())){
                    removeLocationUpdates();
                }
                if (currentFragment == R.id.action_home){
                    loadInitialFragment();
                }
            }
        });
        updateButtonsState(LocationRequestHelper.getRequesting(this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            LocalDataStorageManager.getInstance().clear();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(LocationResultHelper.KEY_LOCATION_UPDATES_RESULT)) {
            //mLocationUpdatesResultView.setText(LocationResultHelper.getSavedLocationResult(this));
        } else if (s.equals(LocationRequestHelper.KEY_LOCATION_UPDATES_REQUESTED)) {
            updateButtonsState(LocationRequestHelper.getRequesting(this));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        final String text = "Connection suspended";
        Log.w(TAG, text + ": Error code: " + i);
        showSnackbar("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        final String text = "Exception while connecting to Google Play services";
        Log.w(TAG, text + ": " + connectionResult.getErrorMessage());
        showSnackbar(text);
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.bottom_nav_menu);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. Kick off the process of building and connecting
                // GoogleApiClient.
                buildGoogleApiClient();
            } else {
                // Permission denied.
                /**
                 * Notify the user via a SnackBar that they have rejected a core permission for the
                 app, which makes the Activity useless. In a real app, core permissions would
                 typically be best requested during a welcome-screen flow.

                 Additionally, it is important to remember that a permission might have been
                 rejected without asking the user for permission (device policy or "Never ask
                 again" prompts). Therefore, a user interface affordance is typically implemented
                 when permissions are denied. Otherwise, your app could appear unresponsive to
                 touches or interactions which have required permissions.
                 */
                Snackbar.make(
                        findViewById(R.id.fab),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.fab),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Handles the Request Updates button and requests start of location updates.
     */
    public void requestLocationUpdates() {
        try {
            Log.i(TAG, "Starting location updates");
            LocationRequestHelper.setRequesting(this, true);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, getPendingIntent());
            //Save to firebase
            //Create a new trip
            firebaseDataManager.createTrip(new MTrip());
        } catch (SecurityException e) {
            LocationRequestHelper.setRequesting(this, false);
            e.printStackTrace();
        }
    }

    /**
     * Handles the Remove Updates button, and requests removal of location updates.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        LocationRequestHelper.setRequesting(this, false);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                getPendingIntent());
        //End the current trip
        String tripId = LocalDataStorageManager.getInstance().currentTripId();
        if (!tripId.equals(""))
            firebaseDataManager.endCurrentTrip(tripId);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */
    private void updateButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            //mRequestUpdatesButton.setEnabled(false);
            //mRemoveUpdatesButton.setEnabled(true);
            switchTripStatus.setChecked(true);
        } else {
            //mRequestUpdatesButton.setEnabled(true);
            //mRemoveUpdatesButton.setEnabled(false);
            switchTripStatus.setChecked(false);
        }
    }

    /**
     * Called when an item in the bottom navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item and false if the item should not
     * be selected. Consider setting non-selectable items as disabled preemptively to
     * make them appear non-interactive.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                currentFragment = item.getItemId();
                loadInitialFragment();
                break;
            case R.id.action_timeline:
                currentFragment = item.getItemId();
                loadFragment(TripTimelineFragment.getInstance());
                break;
            /*case R.id.action_camera:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.action_map:
                currentFragment = item.getItemId();
                loadFragment(new MapFragment());
                break;
            case R.id.action_settings:
                break;*/
        }
        return true;
    }

    private void loadInitialFragment() {
        if (LocationRequestHelper.getRequesting(getApplicationContext())){
            loadFragment(TrackingFragment.getInstance(LocalDataStorageManager.getInstance().currentTripId()));
        }else {
            loadFragment(HomeFragment.getInstance());
        }
    }

    private void loadFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
