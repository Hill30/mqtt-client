package com.hill30.android.mqttClient;

import android.content.Intent;
import android.os.Binder;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class ConnectionBinder extends Binder {
    private final Connection connection;
    private final String inboundTopic;
    private final String outboundTopic;
    private String topic;
    private MessageListener<String> messageListener;

    public ConnectionBinder(Connection connection, Intent intent) {
        this.connection = connection;
        topic = intent.getStringExtra(Service.TOPIC_NAME);
        inboundTopic = topic + ".Inbound.user";
        outboundTopic = topic + ".Outbound";
    }

//    @Override
    public String getTopic() {
        return topic;
    }

//    @Override
    public void connect() throws MqttException {
        connection.register(this);
        connection.connect(inboundTopic);
    }

//    @Override
    public void onMessageReceived(String message) {
        if (messageListener != null)
            messageListener.onMessageArrived(message);
    }

    public void send(String message) throws MqttException {
        connection.send(outboundTopic, message);
    }

    public void listener(MessageListener<String> l) {
        messageListener = l;
    }
}
