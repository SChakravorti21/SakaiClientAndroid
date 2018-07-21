package com.example.development.sakaiclientandroid.utils.ui_components.webview;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.utils.requests.DownloadCompleteReceiver;

import java.lang.ref.WeakReference;

/**
 * Created by Development on 7/21/18.
 */

public class AttachmentDownloadListener implements DownloadListener {

    private WeakReference<FragmentActivity> activity;

    public AttachmentDownloadListener(FragmentActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        downloadFile(url);
    }

    private void downloadFile(String url) {
        // Check if the activity is still alive
        FragmentActivity activity = this.activity != null ? this.activity.get() : null;
        if(activity == null) {
            return;
        }

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

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        if(downloadManager != null) {
            long downloadId = downloadManager.enqueue(request);
            DownloadCompleteReceiver.addDownloadId(downloadId);

            // Indicate that the download has begun
            Toast successToast = Toast.makeText(activity, "Download started...",
                    Toast.LENGTH_SHORT);
            successToast.show();
        } else {
            // Show a toast with an error
            Toast errorToast = Toast.makeText(activity, "Download failed, please try again later.",
                    Toast.LENGTH_SHORT);
            errorToast.show();
        }

        // Detach the fragment because it won;t be displaying any information
        // The WebFragment is always added to the backstack, so we need to pop
        // the backtstack for the back button to function as expected
        activity.getSupportFragmentManager().popBackStack();
    }

    private String getCookies() {
        FragmentActivity context = this.activity != null ? this.activity.get() : null;
        if(context == null) {
            return null;
        }

        // Since the CookieManager was managed by reference earlier
        // in the WebViewClient, the cookies should remain updated
        // We only need one set of cookies, the Sakai cookies,
        // so this method does not need to parse any extra cookies.
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieUrl = context.getString(R.string.COOKIE_URL_1);
        return cookieManager.getCookie(cookieUrl);
    }
}
