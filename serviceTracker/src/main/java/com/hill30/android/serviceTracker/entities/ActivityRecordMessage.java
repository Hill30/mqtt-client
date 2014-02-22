package com.hill30.android.serviceTracker.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by azavarin on 2/20/14.
 */
public class ActivityRecordMessage extends EntityBase {
    private JSONObject payload = null;

    public ActivityRecordMessage() {}

    public ActivityRecordMessage(Integer id, JSONObject payload) {
        super();
        setId(id);
        setPayload(payload);
    }

    public JSONObject getPayload() {return payload;}

    public JSONObject toJSON() throws JSONException {
        return new JSONObject("{\"id\":" + id + ",\"payload\":" + payload.toString() + "}");
    }

    public void setPayload(JSONObject payload) {this.payload = payload;}
}
