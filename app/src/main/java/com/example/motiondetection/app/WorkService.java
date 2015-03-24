package com.example.motiondetection.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Bogdan on 24/03/2015.
 */


    public class WorkService extends Service {

    private boolean isRunning  = false;
    private NotificationManager notificationManager;
    private int NOTIFICATION = 1;

    private static SoundPool soundPool;
    private static HashMap soundPoolMap;

    public static String NORMAL_STATE = "NORMAL";
    public static String SITTING_STATE = "SITTING";
    public static String WALKING_STATE = "WALKING";
    public static String FALLING_STATE = "FALLING";
    public static String STANDING_STATE = "STANDING";

    public static final int FALLING = R.raw.falling;
    public static final int SITTING = R.raw.sitting;
    public static final int STANDING = R.raw.standing;
    public static final int WALKING = R.raw.walking;
    private Context context;
    private SensorDetection sensorActivity;
    @Override
    public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        sensorActivity = new SensorDetection();
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap();
        context = this;
        soundPoolMap.put( FALLING, soundPool.load(this, R.raw.falling, 1) );
        soundPoolMap.put( SITTING, soundPool.load(this, R.raw.sitting, 2) );
        soundPoolMap.put( STANDING, soundPool.load(this, R.raw.standing, 3) );
        soundPoolMap.put( WALKING, soundPool.load(this, R.raw.walking, 4) );

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }
    private void showActivityNotification(String activity) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.app_name)+ " We detected that you are: " + activity;

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                text, contentIntent);

        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);
    }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.app_name)+ "Service started";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                text, contentIntent);

        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);
    }
    @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // Let it continue running until it is stopped.
            new Thread(new Runnable() {
                @Override
                public void run() {


                    //Your logic that service will perform will be placed here
                    //In this example we are just looping and waits for 1000 milliseconds in each loop.
                    while(true) {
                        try {
                            if(sensorActivity.CURRENT_STATE.equals(FALLING_STATE))
                            {
                                showActivityNotification(FALLING_STATE);
                                // zero repeats (i.e play once), and a playback rate of 1f
                                int soundId = soundPool.load(context, R.raw.falling, 1);

                                soundPool.play(Integer.parseInt(soundPoolMap.get(FALLING).toString()), 1f, 1f, 1, 0, 1f);
                            }
                            Log.d(getString(R.string.app_name), "Service running");
                            Thread.sleep(2000);
                        } catch (Exception e) {
                        }


                    }


                }
            }).start();


            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
            return START_STICKY;
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        }


}

