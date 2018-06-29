package com.example.bluefish.anydrum;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.driver.UsbSerialDriver;

import java.util.List;


public  class ArduinoUSB {
    private MainActivity refMain;

    private UsbManager manager;


    public  ArduinoUSB(MainActivity refMain)
    {
        this.refMain = refMain;
        manager = (UsbManager) refMain.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            TextView viewInformation = (TextView) refMain.findViewById(R.id.viewInfo);
            viewInformation.setText("USB-Arduino not detected");
            return;
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
