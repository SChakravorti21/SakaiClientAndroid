package com.example.development.sakaiclientandroid.fragments.assignments;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
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

public class AssignmentSubmissionDialogFragment extends BottomSheetDialogFragment {
    public static final String URL_PARAM = "URL_PARAM";

    private String url;

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
        webView.initialize(this);
        webView.setDownloadListener(new AttachmentDownloadListener(getActivity()));
        webView.loadUrl(url);
    }
}
