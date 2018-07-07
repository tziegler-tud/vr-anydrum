package SerialCommunication;

/**
 * Created by tom on 18.05.18.
 */


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import com.example.bluefish.anydrum.MainActivity;

import java.util.HashMap;

public class BroadcastReceiver {

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";


    PendingIntent mPermissionIntent = null;
    UsbManager mUsbManager = null;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

    };
}