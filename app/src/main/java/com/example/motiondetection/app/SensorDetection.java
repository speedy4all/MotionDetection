package com.example.motiondetection.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Bogdan on 24/03/2015.
 */
public class SensorDetection implements SensorEventListener {
    public static String NORMAL_STATE = "NORMAL";
    public static String SITTING_STATE = "SITTING";
    public static String WALKING_STATE = "WALKING";
    public static String FALLING_STATE = "FALLING";
    public static String STANDING_STATE = "STANDING";
    public static int FINAL_STATE = 0;
    public static String FINAL_PREVIOUS_STATE = "";
    public double ax,ay,az;
    public double accelerationVector;
    static int BUFF_SIZE = 50;
    static public double[] sampleData = new double[BUFF_SIZE];
    double sigmaFilter=0.5,thresholdHigh=10,thresholdLow=5,thresholdMiddle=2;
    private SensorManager sensorManager;
    public static String CURRENT_STATE = "none",PREVIEW_STATE = "none";

    public SensorDetection(Context context){
        for(int i=0; i<BUFF_SIZE; i++){
            sampleData[i]=0;
        }
        sensorManager=(SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
            AddData(ax,ay,az);
            ActivityRecognition(sampleData,ay);
            SystemState(CURRENT_STATE,PREVIEW_STATE);
            if(!PREVIEW_STATE.equalsIgnoreCase(CURRENT_STATE)){
                PREVIEW_STATE  = CURRENT_STATE;
            }
     }
    }
    public void Stop(){
        sensorManager.unregisterListener(this);
    }
    private void SystemState(String curr_state,String prev_state) {

        //Fall !!
        if(!prev_state.equalsIgnoreCase(curr_state)){
            if(curr_state.equalsIgnoreCase(FALLING_STATE)){
                FINAL_STATE = 0;
                //
            }
            if(curr_state.equalsIgnoreCase(SITTING_STATE)){
               FINAL_STATE = 1;
               //
            }
            if(curr_state.equalsIgnoreCase(STANDING_STATE)){
                FINAL_STATE = 2;
                //
            }
            if(curr_state.equalsIgnoreCase(WALKING_STATE)){
                FINAL_STATE = 3;
                //
            }
        }


    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private int ComputeZeroCrossingRate(double[] samplingData) {

        int count=0;
        for(int i=1;i<=BUFF_SIZE-1;i++){
            if( (samplingData[i]-thresholdHigh) < sigmaFilter && (samplingData[i-1]-thresholdHigh) > sigmaFilter)
                count++;
        }
        return count;
    }
    private void AddData(double ax2, double ay2, double az2) {

        accelerationVector = Math.sqrt( ax*ax + ay*ay + az*az );
        for(int i=0 ;i<=BUFF_SIZE-2 ;i++){
            sampleData[i] = sampleData[i+1];
        }
        sampleData[BUFF_SIZE-1] = accelerationVector;

    }
    private void ActivityRecognition(double[] sample,double ay2) {

        int zeroCrossingRate = ComputeZeroCrossingRate(sample);
        if(zeroCrossingRate == 0){

            if( Math.abs(ay2) < thresholdHigh){
                CURRENT_STATE = SITTING_STATE;
            }else{
                CURRENT_STATE = STANDING_STATE;
            }

        }else{

            if(zeroCrossingRate > thresholdMiddle ){
                CURRENT_STATE = WALKING_STATE;
            }else{
                CURRENT_STATE = NORMAL_STATE;
            }

        }



    }
}
