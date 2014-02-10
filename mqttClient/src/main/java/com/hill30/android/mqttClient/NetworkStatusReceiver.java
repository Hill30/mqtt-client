package com.hill30.android.mqttClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkStatusReceiver extends BroadcastReceiver {

    public static final String INTENT_NETWORK_CONNECTED = "com.hill30.android.mqttClient.NetworkStatusReceiver.INTENT_NETWORK_CONNECTED";
    public static final String INTENT_NETWORK_DISCONNECTED = "com.hill30.android.mqttClient.NetworkStatusReceiver.INTENT_NETWORK_DISCONNECTED";

    @Override
    public void onReceive(Context context, Intent intent) {
        //AZ: this code is rather dumb and only thing that it does is simple connectivity check
        // is case we need extra info (network type, reason why there is no network) there are a few options to make it
        boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

        context.sendBroadcast(new Intent(noConnectivity ? INTENT_NETWORK_DISCONNECTED : INTENT_NETWORK_CONNECTED ));
    }
}
