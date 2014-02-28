package com.hill30.android.serviceTracker.activityRecordStorage;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.hill30.android.serviceTracker.entities.ActivityRecordMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StorageConnection implements ServiceConnection {

    private Context context;
    private MessageListener listener;
    private StorageBinder service;

    public interface MessageListener {
        void onNewActivityRecord(int id, ActivityRecordMessage activityRecord);
    }

    private static void startIfNecessary(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return;
            }
        }
        context.startService(new Intent(context, serviceClass));
    }

    public StorageConnection(Context context, MessageListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void bind() {
        startIfNecessary(context, Storage.class);
        context.bindService(new Intent(context, Storage.class), this, Context.BIND_AUTO_CREATE);
    }

    public void unbind() {
        context.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = (StorageBinder)service;
        this.service.listener(listener);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public Set<Map.Entry<Integer, ActivityRecordMessage>> get() {
        return service.get();
    }

    public ActivityRecordMessage get(int id) {
        return service.get(id);
    }

    public void save(int id, String record) {
        service.save(id, record);
    }
}
