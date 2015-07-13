package com.hill30.android.mqttClient;

import android.content.*;
import android.os.Binder;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

class ConnectionBinder extends Binder {
    private final Connection connection;
    private final String topic;
    private final String brokerUrl;
    private final String username;
    private final String password;

    private ServiceConnection.MessageListener messageListener;
    private ServiceConnection.ConnectionStateListener connectionStateListener;

    public ConnectionBinder(Connection connection, Intent intent) {
        this.connection = connection;
        topic = intent.getStringExtra(Service.TOPIC_NAME);
        brokerUrl = intent.getStringExtra(Service.BROKER_URL);
        username = intent.getStringExtra(Service.USERNAME);
        password = intent.getStringExtra(Service.PASSWORD);
    }

    public void connect() throws MqttException, IOException {
        connection.connect(this, topic, brokerUrl, username, password);
    }

    public void onMessageReceived(String message) {
        if (messageListener != null)
            messageListener.onMessageArrived(message);
    }

    public IMqttAsyncClient getMqttClient(){
        return connection.getMqttClient();
    }

    public void send(String message) throws IOException {
        connection.send(topic, message);
    }

    public void listener(ServiceConnection.MessageListener listener) {
        messageListener = listener;
    }

    public void setConnectionStateListener(ServiceConnection.ConnectionStateListener connectionStateListener){
        this.connectionStateListener = connectionStateListener;
    }

    public void disconnectBinder() {
        connection.unregisterSubscriber(topic);
    }

    public void onConnectionStateChanged(int connectionStatus) {
        if(connectionStateListener != null)
            connectionStateListener.onConnectionStateChanged(connectionStatus);
    }

    public void suspend() {
        connection.suspend();
    }

    public void resume() {
        connection.resume();
    }
}
