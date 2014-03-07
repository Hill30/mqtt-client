package com.hill30.android.mqttClient;

/**
 * Created by mfeingol on 3/7/14.
 */
public interface ILogger {
    public void log(String message);
    public void log(String message, Throwable throwable);
}
