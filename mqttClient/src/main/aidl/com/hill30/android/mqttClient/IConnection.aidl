package com.hill30.android.mqttClient;

interface IConnection {
    String getTopic();
    void connect();
    void onMessageReceived(String message);
}