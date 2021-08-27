package com.atakmap.android.harrissaspr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HhmpUdpService extends Service {
    public HhmpUdpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}