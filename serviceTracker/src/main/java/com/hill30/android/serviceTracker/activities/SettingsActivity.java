package com.hill30.android.serviceTracker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.hill30.android.mqttClient.Service;
import com.hill30.android.serviceTracker.activityRecordStorage.Storage;

public class SettingsActivity extends Activity {

    private EditText txtUrl;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtUserId;
    private MessagingServicePreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hill30.android.mqttClient.R.layout.activity_settings);

        prefs = new MessagingServicePreferences(getApplication());

        txtUrl = (EditText) findViewById(com.hill30.android.mqttClient.R.id.txtUrl);
        txtUsername = (EditText) findViewById(com.hill30.android.mqttClient.R.id.txtUsername);
        txtPassword = (EditText) findViewById(com.hill30.android.mqttClient.R.id.txtPassword);
        txtUserId = (EditText) findViewById(com.hill30.android.mqttClient.R.id.txtUserId);

        findViewById(com.hill30.android.mqttClient.R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String brokerURL = txtUrl.getText().toString().trim();
                String username = txtUsername.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String userId = txtUserId.getText().toString().trim();

                prefs.saveUrl(brokerURL);
                prefs.saveUsername(username);
                prefs.savePassword(password);
                prefs.saveUserId(userId);

                Intent restartConnectionIntent = new Intent(Storage.RESTART_CONNECTION);
                SettingsActivity.this.sendBroadcast(restartConnectionIntent);

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!prefs.isValid()){
            txtUrl.setText("tcp://10.0.1.134:1883");
            txtUserId.setText("253012");
            txtUsername.setText("admin");
            txtPassword.setText("admin");
        } else {
            txtUrl.setText(prefs.getUrl());
            txtUsername.setText(prefs.getUsername());
            txtPassword.setText(prefs.getPassword());
            txtUserId.setText(prefs.getUserId());
        }
    }
}
