package com.hill30.android.serviceTracker.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hill30.android.serviceTracker.BuildConfig;
import com.hill30.android.serviceTracker.R;
import com.hill30.android.serviceTracker.common.Application;

/**
 * Created by azavarin on 2/25/14.
 */
public class SettingsActivity extends ActionBarActivity {

    private EditText txtUrl;
    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnSave;

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
        btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String brokerURL = txtUrl.getText().toString().trim();
                String username = txtUsername.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                application().messagingServicePreferences().saveUrl(brokerURL);
                application().messagingServicePreferences().saveUsername(username);
                application().messagingServicePreferences().savePassword(password);

//                sendBroadcast();



                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!application().messagingServicePreferences().isValid()){
            txtUrl.setText("tcp://10.0.2.2:1883");
            txtUsername.setText("userName");
            txtPassword.setText("password");
        } else {
            txtUrl.setText(application().messagingServicePreferences().getUrl());
            txtUsername.setText(application().messagingServicePreferences().getUsername());
            txtPassword.setText(application().messagingServicePreferences().getPassword());
        }
    }
}
