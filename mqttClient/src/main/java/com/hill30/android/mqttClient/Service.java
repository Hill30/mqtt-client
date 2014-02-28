package com.hill30.android.mqttClient;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class Service extends android.app.Service {

    public static final String TAG = "MQTT Service";
    public static final String BROKER_URL = "com.hill30.android.mqttClient.broker-url";
    public static final String USER_NAME = "com.hill30.android.mqttClient.user-name";
    public static final String PASSWORD = "com.hill30.android.mqttClient.password";
    public static final String TOPIC_NAME = "com.hill30.android.mqttClient.topic-name";
    public static final String RECONNECT = "com.hill30.android.mqttClient.reconnect";
    public static final String CONNECTION_RETRY_INTERVAL = "com.hill30.android.mqttClient.connection_retry_interval";
    public static final int RESTART = 1;

    private Connection connection;
    private Timer reconnectTimer = new Timer();

    private int retry_interval = 5000; //milliseconds

    private BroadcastReceiver networkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            networkAvailable = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (networkAvailable)
                Log.e(TAG, "Network restored. Reconnecting");
                try {
                    connection.connectIfNecessary();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
        }
    };
    private boolean networkAvailable = false;

    private BroadcastReceiver messagingServiceCommandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            connection.sendEmptyMessage(RESTART);
        }
    };
    private NotificationCompat.Builder notificationBuilder;

    @Override
    public void onCreate() {
        HandlerThread connectionThread = new HandlerThread("mqttConnection", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        connectionThread.start();

        connection = new Connection(connectionThread.getLooper(), this);

        registerReceiver(networkStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    public void onDestroy() {
        // todo: implement connection disconnect
        unregisterReceiver(networkStatusReceiver);
        super.onDestroy();
    }

    public void onConnectionLost() {
        // todo: do something smarter about onConnectionLost attempts
        if (networkAvailable) {
            Log.e(TAG, String.format("Connection failure. Reconnecting in %d sec.", retry_interval/1000));
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
        } else
            Log.e(TAG, "Connection failure. Network unavailable.");

    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new ConnectionBinder(connection, intent);
    }
}
