package com.example.motiondetection.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.hardware.Sensor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class HomeActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>,
        com.google.android.gms.location.LocationListener {

    //Google location init
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private static final long UPDATE_INTERVAL = 30000;
    private static final long FAST_UPDATE_INTERVAL = 5000;

    public static String ADDRESS = "";

    protected GoogleApiClient googleApiClient;
    Context context;
    Button btnStartService;
    Button btnStopService;
    Button btnLocation;
    Location newLocation = null;
    LocationRequest locationRequest;
    private boolean localizationService = false;
    private boolean mResolvingError = false;
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        btnStartService = (Button)findViewById(R.id.btnStartService);
        btnStopService = (Button)findViewById(R.id.btnStopService);
        btnLocation = (Button) findViewById(R.id.buttonLocation);
        btnLocation.setVisibility(View.GONE);

        context = this;
        geocoder = new Geocoder(context, Locale.getDefault());
        buildGoogleApiClient();
        createLocationRequest();
        addButtonClickListner();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    //Verificare Play Services
    private boolean servicesConnected() {
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, HomeActivity.this, 1).show();
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (servicesConnected())
            googleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        stopLocationUpdates();
        googleApiClient.disconnect();
        super.onDestroy();
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

        //Buton pt afisarea coordonatelor GPS

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newLocation != null) {
                    List<Address> addresses;
                    String text = "";
                    try {
                        addresses = geocoder.getFromLocation(newLocation.getLatitude(), newLocation.getLongitude(), 1);
                        if (!addresses.isEmpty())
                            text = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        text = "No valid address...";
                    }
                    Toast.makeText(getApplicationContext(), text,
                            Toast.LENGTH_LONG).show();
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
                if (WorkService.isServiceRunning(context, WorkService.class)) {
                    EnableStartBtn();
                    stopService();
                }
            }
        });
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        btnLocation.setVisibility(View.VISIBLE);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        newLocation = location;
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(newLocation.getLatitude(), newLocation.getLongitude(), 1);
            if (!addresses.isEmpty())
                ADDRESS = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            ADDRESS = "No valid address...";
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!googleApiClient.isConnecting() &&
                        !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            localizationService = !localizationService;

            Toast.makeText(
                    getApplicationContext(),
                    (localizationService ? getString(R.string.location_enabled) :
                            getString(R.string.location_disabled)),
                    Toast.LENGTH_LONG
            ).show();
        } else {
            String errorMessage = GoogleClientErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void EnableStartBtn() {
        btnStartService.setEnabled(true);
        btnStopService.setEnabled(false);
    }

    private void EnableStopBtn() {
        btnStartService.setEnabled(false);
        btnStopService.setEnabled(true);
    }

    private void startService() {
        startService(new Intent(getBaseContext(), WorkService.class));
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((HomeActivity) getActivity()).onDialogDismissed();
        }
    }
}