package com.akshaykale.triptracker.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.akshaykale.triptracker.R;
import com.akshaykale.triptracker.fragments.CreateNewTripFragment;
import com.akshaykale.triptracker.fragments.HomeFragment;
import com.akshaykale.triptracker.fragments.TripTimelineFragment;
import com.akshaykale.triptracker.utils.ui.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by akshay.kale on 27/10/2017.
 */

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar_home)
    Toolbar toolbar;
    @BindView(R.id.bottom_nav_menu_home)
    BottomNavigationViewEx bottomNavigationView;

    private int currentFragment = R.id.action_home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //Bottom menu
        bottomNavigationView.enableAnimation(true);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.setTextVisibility(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

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
                loadFragment(HomeFragment.getInstance());
                break;
            case R.id.action_create_trip:
                currentFragment = item.getItemId();
                loadFragment(CreateNewTripFragment.getInstance());
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void loadFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_home, newFragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
