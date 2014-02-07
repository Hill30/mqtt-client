package com.hill30.android.mqttClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class Service extends android.app.Service {

    public static final String BROKER_URL = "com.hill30.android.mqttClient.broker-url";
    public static final String USER_NAME = "com.hill30.android.mqttClient.user-name";
    public static final String PASSWORD = "com.hill30.android.mqttClient.password";
    public static final String TOPIC_NAME = "com.hill30.android.mqttClient.topic-name";

    private Connection connection;
    private Timer reconnectTimer = new Timer();

    @Override
    public void onCreate() {
        HandlerThread connectionThread = new HandlerThread("mqttConnection", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        connectionThread.start();

        // todo: come up with a better way to receive broker url and credentials
        // todo: should the below be run on the connectionThread?
        SharedPreferences prefs = getSharedPreferences("MqttConnection", MODE_PRIVATE);
        try {
            connection = new Connection(
                    connectionThread.getLooper(),
                    this,
                    prefs.getString(BROKER_URL, "tcp://10.0.2.2:1883"),
                    prefs.getString(USER_NAME, "userName"),
                    prefs.getString(PASSWORD, "password")
            );
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void reconnect() {
        // todo: do something smarter about reconnect attempts
        Log.d(Connection.TAG, "reconnecting in 5 sec.");
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    connection.connectIfNecessary();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }, 5000);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new ConnectionBinder(connection, intent);
    }
}
