package com.hill30.android.serviceTracker.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.hill30.android.serviceTracker.common.Application;

public abstract class BasePreferencesStorage {

    protected abstract String getPrefsName();


    protected final SharedPreferences mPref;
    private Application application;

    public BasePreferencesStorage(final Application application) {
        this.application = application;
        mPref = application.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    protected SharedPreferences getPreferences() {
        return application.getSharedPreferences(getPrefsName(), Context.MODE_PRIVATE);
    }

}
