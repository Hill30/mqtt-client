package com.hill30.android.mqttClient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MessagingServicePreferences {

    private static final String URL = "com.hill30.android.mqttClient.MessagingServicePreferences.URL";
    private static final String USERNAME = "com.hill30.android.mqttClient.MessagingServicePreferences.USERNAME";
    private static final String PASSWORD = "com.hill30.android.mqttClient.MessagingServicePreferences.PASSWORD";
    private static final String PREFERENCES = "com.hill30.android.mqttClient.MessagingServicePreferences";

    private Application application;

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
