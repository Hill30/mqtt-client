package com.hill30.android.serviceTracker;

import com.hill30.android.serviceTracker.activityRecordStorage.StorageConnection;
import com.hill30.android.serviceTracker.activityRecordStorage.ActivityRecordMessage;

import org.json.JSONArray;

import java.util.HashMap;

public class MVCControllers {

    private StorageConnection storageConnection;


    public MVCControllers(StorageConnection storageConnection) {

        this.storageConnection = storageConnection;
    }

    public String get(String url) throws Exception {
        String[] tokens = url.split("/");
        if (tokens[0].equals("activities")) {
            JSONArray result = new JSONArray();
            for (HashMap.Entry<Integer, ActivityRecordMessage> record : storageConnection.get()) {
                result.put(record.getValue().toJSON());
            }
            return result.toString();
        }
        if (tokens[0].equals("activity")) {
            if (tokens.length < 2)
                throw new Exception("Invalid REST request: activityRecord id missing");
            return storageConnection.get(Integer.parseInt(tokens[1])).getPayload().toString();
        }
        throw new Exception("Invalid REST request: unknown controller '" + tokens[0] + "'");
    }

    public String post(String url, String post) throws Exception {
        String[] tokens = url.split("/");
        if (tokens[0].equals("activity")) {
            if (tokens.length < 2)
                throw new Exception("Invalid REST request: activityRecord id missing");
            storageConnection.save(Integer.parseInt(tokens[1]), post);
            return post;
        }
        throw new Exception("Invalid REST request: unknown controller '" + tokens[0] + "'");
    }
}
