package com.hill30.android.serviceTracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by azavarin on 2/26/14.
 */
public class MessagePersistenceService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("MessagePersistenceService", "Service started");
    }
}
