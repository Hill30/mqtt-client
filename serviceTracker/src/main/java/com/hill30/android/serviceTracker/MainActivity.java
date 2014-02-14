package com.hill30.android.serviceTracker;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hill30.android.mqttClient.*;

public class MainActivity extends ActionBarActivity {

    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setDisplayShowTitleEnabled(false);
        new ActionBarTab(this, "Native", NativeView.class.getName());
        new ActionBarTab(this, "Web", WebView.class.getName());

        serviceConnection = new ServiceConnection(this, "tcp://10.0.2.2:1883", "userName", "password", "ServiceTracker",
            new ServiceConnection.MessageListener() {
                @Override
                public void onMessageArrived(String message) {
                    Log.d("*****", "received " + message);
                }
            });

    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    public void send(String message) {
        serviceConnection.send(message);
    }
}
