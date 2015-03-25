package com.example.motiondetection.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    UserSettings userSettings;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userSettings = new UserSettings(this);
        addButtonClickListner();
    }

    public void addButtonClickListner()
    {
        Button btnNavigator = (Button)findViewById(R.id.button0);
        btnNavigator.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                if ( userSettings.getServicePreference() )
                {
                    if ( !WorkService.isServiceRunning(context, WorkService.class) )
                        startService();

                }
                startActivity(new Intent(MainActivity.this, HomeActivity.class));

            }
        });
    }


    // Method to start the service
    public void startService() {
        startService(new Intent(getBaseContext(), WorkService.class));
    }

    // Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), WorkService.class));
    }
    //Metoda pentru prinderea evenimentului de back pressed si afisare alerta inchidere
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.quit_text_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
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
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
