package com.example.motiondetection.app;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;


public class SensorActivity extends ActionBarActivity implements SensorEventListener{

    //Declararea variabilelor UI
    private TextView tvAccXValue;
    private TextView tvAccYValue;
    private TextView tvAccZValue;

    private TextView tvAccXValueMax;
    private TextView tvAccYValueMax;
    private TextView tvAccZValueMax;

    private TextView tvGraXValue;
    private TextView tvGraYValue;
    private TextView tvGraZValue;

    private TextView tvGraXValueMax;
    private TextView tvGraYValueMax;
    private TextView tvGraZValueMax;

    //Declararea variabilelor de lucru
    private HelperSensorManager sensorManager;
    private SensorManager activitySensorManager;
    private double accXMax, accYMax, accZMax, graXMax, graYMax, graZMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_monitor);
        //Flag pentru mentinerea ecranului activ
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Initializare UI
        initView();
        //Initializare senzori si afisare valori
        initSensorAndUpdateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.sensor_settings || super.onOptionsItemSelected(item);
    }
    //Metoda initializare elemente UI
    private void initView()
    {
        tvAccXValue = (TextView)findViewById(R.id.txtAccXAxis);
        tvAccXValueMax = (TextView)findViewById(R.id.txtAccXAxisMax);
        tvAccYValue = (TextView)findViewById(R.id.txtAccYAxis);
        tvAccYValueMax = (TextView)findViewById(R.id.txtAccYAxisMax);
        tvAccZValue = (TextView)findViewById(R.id.txtAccZAxis);
        tvAccZValueMax = (TextView)findViewById(R.id.txtAccZAxisMax);

        tvGraXValue = (TextView)findViewById(R.id.txtGraXAxis);
        tvGraXValueMax = (TextView)findViewById(R.id.txtGraXAxisMax);
        tvGraYValue = (TextView)findViewById(R.id.txtGraYAxis);
        tvGraYValueMax = (TextView)findViewById(R.id.txtGraYAxisMax);
        tvGraZValue = (TextView)findViewById(R.id.txtGraZAxis);
        tvGraZValueMax = (TextView)findViewById(R.id.txtGraZAxisMax);

    }
    //Metoda initializare senzori si inregistrare serviciu
    private void initSensorAndUpdateData(){
        sensorManager = new HelperSensorManager(SensorActivity.this);

        if(sensorManager.isSensorAvailable(Sensor.TYPE_LINEAR_ACCELERATION))
            sensorManager.wakeSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(sensorManager.isSensorAvailable(Sensor.TYPE_GRAVITY))
            sensorManager.wakeSensor(Sensor.TYPE_GRAVITY);
        activitySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        activitySensorManager.registerListener(this, activitySensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
        activitySensorManager.registerListener(this, activitySensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onResume() {
        activitySensorManager.registerListener(this, activitySensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
        activitySensorManager.registerListener(this, activitySensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }

    @Override
    protected void onPause() {
        activitySensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                updateAccValues(event.values[0], event.values[1], event.values[2]);
                break;
            case Sensor.TYPE_GRAVITY:
                updateGraValues(event.values[0], event.values[1], event.values[2]);
                break;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //Metoda calcul si afisare date accelerometru
    private void updateAccValues(double ax, double ay, double az)
    {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = (float) 0.5;


        // Isolate the force of gravity with the low-pass filter.

        float[] gravity = new float[3];
        gravity[0] = (float) (alpha * gravity[0] + (1 - alpha) * ax);
        gravity[1] = (float) (alpha * gravity[1] + (1 - alpha) * ay);
        gravity[2] = (float) (alpha * gravity[2] + (1 - alpha) * az);

        // Remove the gravity contribution with the high-pass filter.
        float[] linear_acceleration = new float[3];
        linear_acceleration[0] = (float) (ax - gravity[0]);
        linear_acceleration[1] = (float) (ay - gravity[1]);
        linear_acceleration[2] = (float) (az - gravity[2]);

        tvAccXValue.setText(getResources().getString(R.string.sensor_txt_acc_x_axis, linear_acceleration[0]));
        tvAccYValue.setText(getResources().getString(R.string.sensor_txt_acc_y_axis, linear_acceleration[1]));
        tvAccZValue.setText(getResources().getString(R.string.sensor_txt_acc_z_axis, linear_acceleration[2]));

        if(Math.abs(linear_acceleration[0]) > accXMax)
        {
            tvAccXValueMax.setText(getResources().getString(R.string.sensor_txt_acc_x_axis_max, linear_acceleration[0]));
            accXMax = linear_acceleration[0];
        }
        if(Math.abs(linear_acceleration[1]) > accYMax)
        {
            tvAccYValueMax.setText(getResources().getString(R.string.sensor_txt_acc_y_axis_max, linear_acceleration[1]));
            accYMax = linear_acceleration[1];
        }
        if(Math.abs(linear_acceleration[2]) > accZMax)
        {
            tvAccZValueMax.setText(getResources().getString(R.string.sensor_txt_acc_z_axis_max, linear_acceleration[2]));
            accZMax = linear_acceleration[2];
        }
    }
    //Metoda calcul si afisare date senzor gravitational
    private void updateGraValues(double ax, double ay, double az)
    {
        tvGraXValue.setText(getResources().getString(R.string.sensor_txt_gra_x_axis, ax));
        tvGraYValue.setText(getResources().getString(R.string.sensor_txt_gra_y_axis, ay));
        tvGraZValue.setText(getResources().getString(R.string.sensor_txt_gra_z_axis, az));

        if(Math.abs(ax) > graXMax)
        {
            tvGraXValueMax.setText(getResources().getString(R.string.sensor_txt_gra_x_axis_max, ax));
            graXMax = ax;
        }
        if(Math.abs(ay) > graYMax)
        {
            tvGraYValueMax.setText(getResources().getString(R.string.sensor_txt_gra_y_axis_max, ay));
            graYMax = ay;
        }
        if(Math.abs(az) > graZMax)
        {
            tvGraZValueMax.setText(getResources().getString(R.string.sensor_txt_gra_z_axis_max, az));
            graZMax = az;
        }

    }
}
