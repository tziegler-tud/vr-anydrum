package Sensors;

import android.app.Application;
import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.widget.TextView;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;


public class AccelerationSensor
{
    private Sensor mSensor;
    MainActivity refMain;
    private int updatePeriod = 2 * 1000 * 1000;

    public AccelerationSensor(MainActivity refMain, int period)
    {
        this.refMain = refMain;
        mSensor = refMain.getmSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(mSensor == null)
            System.out.println( "ACCELEROMETER not available");
    }

    public void subscribeToAccelerationSensor()
    {
        // Create listener
        SensorEventListener acclerationSensorListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // More code goes here
                System.out.println("zAcl: "+ sensorEvent.values[2]);
                TextView tv = (TextView) refMain.findViewById(R.id.sample_text);
                tv.setText("Acl_Z: "+sensorEvent.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        refMain.getmSensorManager().registerListener(acclerationSensorListener, mSensor, updatePeriod);
    }
}
