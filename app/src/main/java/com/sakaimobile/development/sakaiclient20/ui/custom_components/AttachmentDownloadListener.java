package com.sakaimobile.development.sakaiclient20.ui.custom_components;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.AssignmentSubmissionDialogFragment;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Shoumyo Chakravorti on 7/21/18.
 *
 * An implementation of a {@link android.webkit.WebView}'s {@link DownloadListener}.
 * Listens for download initiation and requests the parent {@link Fragment}'s
 * {@link DownloadManager} to download the requested file. Communicates with the
 * {@link DownloadCompleteReceiver} to ensure that the user can open the file after it is
 * downloaded.
 */

public class AttachmentDownloadListener implements DownloadListener {

    /**
     * The request code that is used to request the permission to
     * write to external storage (required for downloading files).
     */
    public static final int REQUEST_WRITE_PERMISSION_CODE = 23112;

    /**
     * A {@code WeakReference} to the parent fragment, necessary to access
     * the current {@link Context} and the {@link Fragment}'s {@link DownloadManager}.
     */
    private WeakReference<Fragment> fragment;

    /**
     * Stores the previous download URL if the permission to write to external storage
     * did not exist previously, allowing a retry of the download once the permission is
     * granted.
     */
    private String previousUrl;

    /**
     * Initializes the {@code AttachmentDownloadListener} with the parent
     * {@link Fragment}.
     * @param fragment The parent {@link Fragment}.
     */
    AttachmentDownloadListener(Fragment fragment) {
        this.fragment = new WeakReference<>(fragment);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        // Check permission to write to external storage before trying to download the file
        if(checkAndRequestPermissions()) {
            downloadFile(url);
        } else {
            // If download is not possible right now, save the URL so that
            // the download can be tried again once the permission is granted.
            previousUrl = url;
        }
    }

    /**
     * Accessible within the package for times when the write external storage permission
     * needs to be requested and is subsequently granted.
     */
    void retryDownloadFile() {
        downloadFile(previousUrl);
    }

    /**
     * Handles the download of attachments, whether it is initially when the attachment
     * is clicked (in which case the permission to write to external storage already exists),
     * or when retrying after being granted the permission to write to storage.
     * @param url The URL of the file to download
     */
    private void downloadFile(String url) {
        // Check for invalid url and check if the fragment is still alive
        if(url == null || url.isEmpty() || this.fragment.get() == null)
            return;

        // Parse the URI and create a download request
        Uri downloadUri = Uri.parse(url);
        // Title that will show up in the download manager/notification region
        String title = downloadUri.getLastPathSegment();

        // Check if the file has already been downloaded before enqueueing the request
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File downloadedFile = new File(downloadsDirectory.getAbsolutePath() + File.separatorChar + title);
        // If the file does not exist, proceed to download it, otherwise confirm to re-download
        if(!downloadedFile.exists()) {
            enqueueDownloadRequest(downloadUri, title);
        } else {
            new AlertDialog.Builder(this.fragment.get().getContext())
                .setTitle("Resource already exists")
                .setIcon(R.drawable.ic_folder_open_24dp)
                .setMessage("A file named \"" + title +
                        "\" already exists, are you sure you would like to download it again?")
                .setPositiveButton("Download again", (dialog, buttonClicked) -> {
                    // User chose to download the file again, so enqueue the
                    // download request anyways
                    enqueueDownloadRequest(downloadUri, title);
                })
                .setNegativeButton("Open existing file", (dialog, buttonClicked) -> {
                    openExistingFile(downloadedFile);
                })
                .show();
        }
    }

    /**
     * Uses the {@see DownloadManager} to initiate a file download for
     * the given URI. The file will be stored in the publicly-visible
     * downloads directory.
     */
    private void enqueueDownloadRequest(Uri downloadUri, String title) {
        DownloadManager.Request request = new DownloadManager.Request(downloadUri).setTitle(title);

        // Request header is necessary for the download to be successful, as it indicates
        // to Sakai that the user has permission to access this document.
        Fragment fragment = this.fragment.get();
        request.addRequestHeader("Cookie", getCookies(fragment));

        // Allows the file to be found by the device
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        // Indicates in the notification region that the download is in progress/completed
        request.setShowRunningNotification(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

        // Get the DownloadManager and enqueue the request to download this file
        DownloadManager downloadManager =
                (DownloadManager) fragment.getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        if(downloadManager != null) {
            long downloadId = downloadManager.enqueue(request);
            DownloadCompleteReceiver.addDownloadId(downloadId);

            // Indicate that the download has begun
            Toast successToast = Toast.makeText(fragment.getContext(),
                    "Download started...",
                    Toast.LENGTH_SHORT);
            successToast.show();
        } else {
            // Show a toast with an error
            Toast errorToast = Toast.makeText(fragment.getContext(),
                    "Unable to download file, please try again later.",
                    Toast.LENGTH_SHORT);
            errorToast.show();
        }

        // Return to previous screen while download is in progress
        exitDownloadFragment();
    }

    /**
     * Exits the {@see WebFragment} that initiated the file download
     * and opens the previously-downloaded file.
     */
    private void openExistingFile(File downloadedFile) {
        // Get the activity context and exit this fragment before
        // opening the file so that the user returns to the previous
        // fragment rather than a blank WebView
        Context context = this.fragment.get().getActivity();
        exitDownloadFragment();
        DownloadCompleteReceiver.openDownloadedFile(context, downloadedFile);
    }

    /**
     * Checks if this application has the permission to write to external storage,
     * which is necessary for downloading anything. If the permission does not exist
     * (i.e. this method returns false), then a request for the permission
     * is initiated in the meantime.
     * @return Whether the permission to download a file has already been granted
     */
    private boolean checkAndRequestPermissions() {
        Fragment fragment = this.fragment.get();
        if(fragment == null) {
            // No point in checking the permissions if the fragment is no longer
            // in the foreground anyways
            return false;
        }

        // If the permission has not been granted to this application, request it
        if(ContextCompat.checkSelfPermission(fragment.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            fragment.requestPermissions(permissions, REQUEST_WRITE_PERMISSION_CODE);
            return false;
        }

        // Permission has already been granted to the application, download can be initiated.
        return true;
    }

    /**
     * Returns to the previous Fragment in the backstack once a download
     * is initiated, or if an existing file is opened (so that the user
     * returns to the expected Fragment rather than a blank WebView).
     */
    private void exitDownloadFragment() {
        Fragment fragment = this.fragment.get();
        // Detach the fragment because it won't be displaying any information
        // The WebFragment is always added to the back stack, so we need to pop
        // the back stack for the back button to function as expected
        if(fragment != null && !(fragment instanceof AssignmentSubmissionDialogFragment))
            fragment.getActivity().onBackPressed();
    }

    /**
     * Gets the same cookies that allow the
     *  us to make requests to the Sakai server. These same cookies will provide
     *  the permission to download files as well.
     * @return The cookies in a {@link String} format.
     */
    private String getCookies(Fragment fragment) {
        if(fragment == null)
            return null;

        // Since the CookieManager was managed by reference earlier
        // in a WebViewClient, the cookies should remain updated
        // We only need one set of cookies, the Sakai cookies,
        // so this method does not need to parse any extra cookies.
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieUrl = fragment.getContext().getString(R.string.COOKIE_URL_1);
        return cookieManager.getCookie(cookieUrl);
    }
}
