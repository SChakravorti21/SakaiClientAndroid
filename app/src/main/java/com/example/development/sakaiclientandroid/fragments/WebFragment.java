package com.example.development.sakaiclientandroid.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
            URL = "https://nofile.io"; // getArguments().getString(URL_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web, container, false);

        FileCompatWebView webView = view.findViewById(R.id.data_webview);
        this.webView = new WeakReference<>(webView);

        webView.initialize(this);
        this.attachmentDownloadListener = new AttachmentDownloadListener(getActivity());
        webView.setDownloadListener(this.attachmentDownloadListener);
        webView.loadUrl(URL);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(this.webView != null && this.webView.get() != null) {
            this.webView.get().onActivityResult(requestCode, resultCode, intent);
        }
    }
}
