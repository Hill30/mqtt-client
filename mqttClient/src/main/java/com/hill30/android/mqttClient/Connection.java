package com.hill30.android.mqttClient;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public abstract class Connection extends Handler
{
    public static final String TAG = "MQTT Connection";
    private final MqttConnectOptions connectionOptions;
    private final Service service;
    private MqttAsyncClient mqttClient;

    public Connection(Looper looper, Service service, String brokerUrl, String userName, String password) throws MqttException {
        super(looper);
        this.service = service;

        connectionOptions = new MqttConnectOptions();
        connectionOptions.setCleanSession(false);
        connectionOptions.setUserName(userName);
        connectionOptions.setPassword(password.toCharArray());

        mqttClient = new MqttAsyncClient(
                brokerUrl,
                MqttAsyncClient.generateClientId(),
                new MqttDefaultFilePersistence(service.getApplicationContext().getFilesDir().getPath())
        );

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "connection lost cause: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                onMessageReceived(topic, message.toString());
                Log.d(TAG, "message " + message + " received");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "message delivery complete");
            }
        });

    }

    public abstract void onMessageReceived(String topic, String message);

    public void subscribe(String topic) {
        final String inboundTopic = topic + ".inbound.user";
        try {
            mqttClient.subscribe(inboundTopic, 2, // QoS = EXACTLY_ONCE
                    null,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                            Log.d(TAG, "successfully subscribed to " + inboundTopic);
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                            Log.d(TAG, "subscribe to " + inboundTopic + " failed: " + throwable.toString());
                        }
                    });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect(final String topic) throws MqttException {

        boolean disconnected;
        synchronized (mqttClient) {
            disconnected = !mqttClient.isConnected();
            if (disconnected)
                mqttClient.connect(connectionOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "connected");
                        subscribe(topic);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "connect failed :" + exception.toString());
                    }
                });
        }
        if (!disconnected)
            subscribe(topic);

    }

    public void register(ConnectionBinder connectionBinder) {
        service.registerReceiver(connectionBinder);
    }

    public void unRegister(ConnectionBinder connectionBinder) {
        service.unregisterReceiver(connectionBinder);
    }
}
