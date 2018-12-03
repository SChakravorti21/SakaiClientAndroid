package com.example.development.sakaiclient20.ui.fragments.assignments;


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

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.ui.custom_components.AttachmentDownloadListener;
import com.example.development.sakaiclient20.ui.custom_components.FileCompatWebView;

import java.lang.ref.WeakReference;

/**
 * Created by Shoumyo Chakravorti.
 *
 * A subclass of {@link BottomSheetDialogFragment} that contains a
 * {@link com.example.development.sakaiclient20.ui.custom_components.FileCompatWebView}
 * for the user to browse more details about their
 * assignment and submit it if they would like.
 *
 * @// FIXME: 7/29/18 File upload works on https://nofile.io but not sakai.rutger.edu
 */
public class AssignmentSubmissionDialogFragment extends BottomSheetDialogFragment {
    public static final String URL_PARAM = "URL_PARAM";

    /**
     * The URL for the assignment submission {@link android.webkit.WebView},
     * which comes from {@link com.example.development.sakaiclient20.persistence.entities.Assignment#entityURL}.
     */
    private String url;

    /**
     * A {@link WeakReference} to the view that displays the assignment content.
     */
    private WeakReference<FileCompatWebView> webView;

    /**
     * Mandatory empty constructor
     */
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

        // Get the WebView and initialize it with the necessary URL
        FileCompatWebView webView = view.findViewById(R.id.assignment_submission_view);
        this.webView = new WeakReference<>(webView);

        // Initializes the WebView settings, WebViewClient, WebChromeClient,
        // and AttachmentsDownloadListener
        webView.initialize(this);
        webView.loadUrl(url);
    }

    /**
     * Listens for results from other {@link android.app.Activity}s. For this
     * submission {@link android.support.v4.app.Fragment}, the only result that is
     * meaningful is a file upload. The work of this method is offloaded
     * to the {@link FileCompatWebView}, as that contains the {@link android.webkit.ValueCallback}s
     * that allow anything to be uploaded to the WebView.
     * @param requestCode the request code
     * @param resultCode the status of the request
     * @param intent the {@link Intent} that initiated the content request
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(this.webView != null && this.webView.get() != null) {
            this.webView.get().onActivityResult(requestCode, resultCode, intent);
        }
    }

    /**
     * Called when permissions are requested by the {@link android.support.v4.app.Fragment}
     * (this is done on behalf of this {@code Fragment} through the
     * {@link FileCompatWebView} if downloading an attachment fails as a result of
     * missing permissions to write to external storage).
     * @param requestCode the request code
     * @param permissions the permissions that were requested
     * @param grantResults the statuses for the permissions requested
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if there are any permissions worth checking
        if(requestCode != AttachmentDownloadListener.REQUEST_WRITE_PERMISSION_CODE
                || permissions.length == 0
                || grantResults.length == 0) {
            return;
        }

        // Retry downloading the attachment if the permission to write to
        // external storage has been granted.
        if(permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && this.webView != null && this.webView.get() != null) {
            this.webView.get().retryDownloadFile();
        }
    }
}
