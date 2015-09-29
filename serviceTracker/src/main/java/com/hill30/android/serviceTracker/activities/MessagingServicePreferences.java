package com.hill30.android.serviceTracker.activities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MessagingServicePreferences {

    private static final String URL = "com.hill30.android.serviceTracker.activities.MessagingServicePreferences.URL";
    private static final String USER_ID = "com.hill30.android.serviceTracker.activities.MessagingServicePreferences.USER_ID";
    private static final String CLIENT_ID = "com.hill30.android.serviceTracker.activities.MessagingServicePreferences.CLIENT_ID";
    private static final String BROKER_USERNAME = "com.hill30.android.serviceTracker.activities.MessagingServicePreferences.BROKER_USERNAME";
    private static final String BROKER_PASSWORD = "com.hill30.android.serviceTracker.activities.MessagingServicePreferences.BROKER_PASSWORD";
    private static final String PREFERENCES = "com.hill30.android.serviceTracker.activities.MessagingServicePreferences";

    private Application application;
    private String clientId;

    public MessagingServicePreferences(Application application) {
        this.application = application;
    }

    protected SharedPreferences getPreferences() {
        return application.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
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
        return getPreferences().getString(BROKER_USERNAME, null);
    }

    public void saveUsername(String username){
        getPreferences().edit().putString(BROKER_USERNAME, username).commit();
    }

    public String getPassword(){
        return getPreferences().getString(BROKER_PASSWORD, null);
    }

    public void savePassword(String password){
        getPreferences().edit().putString(BROKER_PASSWORD, password).commit();
    }

    public String getUserId(){
        return getPreferences().getString(USER_ID, null);
    }

    public void saveUserId(String userId){
        getPreferences().edit().putString(USER_ID, userId).commit();
    }


    public String getClientId() {
        return getPreferences().getString(CLIENT_ID, null);
    }

    public void saveClientId(String clientId) {
        getPreferences().edit().putString(CLIENT_ID, clientId).commit();
    }
}
