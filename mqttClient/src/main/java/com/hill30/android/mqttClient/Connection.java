package com.hill30.android.mqttClient;

import android.content.res.AssetManager;
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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class Connection extends Handler
{
    public static final String TAG = "MQTT Connection";
    private MqttAsyncClient mqttClient;
    private HashMap<String, ConnectionBinder> recipients = new HashMap<String, ConnectionBinder>();
    private MessageStash stash;
    private Service service;
    private boolean connecting;
    private Object synchLock = new Object();
    private String brokerUrl = null;
    private String userName;
    private String password;

    // todo: exception processing/reporting. Also applies to all other places with printStackTrace

    public Connection(Looper looper, final Service service) {
        super(looper);
        this.service = service;
        stash = new MessageStash(service.getApplicationContext().getFilesDir().getPath());
    }

    public void connect(final ConnectionBinder connectionBinder, String brokerUrl, String userName, String password, final String topic) throws MqttException {
        this.brokerUrl = brokerUrl;
        this.userName = userName;
        this.password = password;

        recipients.put(topic, connectionBinder);
        if (connectIfNecessary())
            subscribe(topic);
    }

    public boolean connectIfNecessary() throws MqttException {

        // There was no call to connect yet - we do not know what to connect to
        if (brokerUrl == null)
            return false;

        synchronized (synchLock) {

            MqttConnectOptions connectionOptions = new MqttConnectOptions();
            connectionOptions.setCleanSession(false);
            connectionOptions.setUserName(userName);
            connectionOptions.setPassword(password.toCharArray());

            // setup SSL properties
            String sslClientStore =  this.service.getString(R.string.SSLClientStore);
            if(!sslClientStore.isEmpty()){
                String sslClientStorePswd =  this.service.getString(R.string.SSLClientStorePswd);
                connectionOptions.setSSLProperties( getSSLProperties("","",sslClientStore, sslClientStorePswd) );
            }

            if (mqttClient == null) {

                String appPath = service.getApplicationContext().getFilesDir().getPath();

                mqttClient = new MqttAsyncClient(
                        brokerUrl,
                        userName,
                        new MqttDefaultFilePersistence(appPath)
                );
                Log.d(TAG, "Broker URL: " + brokerUrl);
                Log.d(TAG, "Connection clientId: " + userName);
                Log.d(TAG, "Application path: " + appPath);

                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.e(TAG, "Connection lost. Cause: " + cause.toString());
                        mqttClient = null;
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

    private void subscribe(final String topic) {

        try {

            //TODO: should this be moved to after subscribe-success?
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
                // it seems likely that REASON_CODE_CLIENT_DISCONNECTING should also be here
                // I am not 100% sure, but I've seen a message 'Publish of blah failed ' with this reason code
                case MqttException.REASON_CODE_CLIENT_NOT_CONNECTED:
                    stash.put(topic, message);   // stash it for when the connection comes online;
                    break;
                default:
                    Log.d(TAG, "Publish of " + message + " failed :" + e.toString());
                    break;
            }
        }
    }

    private Properties getSSLProperties(String keyStoreFile, String keyStorePswd, String trustStoreFile, String trustStorePswd )
    {
        // Key and Trust stored are copied from Assets to make access to their paths.
        // These Key and Trust store paths are set as connection SSL properties.
        // Key store - for server authenticating client. This is NOT default setting on the broker
        // Trust store - for client authenticating server. This is a must, default setting for SSL.

        final Properties properties = new Properties();

        if(!keyStoreFile.isEmpty()) {
            String keyStoreFileName = copyFileFromAssetsToAppDirectory(keyStoreFile);
            properties.setProperty("com.ibm.ssl.keyStore",keyStoreFileName);
            properties.setProperty("com.ibm.ssl.keyStorePassword", keyStorePswd);
            Log.d(TAG, "Key store file: " + keyStoreFileName);
        }
        String trustStoreFileName = copyFileFromAssetsToAppDirectory(trustStoreFile);
        properties.setProperty("com.ibm.ssl.trustStore", trustStoreFileName);
        properties.setProperty("com.ibm.ssl.trustStorePassword", trustStorePswd);
        Log.d(TAG, "Trust store file: " + trustStoreFileName);

        return properties;
    }

    private String copyFileFromAssetsToAppDirectory(String filename) {
        try {
            AssetManager assetManager = this.service.getBaseContext().getAssets();

            InputStream in = assetManager.open(filename);
            String newFilePathName = "/data/data/" + this.service.getBaseContext().getPackageName() + "/" + filename;
            OutputStream out = new FileOutputStream(newFilePathName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            Log.d(TAG, "New file: " + newFilePathName );

            in.close();
            out.flush();
            out.close();

            return newFilePathName;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return "";
        }
    }

}
