package com.hill30.android.serviceTracker.common;

import com.hill30.android.serviceTracker.common.database.DBConnectionPool;
import com.hill30.android.serviceTracker.persistance.factories.ActivityRecordMessageFactory;
import com.hill30.android.serviceTracker.persistance.interfaces.IActivityRecordMessageFactory;

/**
 * Created by azavarin on 2/20/14.
 */
public class ApplicationGlobals {

    private Application application;
    private IActivityRecordMessageFactory activityRecordMessageFactory;

    public IActivityRecordMessageFactory activityRecordMessageFactory(){
        if(activityRecordMessageFactory == null){
            activityRecordMessageFactory = new ActivityRecordMessageFactory();
        }
        return activityRecordMessageFactory;
    }


    public Application application() { return application; }

    public ApplicationGlobals(Application application) {
        this.application = application;
        new DBConnectionPool(application, this);
    }

}
