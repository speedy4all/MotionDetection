package com.example.motiondetection.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;


public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addButtonClickListner();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addButtonClickListner()
    {
        Button btnNavigator1 = (Button)findViewById(R.id.btnSensorLayout);
        btnNavigator1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SensorActivity.class));
            }
        });

        Button btnNavigator2 = (Button)findViewById(R.id.button2);
        btnNavigator2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSettings userSettings = new UserSettings(HomeActivity.this);
                if (!userSettings.getEmergencyNumberPreference().isEmpty())
                {
                    String phNum = "tel:" + userSettings.getEmergencyNumberPreference();
                    Intent myIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phNum));
                    startActivity( myIntent ) ;
                }

            }
        });

        Button btnNavigator3 = (Button)findViewById(R.id.button3);
        btnNavigator3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phNum = "tel:" + "112";
                    Intent myIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phNum));
                    startActivity( myIntent ) ;
            }
        });

        Button btnGetAllSensors = (Button) findViewById(R.id.btnGetAllSensors);

        //Metoda ce afiseaza toti senzorii disponibili ai dispozitivului
        btnGetAllSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperSensorManager hSensors = new HelperSensorManager(getApplicationContext());
                List<Sensor> sensorList = hSensors.getSensorList();
                StringBuilder strBuild = new StringBuilder();
                for (Sensor s : sensorList)
                {
                    strBuild.append(s.getName()+ "\n");
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Sensor List")
                        .setMessage(strBuild)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });
    }

}