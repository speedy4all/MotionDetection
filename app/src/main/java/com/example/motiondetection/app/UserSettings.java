package com.example.motiondetection.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    //region set
    public void setServicePreference(boolean args)
    {
        sharedPreferences.edit().putBoolean(SERVICE_KEY, args).commit();
    }
    public void setTypeOfActionPreference(String type)
    {
        sharedPreferences.edit().putString(TYPE_OF_ACTION, type).commit();
    }
    public void setEmergencyNumberPreference(String number)
    {
        sharedPreferences.edit().putString(EMERGENCY_NUMBER, number).commit();
    }
    public void setEmergencyNamePreference(String name)
    {
        sharedPreferences.edit().putString(EMERGENCY_NAME, name).commit();
    }
    //endregion
    //region get
    public boolean getServicePreference()
    {
        if (sharedPreferences.contains(SERVICE_KEY))
        {
            return sharedPreferences.getBoolean(SERVICE_KEY, false);
        }
        else
            return false;
    }

    public String getTypeOfActionPreference()
    {
        if (sharedPreferences.contains(TYPE_OF_ACTION))
        {
            return sharedPreferences.getString(TYPE_OF_ACTION, "");
        }
        else
            return "";
    }

    public  String getEmergencyNumberPreference()
    {
        if (sharedPreferences.contains(EMERGENCY_NUMBER))
        {
            return sharedPreferences.getString(EMERGENCY_NUMBER, "");
        }
        else
            return "";
    }

    public  String getEmergencyNamePreference()
    {
        if (sharedPreferences.contains(EMERGENCY_NAME))
        {
            return sharedPreferences.getString(EMERGENCY_NAME, "");
        }
        else
            return "";
    }
    //endregion

}
