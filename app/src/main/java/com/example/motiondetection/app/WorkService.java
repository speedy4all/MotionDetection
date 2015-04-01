package com.example.motiondetection.app;

import android.app.ActivityManager;
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

    public static int CURRENT_STATE = 0;
    public static String SITTING_STATE = "SITTING";
    public static String WALKING_STATE = "WALKING";
    public static String FALLING_STATE = "FALLING";
    public static String STANDING_STATE = "STANDING";

    public static int FALLING;
    public static int SITTING ;
    public static int STANDING;
    public static int WALKING ;
    private Context context;

    private SensorDetection sensorActivity;
    @Override
    public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        sensorActivity = new SensorDetection(this);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap();
        context = this;
        FALLING =  soundPool.load(this, R.raw.falling, 1);
        SITTING = soundPool.load(this, R.raw.sitting, 2);
        STANDING = soundPool.load(this, R.raw.standing, 3);
        WALKING = soundPool.load(this, R.raw.walking, 4);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }
    private void showActivityNotification(String activity) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
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
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL ;
        // Send the notification.
        notificationManager.notify(NOTIFICATION, notification);
    }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.app_name)+ " Service started";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

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
        // Let it continue running until it is stopped.
        new Thread(new Runnable() {
            @Override
            public void run() {


                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                while(true) {
                    try {
                        if(sensorActivity.FINAL_STATE != CURRENT_STATE) {
                            if (sensorActivity.FINAL_STATE == 0) {
                                CURRENT_STATE = 0;
                                showActivityNotification(FALLING_STATE);
                                soundPool.play(FALLING, 1f, 1f, 1, 0, 1f);
                            } else if (sensorActivity.FINAL_STATE == 3) {
                                CURRENT_STATE = 3;
                                showActivityNotification(WALKING_STATE);
                                soundPool.play(WALKING, 1f, 1f, 1, 0, 1f);
                            } else if (sensorActivity.FINAL_STATE == 1) {
                                CURRENT_STATE = 1;
                                showActivityNotification(SITTING_STATE);
                                soundPool.play(SITTING, 1f, 1f, 1, 0, 1f);
                            } else if (sensorActivity.FINAL_STATE == 2) {
                                CURRENT_STATE = 2;
                                showActivityNotification(STANDING_STATE);
                                soundPool.play(STANDING, 1f, 1f, 1, 0, 1f);
                            }
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
        sensorActivity.Stop();
        sensorActivity = null;
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }

}
