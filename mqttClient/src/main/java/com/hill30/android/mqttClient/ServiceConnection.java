package com.hill30.android.mqttClient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by mfeingol on 2/6/14.
 */
public class ServiceConnection implements android.content.ServiceConnection {
    private ConnectionBinder service;

    public ServiceConnection(Context context, String topic) {
        context.bindService(
                new Intent(context, Service.class).putExtra(Service.TOPIC_NAME, topic),                this, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        this.service = (ConnectionBinder)service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public void unbind(Context context) {
        context.unbindService(this);
    }
}
