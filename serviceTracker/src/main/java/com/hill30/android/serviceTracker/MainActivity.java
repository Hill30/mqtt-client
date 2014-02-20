package com.hill30.android.serviceTracker;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.hill30.android.mqttClient.*;
import com.hill30.android.serviceTracker.common.Application;

public class MainActivity extends ActionBarActivity {

    private ServiceConnection serviceConnection;

    private Application application(){
        return (Application) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //AZ: just touch the factory to invoke migration
        // this code should be removed as soon as get real DB interaction
        application().activityRecordMessageFactory().getAllEntities();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setDisplayShowTitleEnabled(false);
        new ActionBarTab(this, "Native", NativeView.class.getName());
        new ActionBarTab(this, "Web", WebViewFragment.class.getName());

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
