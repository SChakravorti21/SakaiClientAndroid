package com.example.development.sakaiclientandroid.fragments;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.utils.requests.DownloadCompleteReceiver;
import com.example.development.sakaiclientandroid.utils.ui_components.webview.AttachmentDownloadListener;
import com.example.development.sakaiclientandroid.utils.ui_components.webview.FileCompatWebView;

import java.lang.ref.WeakReference;

public class WebFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String URL_PARAM = "URL_PARAM";

    private String URL;
    private WeakReference<FileCompatWebView> webView;
    private AttachmentDownloadListener attachmentDownloadListener;

    public WebFragment() {
        // Required empty public constructor
    }

    public static WebFragment newInstance(String url) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(URL_PARAM, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            URL = getArguments().getString(URL_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FileCompatWebView webView = view.findViewById(R.id.data_webview);
        this.webView = new WeakReference<>(webView);
        this.attachmentDownloadListener = new AttachmentDownloadListener(this);

        webView.initialize(this);
        webView.setDownloadListener(this.attachmentDownloadListener);
        webView.loadUrl(URL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(this.webView != null && this.webView.get() != null) {
            this.webView.get().onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode != AttachmentDownloadListener.REQUEST_WRITE_PERMISSION_CODE
                || permissions.length == 0
                || grantResults.length == 0) {
            return;
        }

        if(permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            attachmentDownloadListener.retryDownloadFile();
        } else {
            // Pop self off the stack since download cannot be completed
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
