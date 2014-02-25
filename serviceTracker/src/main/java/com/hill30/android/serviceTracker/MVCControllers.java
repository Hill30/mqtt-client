package com.hill30.android.serviceTracker;

import android.webkit.WebView;

import com.hill30.android.mqttClient.ServiceConnection;
import com.hill30.android.serviceTracker.common.Application;
import com.hill30.android.serviceTracker.entities.ActivityRecordMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by mfeingol on 2/24/14.
 */
public class MVCControllers {

    private final ServiceConnection serviceConnection;
    private HashMap<Integer, ActivityRecordMessage> records = new HashMap<Integer, ActivityRecordMessage>();
    private int nextId;
    private Application application;

    public MVCControllers(Application application, final WebView webView) {

        this.application = application;

        serviceConnection = new ServiceConnection(webView.getContext(), this.application.messagingServicePreferences().getUrl(), this.application.messagingServicePreferences().getUsername(), this.application.messagingServicePreferences().getPassword(), "ServiceTracker",
                new ServiceConnection.MessageListener() {
                    @Override
                    public void onMessageArrived(String message) {
                        try {
                            final ActivityRecordMessage activityRecord = new ActivityRecordMessage(nextId, new JSONObject(message));
                            records.put(nextId, activityRecord);
                            nextId += 1;
                            webView.loadUrl("javascript:WebApi.NotificationService.newRecord(\'" + activityRecord.toJSON().toString() + "\')");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public String get(String url) throws Exception {
        String[] tokens = url.split("/");
        if (tokens[0].equals("activities")) {
            JSONArray result = new JSONArray();
            for (HashMap.Entry<Integer, ActivityRecordMessage> record : records.entrySet()) {
                result.put(record.getValue().toJSON());
            }
            return result.toString();
        }
        if (tokens[0].equals("activity")) {
            if (tokens.length < 2)
                throw new Exception("Invalid REST request: activityRecord id missing");
            return records.get(Integer.parseInt(tokens[1])).getPayload().toString();
        }
        throw new Exception("Invalid REST request: unknown controller '" + tokens[0] + "'");
    }

    public String post(String url, String post) throws Exception {
        String[] tokens = url.split("/");
        if (tokens[0].equals("activity")) {
            if (tokens.length < 2)
                throw new Exception("Invalid REST request: activityRecord id missing");
            records.get(Integer.parseInt(tokens[1])).setPayload(new JSONObject(post));
            serviceConnection.send(post);
            return post;
        }
        throw new Exception("Invalid REST request: unknown controller '" + tokens[0] + "'");
    }
}
