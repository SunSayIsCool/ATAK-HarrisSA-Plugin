package com.atakmap.android.harrissaspr;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.atakmap.android.harrissaspr.converters.SprSAparser;
import com.atakmap.android.harrissaspr.driver.CdcAcmSerialDriver;
import com.atakmap.android.harrissaspr.driver.ProbeTable;
import com.atakmap.android.harrissaspr.driver.UsbSerialDriver;
import com.atakmap.android.harrissaspr.driver.UsbSerialPort;
import com.atakmap.android.harrissaspr.driver.UsbSerialProber;
import com.atakmap.android.harrissaspr.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SprSerialService extends Service {

    static final String INTENT_ACTION_GRANT_USB = com.atakmap.app.BuildConfig.APPLICATION_ID + ".GRANT_USB";
    static final String INTENT_ACTION_DISCONNECT = com.atakmap.app.BuildConfig.APPLICATION_ID + ".Disconnect";
    static final String NOTIFICATION_CHANNEL = com.atakmap.app.BuildConfig.APPLICATION_ID + ".Channel";
    static final String INTENT_CLASS_MAIN_ACTIVITY = com.atakmap.app.BuildConfig.APPLICATION_ID + ".SprSerialService";

    private static final int WRITE_WAIT_MILLIS = 2000;
    private static final int READ_WAIT_MILLIS = 2000;

    private BroadcastReceiver broadcastReceiver;
    private Handler mainLooper;
    private ProbeTable customTable;

    private UsbDeviceConnection connection;

    private SerialInputOutputManager usbIoManager;
    private UsbSerialPort port;
    private boolean connected = false;
    private boolean stopped = false;

    private final static String TAG = "SPRSerialService";



    @Override
    public void onCreate() {
        super.onCreate();
        customTable = new ProbeTable();
        customTable.addProduct(0x19a5, 0x0012, CdcAcmSerialDriver.class);

        Notification notification = new Notification();
        startForeground(1, notification);
        Log.d(TAG,"Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (!(port == null)) {
                port.close();
                Log.d(TAG, "Port closed");
                stopped = true;
            }
        } catch (Exception e) {
            Log.d(TAG, "Port closing error");
        }

        Log.d(TAG,"Service stopped");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UsbSerialProber prober = new UsbSerialProber(customTable);
        UsbManager manager = (UsbManager) getApplication().getSystemService(Context.USB_SERVICE);

        List<UsbSerialDriver> availableDrivers = prober.findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            Log.d(TAG, "Drivers not found");
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);

        Log.d(TAG, "Permission = " + manager.hasPermission(driver.getDevice()));

        // Checking for USB device permission is granted
        if (manager.hasPermission(driver.getDevice())) {
            Log.d(TAG, "Service started");
            SomeTask();
        } else {
            try {
                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(getApplication(), 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                manager.requestPermission(driver.getDevice(), usbPermissionIntent);
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Log.d(TAG, "Service started");
                SomeTask();
            }
            //Intent intent_stop = new Intent(HarrisSaSprDropDownReceiver.BROADCAST_ACTION);
            //intent_stop.putExtra(HarrisSaSprDropDownReceiver.PARAM_STATUS, false);
            //sendBroadcast(intent_stop);
            //onDestroy();
        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void SomeTask() {
        new Thread(new Runnable() {
            public void run() {
                Intent intent = new Intent(HarrisSaSprDropDownReceiver.BROADCAST_ACTION);
                UsbSerialProber prober = new UsbSerialProber(customTable);
                UsbManager manager = (UsbManager) getApplication().getSystemService(Context.USB_SERVICE);

                List<UsbSerialDriver> availableDrivers = prober.findAllDrivers(manager);
                Log.d(TAG, availableDrivers.toString());
                if (availableDrivers.isEmpty()) {
                    Log.d(TAG, "Drivers not found");
                    stopSelf();
                    onDestroy();
                    intent.putExtra(HarrisSaSprDropDownReceiver.PARAM_STATUS, false);
                }

                // Open a connection to the first available driver.
                UsbSerialDriver driver = availableDrivers.get(0);

                Log.d(TAG, "Device list:\n" + driver.getDevice().toString());
                Log.d(TAG, "Ports list:\n" + driver.getPorts().toString());


                try {
                    connection = manager.openDevice(driver.getDevice());
                    Log.d(TAG, "Connection succeed");
                } catch (Exception e) {
                    Log.d(TAG, "Connection opening error");
                    stopSelf();
                    onDestroy();
                    intent.putExtra(HarrisSaSprDropDownReceiver.PARAM_STATUS, false);
                }

                port = driver.getPorts().get(1);

                Log.d(TAG,"Connected to port: " + port.toString());

                try {
                    port.open(connection);
                    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                    SprSAparser sprparser = new SprSAparser();
                    byte buffer[] = new byte[128];
                    while (true) {
                        int numBytesRead = port.read(buffer, 1000);
                        if (numBytesRead != 0) {
                            Log.d(TAG, "Read " + numBytesRead + " bytes.");
                            Log.d(TAG, "First byte " + new String(buffer));
                            sprparser.saPass(buffer);
                            if (sprparser.saPass(buffer) && (sprparser.isValid())) {
                                intent.putExtra(HarrisSaSprDropDownReceiver.SPR_BYTE, buffer);
                                sendBroadcast(intent);
                            }
                        }
                        if (stopped) {
                            stopSelf();
                        }
                    }
                } catch (IOException eio) {
                    Log.d(TAG, "Read error");
                    stopSelf();
                    intent.putExtra(HarrisSaSprDropDownReceiver.PARAM_STATUS, false);
                    sendBroadcast(intent);
                }

                //stopSelf();
            }
        }).start();
    }

}

