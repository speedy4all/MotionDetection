package com.example.motiondetection.app;

import android.app.AlertDialog;
import android.content.Context;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import java.util.List;


public class HomeActivity extends ActionBarActivity {

    Context context;
    Button btnStartService;
    Button btnStopService;
    Location newLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStopService = (Button) findViewById(R.id.btnStopService);
        context = this;
        addButtonClickListner();
    }


    public void stopService() {
        stopService(new Intent(getBaseContext(), WorkService.class));
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

    public void addButtonClickListner() {
        if (!WorkService.isServiceRunning(context, WorkService.class)) {
            EnableStopBtn();
            startService();
        } else {
    public void addButtonClickListner()
    {
        if (!WorkService.isServiceRunning(context, WorkService.class))
        {
            EnableStartBtn();
        }
        else
        {
            EnableStopBtn();
        }

        Button btnNavigator1 = (Button) findViewById(R.id.btnSensorLayout);
        btnNavigator1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SensorActivity.class));
            }
        });

        Button btnNavigator2 = (Button) findViewById(R.id.button2);
        btnNavigator2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSettings userSettings = new UserSettings(HomeActivity.this);
                if (!userSettings.getEmergencyNumberPreference().isEmpty()) {
                    String phNum = "tel:" + userSettings.getEmergencyNumberPreference();
                    Intent myIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phNum));
                    startActivity(myIntent);
                }

            }
        });

        Button btnNavigator3 = (Button) findViewById(R.id.button3);
        btnNavigator3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phNum = "tel:" + "112";
                Intent myIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phNum));
                startActivity(myIntent);
            }
        });

        //Buton pt afisarea coordonatelor GPS
        Button btnLocation = (Button) findViewById(R.id.buttonLocation);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newLocation != null) {

                    String Text = "Current location is: " + "Latitud = "
                            + newLocation.getLatitude() + "Longitud = "
                            + newLocation.getLongitude();
                    Toast.makeText(getApplicationContext(), Text,
                            Toast.LENGTH_SHORT).show();
                }
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
                for (Sensor s : sensorList) {
                    strBuild.append(s.getName() + "\n");
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

        // Capturez locatia - nu te supara daca nu am pus-o bine  :D
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!WorkService.isServiceRunning(context, WorkService.class))
                {
                    EnableStopBtn();
                    startService();
                }
            }
        });
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WorkService.isServiceRunning(context, WorkService.class))
                {
                    EnableStartBtn();
                    stopService();
                }
            }
        });
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location loc) {
            newLocation = loc;
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private void EnableStartBtn(){
        btnStartService.setEnabled(true);
        btnStopService.setEnabled(false);
    }
    private void EnableStopBtn(){
        btnStartService.setEnabled(false);
        btnStopService.setEnabled(true);
    }
    private void startService() {
        startService(new Intent(getBaseContext(), WorkService.class));
    }
}