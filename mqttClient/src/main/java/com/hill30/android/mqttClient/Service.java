package com.hill30.android.mqttClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private static final String LOG_TAG = Service.class.getName();

    public static final String TAG = "MQTT Service";
    public static final String BROKER_URL = "com.hill30.android.mqttClient.broker-url";
    public static final String USER_NAME = "com.hill30.android.mqttClient.user-name";
    public static final String PASSWORD = "com.hill30.android.mqttClient.password";
    public static final String TOPIC_NAME = "com.hill30.android.mqttClient.topic-name";
    public static final String CONNECTION_RETRY_INTERVAL = "com.hill30.android.mqttClient.connection_retry_interval";
    public static final String IS_SSL = "com.hill30.android.mqttClient.is_ssl";

    private Connection connection;
    private Timer reconnectTimer = new Timer();

    private int retry_interval = 5000; //milliseconds
    private boolean isSSL = false;

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

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "Network connected");
            }
        }, new IntentFilter(NetworkStatusReceiver.INTENT_NETWORK_CONNECTED));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "Network disconnected");
            }
        }, new IntentFilter(NetworkStatusReceiver.INTENT_NETWORK_DISCONNECTED));

    }

    public void onConnectFailure() {
        // todo: do something smarter about onConnectFailure attempts
        Log.e(TAG, "received call onConnectFailure.");

        Log.e(TAG, "reconnecting in 5 sec.");
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    connection.connectIfNecessary();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }, retry_interval);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new ConnectionBinder(connection, intent);
    }
}
