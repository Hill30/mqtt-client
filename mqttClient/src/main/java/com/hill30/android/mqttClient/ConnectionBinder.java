package com.hill30.android.mqttClient;

import android.content.*;
import android.os.Binder;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class ConnectionBinder extends Binder {
    private final Connection connection;
    private final String inboundTopic;
    private final String outboundTopic;
    private ServiceConnection.MessageListener<String> messageListener;

    public ConnectionBinder(Connection connection, Intent intent) {
        this.connection = connection;
        String topic = intent.getStringExtra(Service.TOPIC_NAME);
        inboundTopic = topic + "/Inbound/user";
        outboundTopic = topic + "/Outbound";
    }

    public void connect() throws MqttException {
        connection.registerSubscriber(inboundTopic, this);
        connection.connect(inboundTopic);
    }

    public void onMessageReceived(String message) {
        if (messageListener != null)
            messageListener.onMessageArrived(message);
    }

    public void send(String message) {
        connection.send(outboundTopic, message);
    }

    public void listener(ServiceConnection.MessageListener<String> listener) {
        messageListener = listener;
    }

    public void disconnectBinder() {
        connection.unregisterSubscriber(inboundTopic);
    }
}
