package com.example.motiondetection.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


    public class WorkService extends Service {


    public static int CURRENT_STATE = 0;
    public static String SITTING_STATE = "SITTING";
    public static String WALKING_STATE = "WALKING";
    public static String FALLING_STATE = "FALLING";
    public static String STANDING_STATE = "STANDING";
    public static int FALLING;
    public static int SITTING ;
    public static int STANDING;
    public static int WALKING ;
        private static boolean RUNNING = false;
        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                RUNNING = true;
                try {
                    if (sensorActivity.FINAL_STATE != CURRENT_STATE) {
                        if (sensorActivity.FINAL_STATE == 0) {
                            CURRENT_STATE = 0;
                            showActivityNotification(FALLING_STATE);
                            soundPool.play(FALLING, 1f, 1f, 1, 0, 1f);
                            sensorActivity.FALLING = false;
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

                } catch (Exception e) {
                }

                mHandler.postDelayed(this, 2500);
            }
        };
        private static SoundPool soundPool;
        private final Handler mHandler = new Handler();
        private NotificationManager notificationManager;
        private int NOTIFICATION = 1;
    private SensorDetection sensorActivity;

        public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
            return RUNNING;
    }

        @Override
        public void onCreate() {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            sensorActivity = new SensorDetection(this);
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);

            FALLING = soundPool.load(this, R.raw.falling, 1);
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
            notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
            // Send the notification.
            notificationManager.notify(NOTIFICATION, notification);
        }

        private void showNotification() {
            // In this sample, we'll use the same text for the ticker and the expanded notification
            CharSequence text = getText(R.string.app_name) + " Service started";

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
            mHandler.postDelayed(refresh, 500);
            Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show();
            return START_STICKY;
        }

        @Override
        public void onDestroy() {
            RUNNING = false;
            mHandler.removeCallbacks(refresh);
            sensorActivity.Stop();
            sensorActivity = null;
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
            super.onDestroy();
        }

}

