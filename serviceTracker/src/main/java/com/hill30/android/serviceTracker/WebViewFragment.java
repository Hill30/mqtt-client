package com.hill30.android.serviceTracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hill30.android.mqttClient.ServiceConnection;
import com.hill30.android.serviceTracker.entities.ActivityRecordMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mfeingol on 2/13/14.
 */
public class WebViewFragment extends Fragment {

    public static final String TAG = WebViewFragment.class.getName();

    private WebView webView;

    private Integer nextId = 0;
    private HashMap<Integer, ActivityRecordMessage> records = new HashMap<Integer, ActivityRecordMessage>();
    private BroadcastReceiver broadcastReceiver;

    public WebViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);


        webView = (WebView) view.findViewById(R.id.webView);
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
                Toast.makeText(getActivity(), "Navigating to " + url, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        webView.addJavascriptInterface(new WebApi(), "WebApi");
        webView.loadUrl("file:///android_asset/application/index.html");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String payload = intent.getStringExtra(ServiceConnection.MESSAGE_PAYLOAD);
                try {
                    ActivityRecordMessage activityRecord = new ActivityRecordMessage(nextId, new JSONObject(payload));
                    records.put(nextId, activityRecord);
                    nextId += 1;
                    webView.loadUrl("javascript:WebApi.NotificationService.newRecord(\'" + activityRecord.toJSON().toString() + "\')");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(ServiceConnection.MESSAGE_ARRIVED));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    public class WebApi {
        public String get(String url) throws Exception {
            String[] tokens = url.split("/");
            if (tokens[0].equals("activities")) {
                JSONArray result = new JSONArray();
                for (HashMap.Entry<Integer, ActivityRecordMessage> record : records.entrySet()) {
                    result.put(record.getValue().toJSON());
                }
                return result.toString();
            }
            if (tokens[0].equals("activity")) {
                if (tokens.length < 2)
                    throw new Exception("Invalid REST request: activityRecord id missing");
                return records.get(Integer.parseInt(tokens[1])).getPayload().toString();
            }
            throw new Exception("Invalid REST request: unknown controller '" + tokens[0] + "'");
        }

        public String post(String url, String post) throws Exception {
            String[] tokens = url.split("/");
            if (tokens[0].equals("activity")) {
                JSONObject jsonObject = new JSONObject(post);
                return jsonObject.toString();
            }
            throw new Exception("Invalid REST request: unknown controller '" + tokens[0] + "'");
        }
    }

}
