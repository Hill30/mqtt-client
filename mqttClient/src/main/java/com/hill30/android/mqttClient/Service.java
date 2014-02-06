package com.hill30.android.mqttClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class Service extends android.app.Service {

    public static final String BROKER_URL = "com.hill30.android.mqttClient.broker-url";
    public static final String USER_NAME = "com.hill30.android.mqttClient.user-name";
    public static final String PASSWORD = "com.hill30.android.mqttClient.password";
    public static final String TOPIC_NAME = "com.hill30.android.mqttClient.topic-name";

    private Connection connection;
    private HashMap<String, ConnectionBinder> recipients = new HashMap<String, ConnectionBinder>();

    @Override
    public void onCreate() {
        HandlerThread connectionThread = new HandlerThread("mqttConnection", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        connectionThread.start();

        SharedPreferences prefs = getSharedPreferences("MqttConnection", MODE_PRIVATE);
        try {
            connection = new Connection(
                    connectionThread.getLooper(),
                    this,
                    prefs.getString(BROKER_URL, ""),
                    prefs.getString(USER_NAME, ""),
                    prefs.getString(PASSWORD, "")
            ) {

                @Override
                public void onMessageReceived(String topic, String message) {
                    ConnectionBinder recipient = recipients.get(topic);
                    if (recipient != null)
                        recipient.onMessageReceived(message);
                }

            };
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new ConnectionBinder(connection, intent.getCharSequenceArrayExtra(TOPIC_NAME).toString());
    }

    public void registerReceiver(ConnectionBinder connectionBinder) {
        recipients.put(connectionBinder.getTopic(), connectionBinder);
    }

    public void unregisterReceiver(ConnectionBinder connectionBinder) {
        recipients.remove(connectionBinder.getTopic());
    }
}
