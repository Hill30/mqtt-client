package com.hill30.android.mqttClient;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

public class ServiceConnection implements android.content.ServiceConnection {
    private ConnectionBinder connectionBinder;
    private Context context;
    private MessageListener messageListener;

    public interface MessageListener {
        void onMessageArrived(String message);
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

    public ServiceConnection(Context context, String topic, MessageListener messageListener) {
        this.context = context;
        this.messageListener = messageListener;

        startIfNecessary(context, Service.class);

        // todo: validate topic for illegal characters - it is important for matching incoming message to the recipients (see Connection.java)
        context.bindService(
                new Intent(context, Service.class)
                        .putExtra(Service.TOPIC_NAME, topic),
                this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        connectionBinder = (ConnectionBinder)binder;
        connectionBinder.listener(messageListener);
        try {
            connectionBinder.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        connectionBinder.disconnectBinder();
    }

    public void send(String message) throws IOException {
        connectionBinder.send(message);
    }

    public void close() {
        context.unbindService(this);
    }

}
