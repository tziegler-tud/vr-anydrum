package Sensors;

import Visualizations.LineChart;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.widget.TextView;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Vector;


public class AccelerationSensor
{
    private Sensor mSensor;
    private MainActivity refMain;
    private int updatePeriod = 2 * 1000 * 1000;
    private double stepX = 1;
    private Vector<DataPoint> listOfSensorData;
    public Vector<DataPoint> getListOfSensorData() {
        return listOfSensorData;
    }

    public AccelerationSensor(MainActivity refMain, int period)
    {
        this.refMain = refMain;
        listOfSensorData = new Vector<DataPoint>();
        listOfSensorData.add(new DataPoint(0.0d, 0.0d));
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
                double x = listOfSensorData.get(listOfSensorData.size()-1).getX()+stepX;
                /*if(listOfSensorData.size() >= 100)
                {
                    listOfSensorData.clear();
                    x= 0;
                }*/
                listOfSensorData.add(new DataPoint(x, sensorEvent.values[2]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        refMain.getmSensorManager().registerListener(acclerationSensorListener, mSensor, updatePeriod);
    }
}
