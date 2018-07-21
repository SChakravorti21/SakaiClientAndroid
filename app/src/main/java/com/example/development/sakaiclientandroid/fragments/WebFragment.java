package com.example.development.sakaiclientandroid.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.Attachment;
import com.example.development.sakaiclientandroid.utils.requests.DownloadCompleteReceiver;
import com.example.development.sakaiclientandroid.utils.ui_components.FileCompatWebView;

import java.lang.ref.WeakReference;

public class WebFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String URL_PARAM = "URL_PARAM";

    private String URL;
    private WeakReference<FileCompatWebView> webView;

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
        webView.setDownloadListener(new AttachmentDownloadListener());
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

    // TODO: This functionality is shared by WebFragment and AssignmentSubmissionDialogFragment,
    // refactor it to be usable by both classes.
    private class AttachmentDownloadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri downloadUri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.addRequestHeader("Cookie", getCookies());
            request.setTitle(downloadUri.getLastPathSegment());
            request.allowScanningByMediaScanner(); //allows the file to be found by the device
            request.setVisibleInDownloadsUi(true);
            request.setShowRunningNotification(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    downloadUri.getLastPathSegment());

            DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            if(downloadManager != null) {
                long downloadId = downloadManager.enqueue(request);
                DownloadCompleteReceiver.addDownloadId(downloadId);

                // Indicate that the download has begun
                Toast successToast = Toast.makeText(getContext(), "Download started...",
                        Toast.LENGTH_SHORT);
                successToast.show();
            } else {
                // Show a toast with an error
                Toast errorToast = Toast.makeText(getContext(), "Download failed, please try again later.",
                        Toast.LENGTH_SHORT);
                errorToast.show();
            }

            // Detach the fragment because it won;t be displaying any information
            // The WebFragment is always added to the backstack, so we need to pop
            // the backtstack for the back button to function as expected
            getActivity().getSupportFragmentManager().popBackStack();
        }

        private String getCookies() {
            // Since the CookieManager was managed by reference earlier
            // in the WebViewClient, the cookies should remain updated
            // We only need one set of cookies, the Sakai cookies,
            // so this method does not need to parse any extra cookies.
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieUrl = getContext().getString(R.string.COOKIE_URL_1);
            return cookieManager.getCookie(cookieUrl);
        }
    }
}
