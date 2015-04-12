package com.example.motiondetection.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Bogdan on 12/02/2015.
 */
public class UserSettings {

    //Variabile statice
    private static final String USER_SETTINGS = "UserSettings" ;
    private static final String SERVICE_KEY = "ServiceKey";
    private static final String TYPE_OF_ACTION = "TypeOfActionKey";
    private static final String EMERGENCY_NUMBER = "EmergencyNumberKey";
    private static final String EMERGENCY_NAME = "EmergencyNameKey";
    private static final String SENSOR_SENSITIVITY = "SensorSensitivity";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //Constructor ce are ca parametru contextul din care a fost apelat
    public UserSettings(Context context){
        sharedPreferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
    }

    //Metoda ce returneaza toate cheile stocate pentru setari
    public ArrayList<String> getAllKeys()
    {
        ArrayList<String> keyList = new ArrayList<String>();
        for (String s : sharedPreferences.getAll().keySet())
            keyList.add(s);
        return keyList;
    }

    //endregion
    //region get
    public int getSensorSensitivity() {
        if (sharedPreferences.contains(SENSOR_SENSITIVITY))
            return sharedPreferences.getInt(SENSOR_SENSITIVITY, 10);
        else
            return 10;
    }

    public void setSensorSensitivity(int value) {
        sharedPreferences.edit().putInt(SENSOR_SENSITIVITY, value).commit();
    }

    public boolean getServicePreference() {
        if (sharedPreferences.contains(SERVICE_KEY)) {
            return sharedPreferences.getBoolean(SERVICE_KEY, false);
        } else
            return false;
    }

    //region set
    public void setServicePreference(boolean args)
    {
        sharedPreferences.edit().putBoolean(SERVICE_KEY, args).commit();
    }

    public String getTypeOfActionPreference()
    {
        if (sharedPreferences.contains(TYPE_OF_ACTION)) {
            return sharedPreferences.getString(TYPE_OF_ACTION, "");
        } else
            return "";
    }

    public void setTypeOfActionPreference(String type)
    {
        sharedPreferences.edit().putString(TYPE_OF_ACTION, type).commit();
    }

    public String getEmergencyNumberPreference()
    {
        if (sharedPreferences.contains(EMERGENCY_NUMBER))
        {
            return sharedPreferences.getString(EMERGENCY_NUMBER, "");
        }
        else
            return "";
    }

    public void setEmergencyNumberPreference(String number)
    {
        sharedPreferences.edit().putString(EMERGENCY_NUMBER, number).commit();
    }

    public String getEmergencyNamePreference()
    {
        if (sharedPreferences.contains(EMERGENCY_NAME))
        {
            return sharedPreferences.getString(EMERGENCY_NAME, "");
        }
        else
            return "";
    }

    public void setEmergencyNamePreference(String name)
    {
        sharedPreferences.edit().putString(EMERGENCY_NAME, name).commit();
    }
    //endregion

}