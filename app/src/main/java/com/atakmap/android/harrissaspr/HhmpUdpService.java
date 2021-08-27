package com.atakmap.android.harrissaspr;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.atakmap.android.harrissaspr.converters.HarrisSAparser;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class HhmpUdpService extends Service {

    private final static String TAG = "HhmpUdpService";

    private byte[] rcv_buf = null;
    private DatagramSocket ds = null;
    private DatagramPacket DpReceive = null;
    private boolean stop = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = new Notification();
        startForeground(1, notification);
        Log.d(TAG, "UDP Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SAUDPengine();
        Log.d(TAG, "UDP Service started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop = true;
        ds.close();
        if (ds == null) {
            Log.d(TAG, "Socket closed");
        }
        Log.d(TAG, "UDP Service stopped");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void SAUDPengine() {
        new Thread(new Runnable() {
            public void run() {
                Intent intent = new Intent(HarrisSaSprDropDownReceiver.BROADCAST_ACTION);
                Log.d(TAG, "UDP Thread: Started");
                try {
                    ds = new DatagramSocket(10011);
                    ds.setReuseAddress(true);
                    if (ds != null) {
                        Log.d(TAG, "UDP Thread: Socket created");
                    }
                } catch (SocketException e) {
                    Log.d(TAG, "UDP Thread: SocketException");
                }
                if (ds != null) {
                    HarrisSAparser saparser = new HarrisSAparser();
                    while (true) {
                        if (stop) {
                            Log.d(TAG, "UDP Thread: Stopped");
                            break;
                        }
                        try {
                            rcv_buf = new byte[512];
                            DpReceive = new DatagramPacket(rcv_buf, rcv_buf.length);
                            ds.receive(DpReceive);
                            String str = new String(rcv_buf);
                            str = str.replaceAll("[\\u0000-\u0009]", "");
                            saparser.init_sa(str);
                            if (saparser.GeneralPass()) {
                                intent.putExtra(HarrisSaSprDropDownReceiver.UDP_STRING, str);
                                sendBroadcast(intent);
                                Log.d(TAG, "UDP Thread: Received valid SA");
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "UDP Thread: Exception");
                        }
                    }
                    stopSelf();
                }
            }
        }).start();
    }
}