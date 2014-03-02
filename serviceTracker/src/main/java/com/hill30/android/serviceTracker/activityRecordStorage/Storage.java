package com.hill30.android.serviceTracker.activityRecordStorage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hill30.android.mqttClient.ServiceConnection;
import com.hill30.android.serviceTracker.common.Application;
import com.hill30.android.serviceTracker.entities.ActivityRecordMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Storage extends Service {

    public HashMap<Integer, ActivityRecordMessage> records = new HashMap<Integer, ActivityRecordMessage>();
    private int nextId = 0;
    private StorageBinder storageBinder;
    private ServiceConnection serviceConnection;

    private Application application() {
        return (Application)getApplication();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        storageBinder = new StorageBinder(this);

        serviceConnection = new ServiceConnection(this, "ServiceTracker",
                new ServiceConnection.MessageListener() {

                    @Override
                    public void onMessageArrived(String message) {
                        try {
                            final ActivityRecordMessage activityRecord = new ActivityRecordMessage(nextId, new JSONObject(message));
                            records.put(nextId, activityRecord);
                            storageBinder.onNewActivityRecord(nextId, activityRecord);
                            nextId += 1;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return storageBinder;
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
        }
    }
}
