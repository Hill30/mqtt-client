package com.hill30.android.mqttClient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by mfeingol on 2/6/14.
 */
public class ServiceConnection implements android.content.ServiceConnection {
    private ConnectionBinder connectionBinder;

    public interface MessageListener<T> {
        void onMessageArrived(T message);
    }

    public ServiceConnection(Context context, String topic) {
        // todo: validate topic for illegal characters
        context.bindService(
                new Intent(context, Service.class).putExtra(Service.TOPIC_NAME, topic),
                this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        connectionBinder = (ConnectionBinder)binder;
        attachListener();
        try {
            connectionBinder.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        connectionBinder.disconnectBinder();
    }

    public void listener(MessageListener<String> l) {
        connectionBinder.listener(l);
    }

    public void attachListener() {}

    public void send(String message) {
        connectionBinder.send(message);
    }

}
