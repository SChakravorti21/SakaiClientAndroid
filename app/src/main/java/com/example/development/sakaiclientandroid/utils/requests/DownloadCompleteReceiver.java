package com.example.development.sakaiclientandroid.utils.requests;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.webkit.DownloadListener;

import java.io.File;
import java.util.HashSet;

/**
 * Created by Development on 7/7/18.
 */

public class DownloadCompleteReceiver extends BroadcastReceiver {

    private static HashSet<Long> downloads = new HashSet<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,
                -1);

        // This is a broadcast receiver, so we need to check if the download
        // actually came from our application
        if(downloadId == -1 || !downloads.contains(downloadId))
            return;

        //
        DownloadManager downloadManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        // We can't get the downloaded file if there is no download manager
        if(downloadManager == null)
            return;

        DownloadManager.Query fileQuery = new DownloadManager.Query();
        fileQuery.setFilterById(downloadId);
        Cursor fileCursor = downloadManager.query(fileQuery);

        // Move to the file, and check if it exists (just in case downloading to the correct
        // location failed, or some other error occurred along the way)
        if(!fileCursor.moveToFirst())
            return;

        int fileStatusIndex = fileCursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int fileStatus = fileCursor.getInt(fileStatusIndex);

        // Check if the download was successful (the download might have ended abruptly)
        if(fileStatus == DownloadManager.STATUS_SUCCESSFUL) {
            int fileUriIndex = fileCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            String uriString = fileCursor.getString(fileUriIndex);

            // Get the file and open it if the uri is not empty
            if(uriString != null && !uriString.trim().equals("")) {
                Uri fileUri = Uri.parse(uriString);
                Intent selectViewerIntent = new Intent(Intent.ACTION_VIEW);
                selectViewerIntent.setData(fileUri);
                Intent openIntent = Intent.createChooser(selectViewerIntent,
                        "Select an application to open this file:");
                context.startActivity(openIntent);
            }
        }

    }

    public static void addDownloadId(long downloadId) {
        downloads.add(downloadId);
    }
}
