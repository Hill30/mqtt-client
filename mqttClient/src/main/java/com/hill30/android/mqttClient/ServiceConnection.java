package com.hill30.android.mqttClient;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

/**
 *  Provides external API to interact with the messaging service.
 *
 *  To use create an instance of the ServiceConnection and supply it with a callback to
 *  be called when a message is arrived (if necessary). As soon as an instance of the service
 *  connection is created it starts listening for incoming messages on the inbound topic
 *  messages. The inbound topic name has the following format: {topic}/inbound/{user} where {topic}
 *  is the value of the topic parameter passed to the constructor, and the {user} is the name
 *  of the currently logged on user.
 *
 *  To send a message to the outbound topic use send method of the instance created.
 *  The name of the outbound topic has the following format: {topic}/outbound where
 *  topic is the value of the topic parameter passed to the constructor
 *
 *  Method close() have to be called to release the resources associated
 *  with the messaging service.
 *
 */
public class ServiceConnection implements android.content.ServiceConnection {
    private ConnectionBinder connectionBinder;
    private Context context;
    private MessageListener messageListener;

    /**
     * Defines callback method to be called when a new message arrives
     */
    public interface MessageListener {
        /**
         * Method to call when a new message arrives. The method will be called on
         * an internal paho thread
         * @param message - message text
         */
        void onMessageArrived(String message);
    }

    private static void startIfNecessary(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return;
            }
        }
        context.startService(new Intent(context, serviceClass));
    }

    /**
     * As soon as the instance is created it starts listening for the incoming
     * messages. Incoming messages are expected on the topic with the name of the following format:
     * {topic}/inbound/{user} where {topic} is the value of the topic parameter, and the {user}
     * is the name of the currently logged on user.
     * @param context
     * @param topic topc name to be used to listen for incoming messages as well as send out outgoing
     * @param messageListener defines the callback to be called when a message arrived
     */

    public ServiceConnection(Context context, String topic, MessageListener messageListener) {
        this.context = context;
        this.messageListener = messageListener;

        startIfNecessary(context, Service.class);

        // todo: validate topic for illegal characters - it is important for matching incoming message to the recipients (see Connection.java)
        context.bindService(
                new Intent(context, Service.class)
                        .putExtra(Service.TOPIC_NAME, topic),
                this, Context.BIND_AUTO_CREATE);
    }

    /**
     * Creates a write - only (no listener for inbound) connection
     * @param context
     * @param topic
     */

    public ServiceConnection(Context context, String topic) {
        this(context, topic, null);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        connectionBinder = (ConnectionBinder)binder;
        connectionBinder.listener(messageListener);
        try {
            connectionBinder.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        connectionBinder.disconnectBinder();
    }

    /**
     * Sends a message to the outbound topic.
     *
     * The name of the outbound topic has the following format: {topic}/outbound where
     * topic is the value of the topic parameter passed to the constructor
     *
     * @param message message body
     * @throws IOException
     */
    public void send(String message) throws IOException {
        connectionBinder.send(message);
    }

    /**
     * Releases resources associated with the connection
     */
    public void close() {
        context.unbindService(this);
    }

}
