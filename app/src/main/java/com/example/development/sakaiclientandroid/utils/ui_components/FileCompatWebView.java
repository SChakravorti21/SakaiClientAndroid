package com.example.development.sakaiclientandroid.utils.ui_components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.lang.ref.WeakReference;

/**
 * Created by Development on 7/21/18.
 */

public class FileCompatWebView extends WebView {

    private static final int FILE_REQUEST_CODE = 28374; // some random number

    private WeakReference<Fragment> parentFragment;
    private ValueCallback<Uri> valueCallbackCompat;
    private ValueCallback<Uri[]> valueCallback;

    // Mandatory constructors
    public FileCompatWebView(Context context) {
        super(context);
    }

    public FileCompatWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FileCompatWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public FileCompatWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Separate initialize method to prevent interfering with default constructors
    public void initialize() {
        this.initializeSettings();
        this.initializeWebViewClient();
        this.initializeWebChromeClient();
    }

    public void initialize(Fragment fragment) {
        this.initialize();
        this.parentFragment = new WeakReference<>(fragment);
    }

    private void initializeSettings() {
        WebSettings settings = this.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
    }

    private void initializeWebViewClient() {
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void initializeWebChromeClient() {
        this.setWebChromeClient(new WebChromeClient() {

            // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileInput(uploadMsg, null);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                return openFileInput(null, filePathCallback);
            }
        });
    }

    private boolean openFileInput(final ValueCallback<Uri> valueCallbackCompat,
                               final ValueCallback<Uri[]> valueCallback) {

        // Invalidate the value callbacks if any still exist
        if(this.valueCallbackCompat != null) {
            this.valueCallbackCompat.onReceiveValue(null);
        }
        this.valueCallbackCompat = valueCallbackCompat;

        if(this.valueCallback != null) {
            this.valueCallback.onReceiveValue(null);
        }
        this.valueCallback = valueCallback;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        Intent chooserIntent = Intent.createChooser(intent, "Choose a file");

        if(parentFragment != null && parentFragment.get() != null) {
            parentFragment.get().startActivityForResult(chooserIntent, FILE_REQUEST_CODE);

            // Intent was handled successfully
            return true;
        }

        // Intent could not be handled
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode != FILE_REQUEST_CODE
                || resultCode != Activity.RESULT_OK
                || intent == null) {
            return;
        }

        if(valueCallbackCompat != null) {
            valueCallbackCompat.onReceiveValue(intent.getData());
            valueCallbackCompat = null;
        } else if (valueCallback != null) {
            Uri[] fileUris = null;

            if(intent.getDataString() != null) {
                try {
                    fileUris = new Uri[] { Uri.parse(intent.getDataString()) };
                } catch (Exception exception) { }
            }

            valueCallback.onReceiveValue(fileUris);
            valueCallback = null;
        }
    }
}
