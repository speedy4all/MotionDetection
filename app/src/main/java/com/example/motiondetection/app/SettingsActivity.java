package com.example.motiondetection.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Bogdan on 11/02/2015.
 */
public class SettingsActivity extends ActionBarActivity{

    public static final int PICK_CONTACT = 1;
    public static int SEEK_BAR_VALUE;
    private CheckBox chkService;
    private RadioGroup rdTypeOfAction;
    private Button btnAddModPerson;
    private TextView txtSelectedNumber;
    private UserSettings userSettings;
    private SeekBar seekSensorSensitivity;
    private TextView txtSeekBarValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        chkService = (CheckBox)findViewById(R.id.chkServiceSetting);
        rdTypeOfAction = (RadioGroup)findViewById(R.id.rdGroupTypeOfAction);
        btnAddModPerson = (Button)findViewById(R.id.btnSettingsAddModPerson);
        txtSelectedNumber = (TextView)findViewById(R.id.txtSettingsCallNumber);
        txtSeekBarValue = (TextView) findViewById(R.id.txtSeekBarValue);
        seekSensorSensitivity = (SeekBar) findViewById(R.id.seekBar);
        seekSensorSensitivity.setMax(13);

        seekSensorSensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SEEK_BAR_VALUE = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userSettings.setSensorSensitivity(SEEK_BAR_VALUE);
                txtSeekBarValue.setText(getResources().getString(R.string.seekBarValue, SEEK_BAR_VALUE));
                SensorDetection.FALLING_THRESHOLD = 10 + SEEK_BAR_VALUE;
            }
        });

        userSettings = new UserSettings(this);


        setClickListener();
    }

    @Override
    protected void onStart() {

        if (userSettings.getAllKeys().isEmpty())
        {
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle(R.string.alert_no_pref_title)
                    .setMessage(R.string.alert_no_pref_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            seekSensorSensitivity.setProgress(5);
                            //trebuie adaugate valori implicite
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
        else
        {
            loadSettings();
        }
        super.onStart();
    }

    private void loadSettings()
    {
        seekSensorSensitivity.setProgress(userSettings.getSensorSensitivity());
        txtSeekBarValue.setText(getResources().getString(R.string.seekBarValue, userSettings.getSensorSensitivity()));
        if (userSettings.getServicePreference()  && !chkService.isChecked())
            chkService.toggle();
        else if (!userSettings.getServicePreference() && chkService.isChecked())
            chkService.toggle();

        if (userSettings.getTypeOfActionPreference().contentEquals(String.valueOf(R.string.type_call)))
            rdTypeOfAction.check(R.id.rdCallAction);
        else if (userSettings.getTypeOfActionPreference().contentEquals(String.valueOf(R.string.type_sms)))
            rdTypeOfAction.check(R.id.rdSmsAction);
        else if (userSettings.getTypeOfActionPreference().contentEquals(String.valueOf(R.string.type_both)))
            rdTypeOfAction.check(R.id.rdBothAction);

        if (!userSettings.getEmergencyNamePreference().isEmpty())
            txtSelectedNumber.setText(userSettings.getEmergencyNamePreference() + " " + userSettings.getEmergencyNumberPreference());
    }
    private void setClickListener()
    {
        chkService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                    userSettings.setServicePreference(true);
                else
                    userSettings.setServicePreference(false);
            }
        });
        rdTypeOfAction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rdCallAction:
                        userSettings.setTypeOfActionPreference(String.valueOf(R.string.type_call));
                        break;
                    case R.id.rdSmsAction:
                        userSettings.setTypeOfActionPreference(String.valueOf(R.string.type_sms));
                        break;
                    case R.id.rdBothAction:
                        userSettings.setTypeOfActionPreference(String.valueOf(R.string.type_both));
                        break;
                }
            }
        });
        btnAddModPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER },
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String name = c.getString(0);
                        String phone = c.getString(1);
                        userSettings.setEmergencyNamePreference(name);
                        userSettings.setEmergencyNumberPreference(phone);
                        txtSelectedNumber.setText(name + " " + phone);
                    }
                }
                break;
        }
    }
}