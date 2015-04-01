package com.example.motiondetection.app;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by Bogdan on 11/02/2015.
 * Clasa ajutatoare pentru lucrul cu senzorii
 * Se pot crea diferite functii ajutatoare pe post de delegate (adica este o clasa "muncitoare")
 */
public class HelperSensorManager {
    private SensorManager mySensorManager;
    private Context myContext;

    public HelperSensorManager(Context context) {
        this.myContext = context;
        this.mySensorManager = (SensorManager) this.myContext.getSystemService(Context.SENSOR_SERVICE);
    }

    public List<Sensor> getSensorList()
    {
        return this.mySensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    public boolean isSensorAvailable(int sensorType){
        if (this.mySensorManager.getDefaultSensor(sensorType) != null)
            return true;
        return false;
    }

    public void wakeSensor(int sensorType){
        if (isSensorAvailable(sensorType))
            this.mySensorManager.getDefaultSensor(sensorType);
    }

    public Sensor getSensor(int sensorType)
    {
        return mySensorManager.getDefaultSensor(sensorType);
    }
}