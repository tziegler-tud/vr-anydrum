package com.example.bluefish.anydrum;

import AsyncTasks.ShowInfoMsgTask;
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

import java.util.List;


public  class ArduinoUSB {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private MainActivity refMain;
    private TextView viewInformation;
    private int packetOffset = 4;
    private long oldSystemTime=0;
    private long updateTimeMS = 0;

    UsbDevice device;
    UsbDeviceConnection usbConnection;
    UsbSerialDevice serial;


    public ArduinoUSB(MainActivity refMain) {
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
            long time= System.currentTimeMillis();
            if(time-oldSystemTime >= updateTimeMS)
            {
                int left=0, right=0;
//                if(data.length >= packetOffset)
//                {
//                    packetOffset += 4;
//
//                    left = data[data.length - 4];
//                    left = (byte) (left << 8);
//                    left = left |  data[data.length - 3];
////                    left +=  data[data.length - 3];
//
//                    right = data[data.length - 1];
//                    right = (byte) (right <<  8);
//                    right = right | data[data.length - 2];
////                    right += data[data.length - 2];
////                    System.out.println("bytelength: "+data.length+" pck "+packetOffset);
////                    System.out.println( "left: "+left+"  right: "+right);
//
////                    int dataLO = (data[data.length - 4] >> 8) & 0xF;
////                    int dataHI = data[data.length - 3]  & 0xF;
////                    int help =0;
////                    help = dataHI;
////                    help = (byte)(dataHI << 8);
////                    help += dataLO;
////
////                    Integer[] integerObjArray = new Integer[2];
////                    integerObjArray[0] = left;
////                    integerObjArray[1] = right;
//
////                    new SerialTask().execute(integerObjArray);
//
                    ArduinoPacket packet = new ArduinoPacket(+data[data.length - 1], +data[data.length - 1]);
                    refMain.storeArduinoData(packet);

//                    System.out.println("hlp:43 "+data[data.length - 4]+"  "+data[data.length - 3]);

                    if(data.length > 200000)
                        data = null;
               // }
                    oldSystemTime = time;

            }
        }
    };



    public class SerialTask extends AsyncTask<Integer[],Integer, Integer> {

        protected Integer doInBackground(Integer[]... data) {

            int left =data[0][0];
            int right = data[0][1];
            ArduinoPacket packet = new ArduinoPacket(left, right);

            refMain.storeArduinoData(packet);
            return 0;
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
