package com.example.motiondetection.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class SensorDetection implements SensorEventListener {
    public static String NORMAL_STATE = "NORMAL";
    public static String SITTING_STATE = "SITTING";
    public static String WALKING_STATE = "WALKING";
    public static String STANDING_STATE = "STANDING";
    public static int FINAL_STATE = 4;
    public static int FALLING_THRESHOLD = 18;
    public static boolean FALLING = false;
    public static String CURRENT_STATE = "none", PREVIEW_STATE = "none";
    static int BUFF_SIZE = 50;
    static public double[] sampleData = new double[BUFF_SIZE];
    public double ax, ay, az;
    public double accelerationVector;
    double sigmaFilter = 0.5, thresholdHigh = 10, thresholdLow = 5, thresholdMiddle = 2;
    private SensorManager sensorManager;

    public SensorDetection(Context context) {
        for (int i = 0; i < BUFF_SIZE; i++) {
            sampleData[i] = 0;
        }
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
            AddData(ax, ay, az);
            ActivityRecognition(sampleData, ay);
            DetectFall();
            SystemState(CURRENT_STATE, PREVIEW_STATE);
            if (!PREVIEW_STATE.equalsIgnoreCase(CURRENT_STATE)) {
                PREVIEW_STATE = CURRENT_STATE;
            }
        }
    }

    public void Stop() {
        sensorManager.unregisterListener(this);
    }

    private void SystemState(String curr_state, String prev_state) {

        //Fall !!
        if (!prev_state.equalsIgnoreCase(curr_state)) {
            if (FALLING && (CURRENT_STATE.equalsIgnoreCase(SITTING_STATE) || CURRENT_STATE.equalsIgnoreCase(NORMAL_STATE))) {
                FINAL_STATE = 0;
                //Log.e(FALLING_STATE, FALLING_STATE);
            }
            if (curr_state.equalsIgnoreCase(SITTING_STATE)) {
                FINAL_STATE = 1;
                // Log.e(SITTING_STATE, SITTING_STATE);
            }
            if (curr_state.equalsIgnoreCase(STANDING_STATE)) {
                FINAL_STATE = 2;
                // Log.e(STANDING_STATE, STANDING_STATE);
            }
            if (curr_state.equalsIgnoreCase(WALKING_STATE)) {
                //Log.e(WALKING_STATE, WALKING_STATE);
                FINAL_STATE = 3;
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private int ComputeZeroCrossingRate(double[] samplingData) {

        int count = 0;
        for (int i = 1; i <= BUFF_SIZE - 1; i++) {
            if ((samplingData[i] - thresholdHigh) < sigmaFilter && (samplingData[i - 1] - thresholdHigh) > sigmaFilter)
                count++;
        }
        //Log.i("ZeroCrossingRate", "Val: "+count);
        return count;
    }

    private void AddData(double ax2, double ay2, double az2) {

        accelerationVector = Math.sqrt(ax2 * ax2 + ay2 * ay2 + az2 * az2);
        for (int i = 0; i <= BUFF_SIZE - 2; i++) {
            sampleData[i] = sampleData[i + 1];
        }
        sampleData[BUFF_SIZE - 1] = accelerationVector;
        //Log.i("Acceleration", "Value: "+accelerationVector);

    }

    private void DetectFall() {
        int min = Integer.MAX_VALUE, max = 0, maxIndex = 0, minIndex = 0;
        for (int i = 1; i < sampleData.length; i++) {
            if (sampleData[i] - sampleData[i - 1] > max) {
                max = (int) sampleData[i] - (int) sampleData[i - 1];
                maxIndex = i;
            }
            if (sampleData[i] - sampleData[i - 1] < min) {
                min = (int) sampleData[i] - (int) sampleData[i - 1];
                minIndex = i;
            }
        }
        if (Math.abs(maxIndex - minIndex) == 1) {
            if (max - min > FALLING_THRESHOLD)
                FALLING = true;
            //Log.e("DetectFall", "Value max-min: " + (max - min) + " IndexVal: " + (maxIndex - minIndex));
        }

    }

    private void ActivityRecognition(double[] sample, double ay2) {

        int zeroCrossingRate = ComputeZeroCrossingRate(sample);
        if (zeroCrossingRate == 0) {
            // Log.e("AY2", "Val: "+ Math.abs(ay2));
            if (Math.abs(ay2) < thresholdLow) {
                CURRENT_STATE = SITTING_STATE;
            } else {
                CURRENT_STATE = STANDING_STATE;
            }

        } else if (zeroCrossingRate > thresholdMiddle) {
            CURRENT_STATE = WALKING_STATE;
        } else {
            CURRENT_STATE = NORMAL_STATE;
        }
    }
}
