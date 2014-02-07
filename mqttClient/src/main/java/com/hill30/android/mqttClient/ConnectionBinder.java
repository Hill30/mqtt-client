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
    private ServiceConnection.MessageListener messageListener;

    public ConnectionBinder(Connection connection, Intent intent) {
        // todo: in the inbound topic replace 'user' with the real user name
        this.connection = connection;
        String topic = intent.getStringExtra(Service.TOPIC_NAME);
        inboundTopic = topic + "/Inbound/user";
        outboundTopic = topic + "/Outbound";
    }

    public void connect() throws MqttException {
        connection.connect(this, inboundTopic);
    }

    public void onMessageReceived(String message) {
        if (messageListener != null)
            messageListener.onMessageArrived(message);
    }

    public void send(String message) {
        connection.send(outboundTopic, message);
    }

    public void listener(ServiceConnection.MessageListener listener) {
        messageListener = listener;
    }

    public void disconnectBinder() {
        connection.unregisterSubscriber(inboundTopic);
    }
}
