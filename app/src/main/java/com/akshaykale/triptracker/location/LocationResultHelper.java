
package com.akshaykale.triptracker.location;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;

import com.akshaykale.triptracker.utils.FirebaseDataManager;
import com.akshaykale.triptracker.utils.LocalDataStorageManager;
import com.akshaykale.triptracker.model.MTripPoint;
import com.akshaykale.triptracker.MainActivity;
import com.akshaykale.triptracker.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Class to process location results.
 */
public class LocationResultHelper {

    public final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";

    final private static String PRIMARY_CHANNEL = "default";


    private Context mContext;
    private List<Location> mLocations;
    private NotificationManager mNotificationManager;

    LocationResultHelper(Context context, List<Location> locations) {
        mContext = context;
        mLocations = locations;

        /*NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL,
                context.getString(R.string.default_channel), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getNotificationManager().createNotificationChannel(channel);*/
    }

    /**
     * Returns the title for reporting about a list of {@link Location} objects.
     */
    private String getLocationResultTitle() {
        String numLocationsReported = mContext.getResources().getQuantityString(
                R.plurals.num_locations_reported, mLocations.size(), mLocations.size());
        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(new Date());
    }

    private String getLocationResultText() {
        if (mLocations.isEmpty()) {
            return mContext.getString(R.string.unknown_location);
        }
        FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
        StringBuilder sb = new StringBuilder();
        for (Location location : mLocations) {
            sb.append("(");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append(")");
            sb.append("\n");

            String tid = LocalDataStorageManager.getInstance().currentTripId();
            if (tid.equals(""))
                return tid;
            //save to firebase
            MTripPoint mTripPoint = new MTripPoint(location.getLatitude(),location.getLongitude());
            mTripPoint.speed = location.getSpeed();
            mTripPoint.accuracy = location.getAccuracy();
            mTripPoint.altitude = location.getAltitude();
            firebaseDataManager.pushTripPoint(tid,mTripPoint);
        }
        return sb.toString();
    }

    /**
     * Saves location result as a string to {@link android.content.SharedPreferences}.
     */
    void saveResults() {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle() + "\n" +
                        getLocationResultText())
                .apply();

    }

    /**
     * Fetches location results from {@link android.content.SharedPreferences}.
     */
    public static String getSavedLocationResult(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LOCATION_UPDATES_RESULT, "");
    }

    /**
     * Get the notification mNotificationManager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    /**
     * Displays a notification with the location results.
     */
    void showNotification() {
        Intent notificationIntent = new Intent(mContext, MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        /*Notification.Builder notificationBuilder = new Notification.Builder(mContext,
                PRIMARY_CHANNEL)
                .setContentTitle(getLocationResultTitle())
                .setContentText(getLocationResultText())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent);

        getNotificationManager().notify(0, notificationBuilder.build());*/
    }
}
