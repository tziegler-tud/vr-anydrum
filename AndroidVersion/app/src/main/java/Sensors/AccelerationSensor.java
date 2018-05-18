package Sensors;

import Learning.ClusterableFloat;
import Visualizations.LineChart;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.widget.TextView;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;

import java.text.DecimalFormat;

import  org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import  org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Vector;


public class AccelerationSensor
{
    DecimalFormat formatter;
    private DBSCANClusterer clusterer;
    private StandardDeviation stdDeviation;
    private double stdDeviationValue=0;
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
        formatter  = new DecimalFormat("#,###,###.###");
        stdDeviation = new StandardDeviation(false);
        //clusterer = new DBSCANClusterer(1, 15)
        this.updatePeriod = period;
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
                TextView viewCurrentValue = (TextView) refMain.findViewById(R.id.sensorValue);
                TextView viewDeviation = (TextView) refMain.findViewById(R.id.viewStdDeviation);
                TextView viewCount = (TextView) refMain.findViewById(R.id.viewSensorValuesCount);

                viewCurrentValue.setText("Acl_Z: "+formatter.format(sensorEvent.values[2]));
                viewCount.setText("SensorList: "+listOfSensorData.size());
                double x = listOfSensorData.get(listOfSensorData.size()-1).getX()+stepX;
                /*if(listOfSensorData.size() >= 100)
                {
                    listOfSensorData.clear();
                    x= 0;
                }*/
                if(listOfSensorData.size()==100)
                {
                    double[] skalars = new double[100];
                    for(int i=49; i<100;++i)
                        skalars[i]=listOfSensorData.get(i).getY();
                    stdDeviationValue=stdDeviation.evaluate(skalars);
                    viewDeviation.setText("deviation: "+formatter.format(stdDeviationValue));
                }
                listOfSensorData.add(new DataPoint(x, sensorEvent.values[2]-stdDeviationValue));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        refMain.getmSensorManager().registerListener(acclerationSensorListener, mSensor, updatePeriod);
    }
}
