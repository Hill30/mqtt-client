package com.hill30.android.mqttClient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notification {

    public static final int STATUS_CONNECTED = 1;
    public static final int STATUS_DISCONNECTED = 2;
    public static final int STATUS_CONNECTING = 3;
    private final NotificationCompat.Builder notificationBuilder;
    private static final int notificationId = 1;
    private Context context;

    public Notification(Context context) {
        this.context = context;

        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.network_connecting_icon);
        notificationBuilder.setContentTitle("Title");
        notificationBuilder.setContentText("Some Text");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pi);

        showNotification();

    }

    private void showNotification() {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, notificationBuilder.build());
    }

    public void updateStatus(int status) {
        switch (status) {
            case STATUS_CONNECTED:
                notificationBuilder.setSmallIcon(R.drawable.network_online_icon);
                break;
            case STATUS_DISCONNECTED:
                notificationBuilder.setSmallIcon(R.drawable.network_unreachable_icon);
                break;
            case STATUS_CONNECTING:
                notificationBuilder.setSmallIcon(R.drawable.network_reconnecting_icon);
                break;
            default:
                notificationBuilder.setSmallIcon(R.drawable.network_connecting_icon);
                break;
        }
        showNotification();
    }
}
