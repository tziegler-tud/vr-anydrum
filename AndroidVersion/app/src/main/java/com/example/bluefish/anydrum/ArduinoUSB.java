package com.example.bluefish.anydrum;

import AsyncTasks.ShowInfoMsgTask;
import Filters.FilterHelper;
import Learning.ClusterableDoublePoint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.widget.TextView;

import com.felhr.utils.HexData;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.driver.UsbSerialDriver;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.hoho.android.usbserial.util.HexDump;

import  org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;


import java.util.List;
import java.util.Vector;


public  class ArduinoUSB {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private MainActivity refMain;
    private TextView viewInformation;
    private long oldSystemTime=0;
    private long updateTimeMS = 0;

    UsbDevice device;
    UsbDeviceConnection usbConnection;
    UsbSerialDevice serial;
    public Vector<ArduinoPacket> sensorDataRaw;

    //Data
    private FilterHelper filter;
    private double stepX = 1;
    private boolean startUsingClusters=false;
    public void setStartUsingClusters(boolean startUsingClusters) {
        this.startUsingClusters = startUsingClusters;
    }


    private StandardDeviation stdDeviation;
    private double stdDeviationValueL=0;
    private double stdDeviationValueR=0;

    private Vector<ClusterableDoublePoint> listOfSensorDataFilteredArduinoL, listOfSensorDataFilteredArduinoR;
    public Vector<ClusterableDoublePoint> getListOfSensorDataFilteredArduinoL() {
        return listOfSensorDataFilteredArduinoL;
    }
    public Vector<ClusterableDoublePoint> getListOfSensorDataFilteredArduinoR() {
        return listOfSensorDataFilteredArduinoR;
    }
    private Vector<ClusterableDoublePoint> listOfSensorDataArduinoL, listOfSensorDataArduinoR;
    public Vector<ClusterableDoublePoint> getListOfSensorDataArduinoL() {
        return listOfSensorDataArduinoL;
    }
    public Vector<ClusterableDoublePoint> getListOfSensorDataArduinoR() {
        return listOfSensorDataArduinoR;
    }


    public ArduinoUSB(MainActivity refMain) {
        sensorDataRaw = new Vector<>();
        filter = new FilterHelper();
        listOfSensorDataArduinoL= new Vector<ClusterableDoublePoint>();
        listOfSensorDataArduinoR= new Vector<ClusterableDoublePoint>();

        listOfSensorDataFilteredArduinoL = new Vector<ClusterableDoublePoint>();
        listOfSensorDataFilteredArduinoR = new Vector<ClusterableDoublePoint>();

        listOfSensorDataArduinoL.add(new ClusterableDoublePoint(new double[]{0.0f, 0.0f}));
        listOfSensorDataArduinoR.add(new ClusterableDoublePoint(new double[]{0.0f, 0.0f}));

        this.refMain = refMain;
        this.viewInformation = (TextView) refMain.findViewById(R.id.viewInfo);

        UsbManager manager = (UsbManager) refMain.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            viewInformation.setText("no driver for connected device");
            return;
        }

        UsbSerialDriver driver = availableDrivers.get(0);
        device = driver.getDevice();

        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(refMain.getBaseContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        manager.requestPermission(device, mPermissionIntent);

        usbConnection = manager.openDevice(device);
        if (usbConnection == null) {
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
            viewInformation.setText("UsbManager.requestPermission(...)");
            return;
        }

        serial = UsbSerialDevice.createUsbSerialDevice(device, usbConnection);


        if (serial != null) {
            serial.open();
            serial.setBaudRate(9600);//115200
            serial.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serial.setParity(UsbSerialInterface.PARITY_ODD);
            serial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);

            serial.read(mCallback);
            viewInformation.setText("serial set!");
        } else {
            viewInformation.setText("serial error!");
        }
    }



    /*if update too fast it crashes app?! */
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {

        @Override
        public void onReceivedData(byte[] data)
        {
            int length = data.length;
            long time= System.currentTimeMillis();
//            if(time-oldSystemTime >= updateTimeMS)
//            {
                int left=0, right=0;
//                if(data.length % 4 == 0)
                {
                    if(data.length>1)
                        left = (data[data.length - 1] << 8) | data[data.length - 2];
                    if(data.length>3)
                        right =  (data[data.length - 3] << 8) | data[data.length - 4];




                    Integer[] integerObjArray = new Integer[2];
                    integerObjArray[0] = left;
                    integerObjArray[1] = right;

                    ArduinoPacket packet = new ArduinoPacket(left,right);
                    if(sensorDataRaw.size() != 10000)
                        sensorDataRaw.clear();
                    sensorDataRaw.add(packet);
                    refMain.updateArduino(packet);
//                    new SerialTask().execute(integerObjArray);


                    if(data.length > 10000)data = null;

                }
                oldSystemTime = time;

            }
//        }
    };



    public class SerialTask extends AsyncTask<Integer[],Integer, Integer> {

        protected Integer doInBackground(Integer[]... data) {

            int left =data[0][0];
            int right = data[0][1];

            double x =0;
            x= listOfSensorDataArduinoL.get(listOfSensorDataArduinoL.size()-1).getPoint()[0]+stepX;

            if(listOfSensorDataArduinoL.size() >= 10000)
            {
                listOfSensorDataArduinoL.clear();
                listOfSensorDataArduinoR.clear();
                x= 0;
            }

            listOfSensorDataArduinoL.add(new ClusterableDoublePoint(new double[]{(float)x, Double.parseDouble(Integer.toString(left))}));
            listOfSensorDataArduinoR.add(new ClusterableDoublePoint(new double[]{(float)x, Double.parseDouble(Integer.toString(right))}));


//            if(listOfSensorDataArduinoL.size()==1000)
//            {
//                double[] skalarsL = new double[1000];
//                double[] skalarsR = new double[1000];
//
//                for(int i=99; i<1000;++i) {
//                    skalarsL[i] = listOfSensorDataArduinoL.get(i).getPoint()[1];
//                    skalarsR[i] = listOfSensorDataArduinoR.get(i).getPoint()[1];
//                }
//
//                stdDeviationValueL=stdDeviation.evaluate(skalarsL);
//                stdDeviationValueR=stdDeviation.evaluate(skalarsR);
//            }
//            if(startUsingClusters)
//                evaluateSound(listOfSensorDataArduinoL, listOfSensorDataArduinoR);


            ArduinoPacket packet = new ArduinoPacket(left, right);
            return 0;
        }

        protected void evaluateSound(Vector<ClusterableDoublePoint> listOfSensorDataL,Vector<ClusterableDoublePoint> listOfSensorDataR )
        {
            double blockfilterValueL= filter.blockFilter(10, stdDeviationValueL, listOfSensorDataL, refMain);
            Vector<ClusterableDoublePoint> listOfSensorDataFilteredL = filter.getListOfSensorDataFiltered();
//            listOfSensorData = filter.getListOfSensorData();
            double blockfilterValueR= filter.blockFilter(10, stdDeviationValueR, listOfSensorDataR, refMain);
            Vector<ClusterableDoublePoint> listOfSensorDataFilteredR = filter.getListOfSensorDataFiltered();

            //left
            if(startUsingClusters && blockfilterValueL>(stdDeviationValueL/10.0))
        {
            int amount = 2;
            int start = listOfSensorDataFilteredL.size()-1 - amount;
            int end = listOfSensorDataFilteredL.size()-1;
            List<ClusterableDoublePoint> subPoints =listOfSensorDataFilteredL.subList(start, end);//listOfSensorData.subList(start, end);//

            for(int i=0; i<subPoints.size(); ++i) {
                if (subPoints.get(i).getPoint()[1] < (stdDeviationValueL / 10.0))
                    subPoints.get(i).getPoint()[1] = 0;
            }

            refMain.getDbscan().evaluteList(subPoints);

            // refMain.getDbscan().evaluatePoint(new ClusterableDoublePoint(new double[]{0, blockfilterValue}));
        }

            //right
            if(startUsingClusters && blockfilterValueR>(stdDeviationValueR/10.0))
            {
                int amount = 2;
                int start = listOfSensorDataFilteredR.size()-1 - amount;
                int end = listOfSensorDataFilteredR.size()-1;
                List<ClusterableDoublePoint> subPoints =listOfSensorDataFilteredR.subList(start, end);//listOfSensorData.subList(start, end);//

                for(int i=0; i<subPoints.size(); ++i) {
                    if (subPoints.get(i).getPoint()[1] < (stdDeviationValueR / 10.0))
                        subPoints.get(i).getPoint()[1] = 0;
                }

                refMain.getDbscan().evaluteList(subPoints);

                // refMain.getDbscan().evaluatePoint(new ClusterableDoublePoint(new double[]{0, blockfilterValue}));
            }
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Integer val) {
        }

    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
