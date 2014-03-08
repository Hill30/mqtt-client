package com.hill30.android.serviceTracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hill30.android.mqttClient.SettingsActivity;
import com.hill30.android.serviceTracker.MVCControllers;
import com.hill30.android.serviceTracker.R;
import com.hill30.android.serviceTracker.activityRecordStorage.StorageConnection;
import com.hill30.android.serviceTracker.common.Application;
import com.hill30.android.serviceTracker.entities.ActivityRecordMessage;

import org.json.JSONException;

public class Container extends ActionBarActivity {

    private static final String TAG = "**WEBVIEW**";

    private WebView webView;
    private StorageConnection storageConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        webView = (WebView) findViewById(R.id.webView);
        webView.requestFocus();

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, "Console: " + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, "Console: " + message);
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.d(TAG, "loading: " + url);
                super.onLoadResource(view, url);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, "onReceivedError: " + description);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading" + url);
                return true;
            }
        });

        storageConnection = new StorageConnection(this, new StorageConnection.MessageListener() {
            @Override
            public void onNewActivityRecord(int id, final ActivityRecordMessage activityRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        webView.loadUrl("javascript:WebApi.NotificationService.newRecord(\'" + activityRecord.toJSON().toString() + "\')");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            }
        });

        webView.addJavascriptInterface(new MVCControllers(storageConnection), "WebApi");

    }

    @Override
    protected void onResume() {
        super.onResume();
        storageConnection.bind();
        webView.loadUrl("file:///android_asset/application/index.html");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (storageConnection != null) {
            storageConnection.unbind();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.container, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(Container.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
