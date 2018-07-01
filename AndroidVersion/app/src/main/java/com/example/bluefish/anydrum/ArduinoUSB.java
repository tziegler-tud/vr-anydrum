package com.example.bluefish.anydrum;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.widget.TextView;


import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.hoho.android.usbserial.util.HexDump;


import java.util.List;


public  class ArduinoUSB extends Activity {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private MainActivity refMain;
    private TextView viewInformation;
    private UsbManager manager;

    private SerialInputOutputManager mSerialIoManager;

    public ArduinoUSB(MainActivity refMain) {
        this.refMain = refMain;
        this.viewInformation = (TextView) refMain.findViewById(R.id.viewInfo);

        manager = (UsbManager) refMain.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (availableDrivers.isEmpty()) {
            viewInformation.setText("USB-Arduino not detected");
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(refMain.getBaseContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        manager.requestPermission(driver.getDevice(), mPermissionIntent);

        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
            viewInformation.setText("UsbManager.requestPermission(...)");
            return;
        }

        final String title = String.format("Vendor %s Product %s", //vendor: 1a86, product:7523
                HexDump.toHexString((short) driver.getDevice().getVendorId()),
                HexDump.toHexString((short) driver.getDevice().getProductId()));

        // Read some data! Most have just one port (port 0).
        UsbSerialPort port = driver.getPorts().get(0); //checked, port size is only 1 when arduino is connected
        try {
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE); //i: 9600 ; standard: 115200 //BaudRate

            byte buffer[] = new byte[32];
            int numBytesRead = port.read(buffer, 500);
            StringBuilder byteText = new StringBuilder();
            for(int i= 16; i<32;++i)
            {
                byteText.append(buffer[i] +" ");
            }
            viewInformation.setText("Read16 " + numBytesRead+ " bytes");
        } catch (IOException e) {
            viewInformation.setText("error read: " + e.getMessage());
        } finally {
            try {
                port.close();
            } catch (IOException e2) {
                viewInformation.setText("error close port: " + e2.getMessage());
            }
        }
    }


    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    viewInformation.setText("Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    ArduinoUSB.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArduinoUSB.this.updateReceivedData(data);
                        }
                    });
                }
            };


    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";
        viewInformation.setText(message);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
