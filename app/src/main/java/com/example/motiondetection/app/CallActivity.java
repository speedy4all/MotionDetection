package com.example.motiondetection.app;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class CallActivity extends ActionBarActivity {
    private UserSettings userSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        userSettings = new UserSettings(this);

    }

    @Override
    protected void onStart() {
        new AlertDialog.Builder(CallActivity.this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.ask_for_call)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phNum = "tel:" + userSettings.getEmergencyNumberPreference();
                        Intent myIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phNum));
                        startActivity(myIntent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WorkService.CONTINUE = true;
                        finish();
                    }
                }).show();

        super.onStart();
    }
}
