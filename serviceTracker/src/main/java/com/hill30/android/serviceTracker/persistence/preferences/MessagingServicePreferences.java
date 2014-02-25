package com.hill30.android.serviceTracker.persistence.preferences;

import com.hill30.android.serviceTracker.common.Application;

/**
 * Created by azavarin on 2/25/14.
 */
public class MessagingServicePreferences extends BasePreferencesStorage {

    private static final String URL = "com.hill30.android.serviceTracker.persistence.preferences.MessagingServicePreferences.URL";
    private static final String USERNAME = "com.hill30.android.serviceTracker.persistence.preferences.MessagingServicePreferences.USERNAME";
    private static final String PASSWORD = "com.hill30.android.serviceTracker.persistence.preferences.MessagingServicePreferences.PASSWORD";

    public MessagingServicePreferences(Application application) {
        super(application);
    }

    @Override
    protected String getPrefsName() {
        return "com.hill30.android.serviceTracker.persistence.preferences.MessagingServicePreferences";
    }

    public boolean isValid() {
        return
                (getUrl() != null && getUrl().length() > 0)
                &&
                (getUsername() != null && getUsername().length() > 0)
                &&
                (getPassword() != null && getPassword().length() > 0);
    }

    public String getUrl(){
        return getPreferences().getString(URL, null);
    }

    public void saveUrl(String url){
        getPreferences().edit().putString(URL, url).commit();
    }

    public String getUsername(){
        return getPreferences().getString(USERNAME, null);
    }

    public void saveUsername(String url){
        getPreferences().edit().putString(USERNAME, url).commit();
    }

    public String getPassword(){
        return getPreferences().getString(PASSWORD, null);
    }

    public void savePassword(String url){
        getPreferences().edit().putString(PASSWORD, url).commit();
    }


}
