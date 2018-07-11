package com.example.development.sakaiclientandroid.fragments.assignments;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.utils.requests.DownloadCompleteReceiver;

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

        final WebView webView = view.findViewById(R.id.assignment_submission_view);
        webView.setDownloadListener(new AttachmentDownloadListener());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // For attachment links, the URLs are often broken by the way
                // redirects work in this fragment. Intercept the url and adjust it
                // if the link is broken.
                if(url.contains("access/null/content")) {
                    url = url.replaceFirst("/null", "");
                }

                webView.loadUrl(url);
                return false;
            }
        });

        WebSettings settings = webView.getSettings();
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
        }
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.loadUrl(url);
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
        }
    }
}
