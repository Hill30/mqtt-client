package com.hill30.android.serviceTracker;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mfeingol on 2/13/14.
 */
public class WebViewFragment extends Fragment {

    public static final String TAG = WebViewFragment.class.getName();

    private WebView webView;

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

        return view;
    }

    public class WebApi {
        public String get(String url) {
            JSONArray jsonObject = new JSONArray();
            try {
                jsonObject.put(new JSONObject("{'name':'branch 1'}"));
                jsonObject.put(new JSONObject("{'name':'branch 2'}"));
                jsonObject.put(new JSONObject("{'name':'branch 3'}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }
    }

}
