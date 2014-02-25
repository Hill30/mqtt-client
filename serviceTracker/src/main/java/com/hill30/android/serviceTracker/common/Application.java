package com.hill30.android.serviceTracker.common;

import com.hill30.android.serviceTracker.persistence.interfaces.IActivityRecordMessageFactory;
import com.hill30.android.serviceTracker.persistence.preferences.MessagingServicePreferences;

/**
 * Created by azavarin on 2/20/14.
 */
public class Application extends android.app.Application {

    private ApplicationGlobals applicationGlobals;

    public Application() {

    }

    public ApplicationGlobals getGlobals() {
        if (applicationGlobals == null)
            applicationGlobals = new ApplicationGlobals(this);
        return applicationGlobals;
    }

    public IActivityRecordMessageFactory activityRecordMessageFactory(){ return getGlobals().activityRecordMessageFactory(); }
    public MessagingServicePreferences messagingServicePreferences(){ return getGlobals().messagingServicePreferences(); }

}
