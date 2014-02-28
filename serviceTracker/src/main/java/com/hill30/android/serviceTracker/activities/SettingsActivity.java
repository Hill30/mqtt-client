package com.hill30.android.serviceTracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import com.hill30.android.mqttClient.Service;
import com.hill30.android.serviceTracker.R;
import com.hill30.android.serviceTracker.common.Application;

public class SettingsActivity extends ActionBarActivity {

    private EditText txtUrl;
    private EditText txtUsername;
    private EditText txtPassword;

    private Application application(){
        return (Application) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtUrl = (EditText) findViewById(R.id.txtUrl);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String brokerURL = txtUrl.getText().toString().trim();
                String username = txtUsername.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                application().messagingServicePreferences().saveUrl(brokerURL);
                application().messagingServicePreferences().saveUsername(username);
                application().messagingServicePreferences().savePassword(password);

                sendBroadcast(
                        new Intent(Service.SERVICE_COMMAND)
                                .putExtra(Service.SERVICE_COMMAND, Service.RESTART)
                                .putExtra(Service.BROKER_URL, brokerURL)
                                .putExtra(Service.USER_NAME, username)
                                .putExtra(Service.PASSWORD, password)
                );

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!application().messagingServicePreferences().isValid()){
            txtUrl.setText("tcp://217.119.26.211:1883");
            txtUsername.setText("userName");
            txtPassword.setText("password");
        } else {
            txtUrl.setText(application().messagingServicePreferences().getUrl());
            txtUsername.setText(application().messagingServicePreferences().getUsername());
            txtPassword.setText(application().messagingServicePreferences().getPassword());
        }
    }
}
