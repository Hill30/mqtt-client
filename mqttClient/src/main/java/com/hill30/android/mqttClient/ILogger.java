package com.hill30.android.mqttClient;

public interface ILogger {
    public void log(String message);
    public void log(String message, Throwable throwable);
}
