package com.hill30.android.serviceTracker.activityRecordStorage;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.hill30.android.mqttClient.ServiceConnection;
import com.hill30.android.serviceTracker.activities.MessagingServicePreferences;
import com.hill30.android.serviceTracker.activities.SettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Storage extends Service {

    public static final String TAG = "activityRecordStorage.Storage";
    public static String RESTART_CONNECTION = " com.hill30.android.serviceTracker.activityRecordStorage.Storage.RESTART_CONNECTION";

    private static final String topic = "ServiceTracker";

    public HashMap<Integer, ActivityRecordMessage> records = new HashMap<Integer, ActivityRecordMessage>();
    private int nextId = 0;
    private StorageBinder storageBinder;
    private ServiceConnection serviceConnection;

    private MessagingServicePreferences messagingServicePreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        storageBinder = new StorageBinder(this);
        startConnection();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(serviceConnection != null)
                    serviceConnection.close();
                startConnection();
            }
        }, new IntentFilter(RESTART_CONNECTION));
    }

    public void startConnection() {
        messagingServicePreferences = new MessagingServicePreferences(getApplication());
        if(messagingServicePreferences.isValid()) {
            serviceConnection = new ServiceConnection(this, topic,
                    messagingServicePreferences.getUrl(),
                    messagingServicePreferences.getUsername(),
                    messagingServicePreferences.getPassword(),

                    new ServiceConnection.MessageListener() {

                        @Override
                        public void onMessageArrived(String message) {
                            try {
                                Log.d(TAG, "Message " + message + " arrived to serviceTracer app");
                                final ActivityRecordMessage activityRecord = new ActivityRecordMessage(nextId, new JSONObject(message));
                                records.put(nextId, activityRecord);
                                storageBinder.onNewActivityRecord(nextId, activityRecord);
                                nextId += 1;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new ServiceConnection.ConnectionStateListener() {
                        @Override
                        public void onConnectionStateChanged(int connectionState) {

                        }
                    });
        } else {
            startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return storageBinder;
    }

    @Override
    public void onDestroy() {
        serviceConnection.close();
        super.onDestroy();
    }

    public Set<Map.Entry<Integer, ActivityRecordMessage>> get() {
        return records.entrySet();
    }

    public ActivityRecordMessage get(int id) {
        return records.get(id);
    }

    public void save(int id, String payload) {
        try {
            records.get(id).setPayload(new JSONObject(payload));
            serviceConnection.send(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
