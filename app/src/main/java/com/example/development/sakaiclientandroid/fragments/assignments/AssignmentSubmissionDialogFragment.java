package com.example.development.sakaiclientandroid.fragments.assignments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.utils.ui_components.webview.AttachmentDownloadListener;
import com.example.development.sakaiclientandroid.utils.ui_components.webview.FileCompatWebView;

import java.lang.ref.WeakReference;

public class AssignmentSubmissionDialogFragment extends BottomSheetDialogFragment {
    public static final String URL_PARAM = "URL_PARAM";

    private String url;
    private WeakReference<FileCompatWebView> webView;
    private AttachmentDownloadListener attachmentDownloadListener;

    public AssignmentSubmissionDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        url = arguments.getString(URL_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignment_submission_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FileCompatWebView webView = view.findViewById(R.id.assignment_submission_view);
        this.webView = new WeakReference<>(webView);
        this.attachmentDownloadListener = new AttachmentDownloadListener(this);

        webView.initialize(this);
        webView.setDownloadListener(attachmentDownloadListener);
        webView.loadUrl(url);
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

        if(permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            attachmentDownloadListener.retryDownloadFile();
        }
    }
}
