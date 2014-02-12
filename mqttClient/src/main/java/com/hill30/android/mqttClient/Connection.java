package com.hill30.android.mqttClient;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class Connection extends Handler
{
    public static final String TAG = "MQTT Connection";
    private final MqttConnectOptions connectionOptions;
    private final MqttAsyncClient mqttClient;
    private HashMap<String, ConnectionBinder> recipients = new HashMap<String, ConnectionBinder>();
    private MessageStash stash;
    private Service service;
    private String userName;
    private boolean connecting;

    // todo: exception processing/reporting. Also applies to all other places with printStackTrace

    public Connection(Looper looper, final Service service, String brokerUrl, String userName, String password) throws MqttException {
        super(looper);

        this.service = service;
        this.userName = userName;

        stash = new MessageStash(service.getApplicationContext().getFilesDir().getPath());

        connectionOptions = new MqttConnectOptions();
        connectionOptions.setCleanSession(false);
        connectionOptions.setUserName(userName);
        connectionOptions.setPassword(password.toCharArray());

        String clientId = Settings.Secure.getString(service.getContentResolver(), Settings.Secure.ANDROID_ID);
        String appPath = service.getApplicationContext().getFilesDir().getPath();

        mqttClient = new MqttAsyncClient(
                brokerUrl,
                clientId,
                new MqttDefaultFilePersistence(appPath)
        );
        Log.d(TAG, "Broker URL: " + brokerUrl);
        Log.d(TAG, "Connection clientId: " + clientId);
        Log.d(TAG, "Application path: " + appPath);

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "Connection lost. Cause: " + cause.toString());
                service.onConnectFailure();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                ConnectionBinder recipient = recipients.get(topic);
                if (recipient != null)
                    recipient.onMessageReceived(message.toString());
                Log.d(TAG, "Message " + message + " received");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "Message delivery complete");
            }
        });

    }

    private void subscribe(final String topic) {

        try {

            //TODO: should this move to after subscribe-success?
            for (MessageStash.Message message : stash.get()) {
                send(message.topic, message.body);
                message.commit();
            }

            mqttClient.subscribe(topic, 2, // QoS = EXACTLY_ONCE
                    null,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                            Log.d(TAG, "Successfully subscribed to " + topic);
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                            Log.e(TAG, "Subscribe to " + topic + " failed: " + throwable.toString());
                        }
                    });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect(final ConnectionBinder connectionBinder, final String topic) throws MqttException {
        recipients.put(topic, connectionBinder);
        if (connectIfNecessary())
            subscribe(topic);
    }

    public boolean connectIfNecessary() throws MqttException {

        synchronized (mqttClient) {
            if(mqttClient.isConnected()) // connection is already active - we can subscribe to the topic synchronously (see connect method)
                return true;

            if (connecting) // connecting was earlier initiated from a different thread - just let things take their course
                return false;
            connecting = true;

            mqttClient.connect(connectionOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    connecting = false;
                    Log.d(TAG, "connected");
                    for (Map.Entry<String, ConnectionBinder> binder : recipients.entrySet())
                        subscribe(binder.getKey());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    connecting = false;
                    // todo: onConnectFailure only on recoverable exceptions
                    Log.e(TAG, "Failed to connect :" + exception.toString());
                    service.onConnectFailure();
                }
            });
            return false;
        }
    }

    public void unregisterSubscriber(String topic) {
        recipients.remove(topic);
    }

    public void send(String topic, String message) {
        try {
            mqttClient.publish(topic, message.getBytes(), 2, true);
            Log.d(TAG, "published :" + message);
        } catch (MqttException e) {
            switch (e.getReasonCode()) {
                // todo: double check this is the only recoverable failure
                case MqttException.REASON_CODE_CLIENT_NOT_CONNECTED:
                    stash.put(topic, message);   // stash it for when the connection comes online;
                    break;
                default:
                    Log.d(TAG, "Publish of " + message + " failed :" + e.toString());
                    break;
            }
        }
    }

    public String getInboundTopic(String topic) {
        return topic + "/Inbound/" + userName;
    }

    public String getOutboundTopic(String topic) {
        return topic + "/Outbound";
    }
}
