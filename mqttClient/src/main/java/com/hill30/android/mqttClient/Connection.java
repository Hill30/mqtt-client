package com.hill30.android.mqttClient;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Connection extends Handler
{
    public static final String TAG = "MQTT Connection";
    private static final int QoS_EXACLY_ONCE = 2;

    private final Service service;
    private final Object synchLock = new Object();
    private final String applicationRoot;
    private final ILogger logger;

    private MqttAsyncClient mqttClient;
    private HashMap<String, ConnectionBinder> recipients = new HashMap<String, ConnectionBinder>();
    private MessageStash stash;
    private boolean connecting;
    private String brokerUrl = null;
    private String userName;
    private String password;

    // todo: exception processing/reporting. Also applies to all other places with printStackTrace

    public Connection(Looper looper, final Service service) {
        super(looper);
        logger = Logger.getLogger();
        this.service = service;
        applicationRoot = service.getApplicationContext().getFilesDir().getPath();
    }

    public IMqttAsyncClient getMqttClient() {
        return mqttClient;
    }

    private String getTopicFromInbound(String inboundTopic) {return inboundTopic.split("/Inbound/")[0]; }

    private String getInboundTopic(String topic) {
        return topic + "/Inbound/" + userName;
    }

    private String getOutboundTopic(String topic) {
        return topic + "/Outbound";
    }

    public void connect(ConnectionBinder connectionBinder, String topic, String brokerUrl, String username, final String password) throws MqttException, IOException {

        recipients.put(topic, connectionBinder);

        this.brokerUrl = brokerUrl;
        this.userName = username;
        this.password = password;

        if (connectIfNecessary())
            subscribe(topic);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what) {
            case Service.RECONNECT:
                try {

//                    if (mqttClient != null)
//                        mqttClient.close();
//                    mqttClient = null;

                    connectIfNecessary();

                } catch (MqttException e) {
                    Log.d(TAG, "Exception handling RESTART command", e);
                } catch (IOException e) {
                    Log.d(TAG, "Exception handling RESTART command", e);
                }
                break;
            default:
                Log.d(TAG, String.format("Unknown command %d", msg.what));
                break;
        }
    }

    public boolean connectIfNecessary() throws MqttException, IOException {

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

                stash = new MessageStash(applicationRoot + "/" + userName);

                mqttClient = new MqttAsyncClient(
                        brokerUrl,
                        userName,
                        new MqttDefaultFilePersistence(applicationRoot + "/" + userName)
                );
                Log.d(TAG, "Broker URL: " + brokerUrl);
                Log.d(TAG, "Connection clientId: " + userName);
                Log.d(TAG, "Application path: " + applicationRoot);

                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.e(TAG, "Connection lost. Cause: " + cause.toString());
                        service.onConnectionLost();
                        for(ConnectionBinder recipient : recipients.values()){
                            recipient.onConnectionStateChanged(ServiceConnection.CONNECTION_STATUS_DISCONNECTED);
                        }
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        ConnectionBinder recipient = recipients.get(getTopicFromInbound(topic));
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

            for(ConnectionBinder recipient : recipients.values()){
                recipient.onConnectionStateChanged(ServiceConnection.CONNECTION_STATUS_CONNECTING);
            }

            mqttClient.connect(connectionOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    connecting = false;
                    Log.d(TAG, "connected");

                    for (MessageStash.Message message : stash.get()) {
                        try {
                            send(message.topic(), message.body());
                            message.commit();
                        } catch (IOException e) {
                            // we can safely ignore it here because this code is executed after the connection is restored
                            // so there will be no need to stash the message, but even the connection will be lost while
                            // resubmitting messages here there will be no need to worry - the message will remain stashed
                            // because message.commit will not be executed
                        }
                    }

                    for (Map.Entry<String, ConnectionBinder> binder : recipients.entrySet())
                        subscribe(binder.getKey());

                    for(ConnectionBinder recipient : recipients.values()){
                        recipient.onConnectionStateChanged(ServiceConnection.CONNECTION_STATUS_CONNECTED);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    connecting = false;
                    // todo: onConnectionLost only on recoverable exceptions
                    Log.e(TAG, "Failed to connect :" + exception.toString());
                    service.onConnectionLost();
                    for(ConnectionBinder recipient : recipients.values()){
                        recipient.onConnectionStateChanged(ServiceConnection.CONNECTION_STATUS_DISCONNECTED);
                    }
                }
            });
            return false;
        }
    }

    private void subscribe(String topic) {

        final String inboundTopic = getInboundTopic(topic);

        try {

            mqttClient.subscribe(inboundTopic, QoS_EXACLY_ONCE,
                    null,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                            Log.d(TAG, "Successfully subscribed to " + inboundTopic);
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                            Log.e(TAG, "Subscribe to " + inboundTopic + " failed: " + throwable.toString());
                        }
                    });
        } catch (MqttException e) {
            logger.log(String.format("Exception subscribing to %s", inboundTopic), e);
        }
    }

    public void unregisterSubscriber(String topic) {

        String inboundTopic = getInboundTopic(topic);

        if (mqttClient.isConnected())
            try {
                mqttClient.unsubscribe(inboundTopic);
            } catch (MqttException e) {
                logger.log(String.format("Exception unsubscribing from %s", inboundTopic), e);
            }
        recipients.remove(topic);
    }

    public void send(String topic, String message) throws IOException {

        String outboundTopic = getOutboundTopic(topic);

        try {
            mqttClient.publish(outboundTopic, message.getBytes(), QoS_EXACLY_ONCE, true);
            Log.d(TAG, "published to " + outboundTopic + " :" + message);
        } catch (MqttException e) {
            switch (e.getReasonCode()) {
                // todo: double check this is the only recoverable failure
                // it seems likely that REASON_CODE_CLIENT_DISCONNECTING should also be here
                // I am not 100% sure, but I've seen a message 'Publish of blah failed ' with this reason code
                case MqttException.REASON_CODE_CLIENT_DISCONNECTING:
                case MqttException.REASON_CODE_CLIENT_NOT_CONNECTED:
                    stash.put(topic, message);   // stash it for when the connection comes back online;
                    break;
                default:
                    logger.log(String.format("Exception publishing to %s", outboundTopic), e);
                    break;
            }
        }
    }

    private Properties getSSLProperties(String keyStoreFile, String keyStorePswd, String trustStoreFile, String trustStorePswd )
    {
        // Key and Trust stores are copied from Assets to make possible access to their paths.
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
            //todo: change as recommended in the warning
            String newFilePathName = /*applicationRoot +*/ "/data/data/" + this.service.getBaseContext().getPackageName() + "/" + filename;
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

    public void executeCommand(Intent intent) {
        int command = intent.getIntExtra(Service.SERVICE_COMMAND, -1);
        switch (command) {
            case Service.RECONNECT:
                if (mqttClient != null && mqttClient.isConnected())
                    // reconnect is possible only if mqttClient is initialized but disconnected
                    return;

                sendEmptyMessage(Service.RECONNECT);
                break;
            default:
                sendEmptyMessage(command);
                break;
        }
    }

}
