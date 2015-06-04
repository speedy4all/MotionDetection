package com.example.motiondetection.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;


public class SendSMSActivity extends ActionBarActivity {
    private String _phoneNumber;
    private String _message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = getIntent();
        _phoneNumber = intent.getStringExtra("phone");
        _message = intent.getStringExtra("message");
    }

    @Override
    protected void onStart() {
        new AlertDialog.Builder(SendSMSActivity.this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.ask_for_sms)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(_phoneNumber, null, _message, null, null);
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
