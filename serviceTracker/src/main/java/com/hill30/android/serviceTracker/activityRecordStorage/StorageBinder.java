package com.hill30.android.serviceTracker.activityRecordStorage;

import android.os.Binder;

import com.hill30.android.serviceTracker.entities.ActivityRecordMessage;

import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by mfeingol on 2/27/14.
 */
public class StorageBinder extends Binder {
    private Storage storage;
    private StorageConnection.MessageListener listener;

    public StorageBinder(Storage storage) {
        this.storage = storage;
    }

    public void listener(StorageConnection.MessageListener listener) {

        this.listener = listener;
    }

    public void onNewActivityRecord(int id, ActivityRecordMessage activityRecord) {
        if (listener != null)
            listener.onNewActivityRecord(id, activityRecord);
    }

    public Set<Map.Entry<Integer, ActivityRecordMessage>> get() {
        return storage.get();
    }

    public ActivityRecordMessage get(int id) {
        return storage.get(id);
    }

    public void save(int id, JSONObject record) {
        storage.save(id, record);
    }

}
