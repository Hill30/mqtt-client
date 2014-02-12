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
    private MessageListener messageListener;

    public interface MessageListener {
        void onMessageArrived(String message);
    }

    public ServiceConnection(Context context, String userName, String password, String topic, MessageListener messageListener) {
        this.messageListener = messageListener;
        // todo: validate topic for illegal characters - it is important for matching incoming message to the recipients (see Connection.java)
        context.bindService(
                new Intent(context, Service.class)
                        .putExtra(Service.USER_NAME, userName)
                        .putExtra(Service.PASSWORD, password)
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
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        connectionBinder.disconnectBinder();
    }

    public void send(String message) {
        connectionBinder.send(message);
    }

}
