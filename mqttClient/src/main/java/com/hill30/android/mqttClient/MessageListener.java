package com.hill30.android.mqttClient;

/**
 * Created by mfeingol on 2/6/14.
 */
public interface MessageListener<T> {
    void onMessageArrived(T message);
}
