package com.hill30.android.mqttClient;

import android.content.Intent;
import android.os.Binder;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class ConnectionBinder extends Binder {
    private final Connection connection;
    private String topic;
    private MessageListener<String> messageListener;

    public ConnectionBinder(Connection connection, Intent intent) {
        this.connection = connection;
        this.topic = intent.getStringExtra(Service.TOPIC_NAME);
    }

//    @Override
    public String getTopic() {
        return topic;
    }

//    @Override
    public void connect() throws MqttException {
        connection.register(this);
        connection.connect(topic);
    }

//    @Override
    public void onMessageReceived(String message) {
        if (messageListener != null)
            messageListener.onMessageArrived(message);
    }

    public void listener(MessageListener<String> l) {
        messageListener = l;
    }
}
