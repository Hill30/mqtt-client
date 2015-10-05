package com.hill30.android.mqttClient;

import android.content.*;
import android.os.Binder;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

class ConnectionBinder extends Binder {
    private final Connection connection;
    private String topic;
    private String brokerUrl;
    private String brokerUsername;
    private String brokerPassword;
    private String userId;
    private String clientId;

    private ServiceConnection.MessageListener messageListener;
    private ServiceConnection.ConnectionStateListener connectionStateListener;

    public ConnectionBinder(Connection connection, Intent intent) {
        this.connection = connection;
        topic = intent.getStringExtra(Service.TOPIC_NAME);
        brokerUrl = intent.getStringExtra(Service.BROKER_URL);
        brokerUsername = intent.getStringExtra(Service.BROKER_USERNAME);
        brokerPassword = intent.getStringExtra(Service.BROKER_PASSWORD);
        userId = intent.getStringExtra(Service.USER_ID);
        clientId = intent.getStringExtra(Service.CLIENT_ID);
    }

    public void setConnectionParameters(String topic, String brokerUrl, String username, String password, String userId, String clientId){
        this.topic = topic;
        this.brokerUrl = brokerUrl;
        this.brokerUsername = username;
        this.brokerPassword = password;
        this.userId = userId;
        this.clientId = clientId;
    }

    public void connect() throws MqttException, IOException {
        connection.connect(this, topic, userId, brokerUrl, brokerUsername, brokerPassword, clientId);
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

    public void disconnect(ServiceConnection.ConnectionStateListener connectionStateListener) {
        connection.disconnect(connectionStateListener);
    }

    public void resume() {
        connection.resume();
    }


}
