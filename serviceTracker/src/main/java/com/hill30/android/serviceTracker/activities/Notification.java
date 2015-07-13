package com.hill30.android.serviceTracker.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.hill30.android.mqttClient.ServiceConnection;

public class Notification {


    private final NotificationCompat.Builder notificationBuilder;
    private static final int notificationId = 1;
    private Context context;

    public Notification(Context context) {
        this.context = context;

        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(com.hill30.android.mqttClient.R.drawable.network_connecting_icon);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pi);

//        showNotification();

    }

    private void showNotification() {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, notificationBuilder.build());
    }

    public void updateStatus(int status) {
        notificationBuilder.setContentTitle("Connection state changed.");
        switch (status) {
            case ServiceConnection.CONNECTION_STATUS_CONNECTED:
                notificationBuilder.setSmallIcon(com.hill30.android.mqttClient.R.drawable.network_online_icon);
                notificationBuilder.setContentText("MQ Connected");

                break;
            case ServiceConnection.CONNECTION_STATUS_DISCONNECTED:
                notificationBuilder.setSmallIcon(com.hill30.android.mqttClient.R.drawable.network_unreachable_icon);
                notificationBuilder.setContentText("MQ Disconnected");
                break;
            case ServiceConnection.CONNECTION_STATUS_CONNECTING:
                notificationBuilder.setSmallIcon(com.hill30.android.mqttClient.R.drawable.network_reconnecting_icon);
                notificationBuilder.setContentText("MQ Connecting");
                break;
            default:
                notificationBuilder.setSmallIcon(com.hill30.android.mqttClient.R.drawable.network_connecting_icon);
                notificationBuilder.setContentText("MQ Connecting");
                break;
        }
        showNotification();
    }
}
