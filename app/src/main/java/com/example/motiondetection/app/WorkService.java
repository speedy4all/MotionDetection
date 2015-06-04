package com.example.motiondetection.app;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class WorkService extends Service implements GoogleApiClient.ConnectionCallbacks,
        LocationListener, GoogleApiClient.OnConnectionFailedListener {

    //Google location init
    private static final long UPDATE_INTERVAL = 120000;
    private static final long FAST_UPDATE_INTERVAL = 60000;
    public static int CURRENT_STATE = 0;
    public static String SITTING_STATE = "SITTING";
    public static String WALKING_STATE = "WALKING";
    public static String FALLING_STATE = "FALLING";
    public static String STANDING_STATE = "STANDING";
    public static int FALLING;
    public static int SITTING;
    public static int STANDING;
    public static int WALKING;
    public static boolean CONTINUE = true;
    private static String ADDRESS = "";
    private static SoundPool soundPool;
    private final Handler mHandler = new Handler();
    protected GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    private boolean localizationService = false;
    private boolean mResolvingError = false;
    private Geocoder geocoder;
    private NotificationManager notificationManager;
    private int NOTIFICATION = 1;
    private UserSettings userSettings;
    private SensorDetection sensorActivity;
    Runnable refresh = new Runnable() {
        @Override
        public void run() {
            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting())
                googleApiClient.connect();
            if (CONTINUE) {
                try {

                    if (sensorActivity.FINAL_STATE == 0) {
                        CURRENT_STATE = 0;
                        if (userSettings.getEmergencyNumberPreference().length() > 2) {
                            if (String.valueOf(R.string.type_sms).contentEquals(userSettings.getTypeOfActionPreference())) {
                                ShowSMSNotification(userSettings.getEmergencyNumberPreference(), "Am nevoie de ajutor !  " +
                                        "Ma aflu la adresa: " + ADDRESS + "");
                                CONTINUE = false;
                            } else {
                                if (userSettings.getTypeOfActionPreference().contentEquals(String.valueOf(R.string.type_call))) {
                                    ShowCallNotification();
                                    CONTINUE = false;
                                } else {
                                    if (userSettings.getTypeOfActionPreference().contentEquals(String.valueOf(R.string.type_both))) {
                                        ShowBothNotification("Am nevoie de ajutor !  " +
                                                "Ma aflu la adresa: " + ADDRESS + "");
                                        CONTINUE = false;
                                    }
                                }
                            }

                        }

                        soundPool.play(FALLING, 1f, 1f, 1, 0, 1f);
                        sensorActivity.FALLING = false;

                    } else if (sensorActivity.FINAL_STATE != CURRENT_STATE) {
                        if (sensorActivity.FINAL_STATE == 3) {
                            CURRENT_STATE = 3;
                            //showActivityNotification(WALKING_STATE);
                            soundPool.play(WALKING, 1f, 1f, 1, 0, 1f);
                        } else if (sensorActivity.FINAL_STATE == 1) {
                            CURRENT_STATE = 1;
                            //showActivityNotification(SITTING_STATE);
                            soundPool.play(SITTING, 1f, 1f, 1, 0, 1f);
                        } else if (sensorActivity.FINAL_STATE == 2) {
                            CURRENT_STATE = 2;
                            //showActivityNotification(STANDING_STATE);
                            soundPool.play(STANDING, 1f, 1f, 1, 0, 1f);
                        }
                    }


                    Log.d(getString(R.string.app_name), "Service running");

                } catch (Exception e) {
                }
            }
            mHandler.postDelayed(this, 2000);
        }
    };

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {

        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;

    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sensorActivity = new SensorDetection(this);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        userSettings = new UserSettings(this);
        FALLING = soundPool.load(this, R.raw.falling, 1);
        SITTING = soundPool.load(this, R.raw.sitting, 2);
        STANDING = soundPool.load(this, R.raw.standing, 3);
        WALKING = soundPool.load(this, R.raw.walking, 4);

        SensorDetection.FALLING_THRESHOLD = 10 + userSettings.getSensorSensitivity();

        geocoder = new Geocoder(this, Locale.getDefault());
        buildGoogleApiClient();
        createLocationRequest();
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    private void ShowBothNotification(String message) {

        CharSequence text = "Click here if you need help";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());
        Intent bothIntent = new Intent(this, SMSAndCallActivity.class);
        bothIntent.putExtra("message", message);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                bothIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                text, contentIntent);
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_VIBRATE;
        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);

    }

    private void ShowSMSNotification(String phone, String message) {

        CharSequence text = "Click here if you need help";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());
        Intent sendSMSIntent = new Intent(this, SendSMSActivity.class);
        sendSMSIntent.putExtra("phone", phone);
        sendSMSIntent.putExtra("message", message);
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                sendSMSIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                text, contentIntent);
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_VIBRATE;
        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);

    }

    private void ShowCallNotification() {

        CharSequence text = "Click here if you need help";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, CallActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                text, contentIntent);
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_VIBRATE;
        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);

    }

    private void showActivityNotification(String activity) {

        CharSequence text = "We detected that you are: " + activity;

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                text, contentIntent);
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);
    }

    private void showNotification() {

        CharSequence text = getText(R.string.app_name) + " Service started";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                text, contentIntent);
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_VIBRATE;
        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.postDelayed(refresh, 500);
        Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(refresh);
        sensorActivity.Stop();
        sensorActivity = null;
        stopLocationUpdates();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty())
                ADDRESS = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            ADDRESS = "No valid address...";
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(getBaseContext(), "Google connection not yet established..." + result.toString(), Toast.LENGTH_LONG).show();
        googleApiClient.connect();
    }


}
