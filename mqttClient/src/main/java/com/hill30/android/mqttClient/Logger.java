package com.hill30.android.mqttClient;

public class Logger implements ILogger {

    private static Logger logger = new Logger();

    public static ILogger getLogger() {
        return logger;
    }

    @Override
    public void log(String message) {

    }

    @Override
    public void log(String message, Throwable throwable) {

    }
}
