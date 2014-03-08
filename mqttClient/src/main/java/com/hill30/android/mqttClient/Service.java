package com.hill30.android.mqttClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Timer;
import java.util.TimerTask;

public class Service extends android.app.Service {

    public static final String TAG = "MQTT Service";
    public static final String TOPIC_NAME = "com.hill30.android.mqttClient.topic-name";
    public static final int RECONNECT = 1;
    public static final String SERVICE_COMMAND = "com.hill30.android.mqttClient.service-command";
    private static final int retry_interval = 20000; //milliseconds

    private Connection connection;
    private Timer reconnectTimer = new Timer();


    private boolean networkAvailable = false;

    private BroadcastReceiver networkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            networkAvailable = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (networkAvailable)
                Log.e(TAG, "Network restored. Reconnecting");
            connection.executeCommand(
                    new Intent(Service.SERVICE_COMMAND)
                            .putExtra(Service.SERVICE_COMMAND, Service.RECONNECT)
            );
        }
    };


    private BroadcastReceiver messagingServiceCommandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            connection.executeCommand(intent);
        }
    };
    private boolean scheduled = false;

    public static void sendCommand(Context context, int command) {
        context.sendBroadcast(
                new Intent(Service.SERVICE_COMMAND)
                        .putExtra(Service.SERVICE_COMMAND, command)
        );

    }

    @Override
    public void onCreate() {
        HandlerThread connectionThread = new HandlerThread("mqttConnection", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        connectionThread.start();

        connection = new Connection(connectionThread.getLooper(), this);

        registerReceiver(networkStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(messagingServiceCommandReceiver, new IntentFilter(Service.SERVICE_COMMAND));

    }

    @Override
    public void onDestroy() {
        // todo: implement connection disconnect
        unregisterReceiver(networkStatusReceiver);
        unregisterReceiver(messagingServiceCommandReceiver);
        super.onDestroy();
    }

    public void onConnectionLost() {
        // todo: do something smarter about onConnectionLost attempts
        Log.e(TAG, "Connection failure");
        if (networkAvailable) {
            if (scheduled) {
                Log.e(TAG, "Retry already scheduled");
                return;
            }
            scheduled = true;
            Log.e(TAG, String.format("Connection failure. Reconnecting in %d sec.", retry_interval/1000));
            reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        connection.connectIfNecessary();
                        scheduled = false;
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
