package com.hill30.android.serviceTracker.common;

import com.hill30.android.serviceTracker.common.database.DBConnectionPool;
import com.hill30.android.serviceTracker.persistence.factories.ActivityRecordMessageFactory;
import com.hill30.android.serviceTracker.persistence.interfaces.IActivityRecordMessageFactory;
import com.hill30.android.mqttClient.MessagingServicePreferences;

/**
 * Created by azavarin on 2/20/14.
 */
public class ApplicationGlobals {

    private Application application;
    private IActivityRecordMessageFactory activityRecordMessageFactory;
    private MessagingServicePreferences messagingServicePreferences;

    public IActivityRecordMessageFactory activityRecordMessageFactory(){
        if(activityRecordMessageFactory == null){
            activityRecordMessageFactory = new ActivityRecordMessageFactory();
        }
        return activityRecordMessageFactory;
    }

    public MessagingServicePreferences messagingServicePreferences(){
        if(messagingServicePreferences == null){
            messagingServicePreferences = new MessagingServicePreferences(application);
        }
        return messagingServicePreferences;
    }


    public Application application() { return application; }

    public ApplicationGlobals(Application application) {
        this.application = application;
        new DBConnectionPool(application, this);
    }

}
