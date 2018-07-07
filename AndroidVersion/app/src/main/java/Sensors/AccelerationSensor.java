package Sensors;

import Filters.FilterHelper;
import Learning.ClusterableDoublePoint;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import com.example.bluefish.anydrum.MainActivity;
import com.example.bluefish.anydrum.R;

import java.text.DecimalFormat;
import java.util.List;

import  org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.Cluster;
import  org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;


import com.jjoe64.graphview.series.DataPoint;
import org.w3c.dom.Text;

import java.util.Vector;


//public class AccelerationSensor
//{
//    private boolean startUsingClusters = false;
//    public boolean isStartUsingClusters() {
//        return startUsingClusters;
//    }
//    public void setStartUsingClusters(boolean startUsingClusters) {
//        this.startUsingClusters = startUsingClusters;
//    }
//    private DecimalFormat formatter;
//
//    private StandardDeviation stdDeviation;
//    private double stdDeviationValue=0;
//    private Sensor mSensor;
//    private MainActivity refMain;
//    private int updatePeriod =100;//microseconds
//    private double stepX = 1;
//
//    private Vector<ClusterableDoublePoint> listOfSensorDataFiltered;
//    public Vector<ClusterableDoublePoint> getListOfSensorDataFiltered() {
//        return listOfSensorDataFiltered;
//    }
//
//    private Vector<ClusterableDoublePoint> listOfSensorData;
//    public Vector<ClusterableDoublePoint> getListOfSensorData() {
//        return listOfSensorData;
//    }
//
//    private FilterHelper filter;
//
//
//
//    public AccelerationSensor(MainActivity refMain, int period)
//    {
//        filter = new FilterHelper();
//        formatter  = new DecimalFormat("#,###,###.###");
//        stdDeviation = new StandardDeviation(false);
//        this.updatePeriod = period;
//        this.refMain = refMain;
//        listOfSensorData = new Vector<ClusterableDoublePoint>();
//
//        listOfSensorDataFiltered = new Vector<ClusterableDoublePoint>();
//
//        listOfSensorData.add(new ClusterableDoublePoint(new double[]{0.0f, 0.0f}));
//
//        mSensor = refMain.getmSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//        if(mSensor == null)
//            System.out.println( "ACCELEROMETER not available");
//    }
//
////    public List<Cluster<ClusterableDoublePoint>>  clusterLastValuesSinceStart(List<ClusterableDoublePoint> points)
////    {
////        List<Cluster<ClusterableDoublePoint>> cluster =  refMain.getDbscan().cluster(points);
////        return  cluster;
////    }
//
//    public void subscribeToAccelerationSensor()
//    {
//        // Create listener
//        SensorEventListener accelerationSensorListener = new SensorEventListener()
//        {
//            @Override
//            public void onSensorChanged(SensorEvent sensorEvent) {
//                // More code goes here
////                System.out.println("zAcl: "+ sensorEvent.values[2]);
//                TextView viewCurrentValue = (TextView) refMain.findViewById(R.id.sensorValue);
//                TextView viewDeviation = (TextView) refMain.findViewById(R.id.viewStdDeviation);
//                TextView viewCount = (TextView) refMain.findViewById(R.id.viewSensorValuesCount);
//                TextView viewInformation = (TextView) refMain.findViewById(R.id.viewInfo);
//
//               // viewCurrentValue.setText("Acl_Z: "+formatter.format(sensorEvent.values[2]));
//                viewCount.setText("SensorList: "+listOfSensorData.size());
//
//
//                double x =0;
//                if(listOfSensorData.size()>0)
//                    x= listOfSensorData.get(listOfSensorData.size()-1).getPoint()[0]+stepX;
//
//                if(listOfSensorData.size()==1000)
//                {
//                    double[] skalars = new double[1000];
//                    for(int i=99; i<1000;++i)
//                        skalars[i]=listOfSensorData.get(i).getPoint()[1];
//                    stdDeviationValue=stdDeviation.evaluate(skalars);
//                    viewDeviation.setText("deviation: "+formatter.format(stdDeviationValue));
//                }
//
//                listOfSensorData.add(new ClusterableDoublePoint(new double[]{(float)x, sensorEvent.values[2]}));
//                if(refMain.packetList.size() >1)
//                {
//                    String left = Integer.toString(refMain.packetList.get(refMain.packetList.size()-1).left);
//                    String right = Integer.toString(refMain.packetList.get(refMain.packetList.size()-1).right);
//                    if(refMain.packetList.size()>10000)
//                        refMain.packetList.clear();
//                    viewInformation.setText("left:"+ left + " right: "+right);
//                }
//
//                double blockfilterValue = filter.blockFilter(10, stdDeviationValue, listOfSensorData, refMain);
//                listOfSensorData = filter.getListOfSensorData();
//                listOfSensorDataFiltered = filter.getListOfSensorDataFiltered();
//
//                if(startUsingClusters && blockfilterValue>(stdDeviationValue/10.0))
//                {
//                    int amount = 2;
//                    int start = listOfSensorDataFiltered.size()-1 - amount;
//                    int end = listOfSensorDataFiltered.size()-1;
//                    List<ClusterableDoublePoint> subPoints =listOfSensorDataFiltered.subList(start, end);//listOfSensorData.subList(start, end);//
//
//                    for(int i=0; i<subPoints.size(); ++i) {
//                        if (subPoints.get(i).getPoint()[1] < (stdDeviationValue / 10.0))
//                            subPoints.get(i).getPoint()[1] = 0;
//                    }
//
//                    refMain.getDbscan().evaluteList(subPoints);
//
//                   // refMain.getDbscan().evaluatePoint(new ClusterableDoublePoint(new double[]{0, blockfilterValue}));
//                }
//
//                if(listOfSensorData.size() >= 10000)
//                {
//                    listOfSensorData.clear();
//                    x= 0;
//                }
//                  refMain.createChart();
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int i) {
//            }
//        };
//
//        refMain.getmSensorManager().registerListener(accelerationSensorListener, mSensor, updatePeriod);
//    }
//}
