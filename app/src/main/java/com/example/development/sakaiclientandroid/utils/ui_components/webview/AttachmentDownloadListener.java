package com.example.development.sakaiclientandroid.utils.ui_components.webview;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

    public static final int REQUEST_WRITE_PERMISSION_CODE = 23112;
    private WeakReference<Fragment> fragment;
    private String previousUrl;

    public AttachmentDownloadListener(Fragment fragment) {
        this.fragment = new WeakReference<>(fragment);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        // Check permission to write to external storage before trying to download the file
        if(checkAndRequestPermissions()) {
            downloadFile(url);
        }
    }

    /**
     * Publicly accessible for times when the write external storage permission
     * needs to be requested and is granted
     */
    public void retryDownloadFile() {
        downloadFile(previousUrl);
    }

    private void downloadFile(String url) {
        // Check for invalid url
        if(url == null || url.isEmpty())
            return;

        // Check if the fragment is still alive
        Fragment fragment = this.fragment != null ? this.fragment.get() : null;
        if(fragment == null) {
            previousUrl = url;
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

        DownloadManager downloadManager = (DownloadManager) fragment.getContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);

        if(downloadManager != null) {
            long downloadId = downloadManager.enqueue(request);
            DownloadCompleteReceiver.addDownloadId(downloadId);

            // Indicate that the download has begun
            Toast successToast = Toast.makeText(fragment.getContext(), "Download started...",
                    Toast.LENGTH_SHORT);
            successToast.show();
        } else {
            // Show a toast with an error
            Toast errorToast = Toast.makeText(fragment.getContext(), "Download failed, please try again later.",
                    Toast.LENGTH_SHORT);
            errorToast.show();
        }

        // Detach the fragment because it won;t be displaying any information
        // The WebFragment is always added to the backstack, so we need to pop
        // the backtstack for the back button to function as expected
        fragment.getActivity().getSupportFragmentManager().popBackStack();
    }

    private boolean checkAndRequestPermissions() {
        Fragment fragment = this.fragment != null ? this.fragment.get() : null;
        if(fragment == null) {
            // No point in checking the permissions if the fragment is no longer
            // in the foreground anyways
            return false;
        }

        if(ContextCompat.checkSelfPermission(fragment.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            fragment.requestPermissions(permissions, REQUEST_WRITE_PERMISSION_CODE);
            return false;
        }

        return true;
    }

    private String getCookies() {
        Fragment fragment = this.fragment != null ? this.fragment.get() : null;
        if(fragment == null) {
            return null;
        }

        // Since the CookieManager was managed by reference earlier
        // in the WebViewClient, the cookies should remain updated
        // We only need one set of cookies, the Sakai cookies,
        // so this method does not need to parse any extra cookies.
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieUrl = fragment.getContext().getString(R.string.COOKIE_URL_1);
        return cookieManager.getCookie(cookieUrl);
    }
}
