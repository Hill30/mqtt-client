package com.hill30.android.mqttClient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends Activity {

    private EditText txtUrl;
    private EditText txtUsername;
    private EditText txtPassword;
    private MessagingServicePreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = new MessagingServicePreferences(getApplication());

        txtUrl = (EditText) findViewById(R.id.txtUrl);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String brokerURL = txtUrl.getText().toString().trim();
                String username = txtUsername.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                prefs.saveUrl(brokerURL);
                prefs.saveUsername(username);
                prefs.savePassword(password);

                Service.sendCommand(SettingsActivity.this, Service.RECONNECT);

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!prefs.isValid()){
            txtUrl.setText("tcp://217.119.26.211:1883");
            txtUsername.setText("userName");
            txtPassword.setText("password");
        } else {
            txtUrl.setText(prefs.getUrl());
            txtUsername.setText(prefs.getUsername());
            txtPassword.setText(prefs.getPassword());
        }
    }
}
