package com.hill30.android.mqttClient;

import android.os.RemoteException;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class ConnectionBinder extends IConnection.Stub {
    private final Connection connection;
    private String topic;

    public ConnectionBinder(Connection connection, String topic) {
        this.connection = connection;
        this.topic = topic;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public void connect() throws RemoteException {
        try {
            connection.connect(topic);
            connection.register(this);
            linkToDeath(new DeathRecipient() {
                @Override
                public void binderDied() {
                    connection.unRegister(ConnectionBinder.this);
                }
            }, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(String message) {

    }
}
