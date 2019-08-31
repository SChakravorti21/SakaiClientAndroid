package com.sakaimobile.development.sakaiclient20.ui.custom_components;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.HashSet;

/**
 * Created by Shoumyo Chakravorti on 7/7/18.
 *
 * A {@link BroadcastReceiver} that listens for successful file download
 * completions, and allows the user to open the downloaded file.
 */

public class DownloadCompleteReceiver extends BroadcastReceiver {

    /**
     * The set of downloaded files to open once download is completed
     * successfully.
     */
    private static HashSet<Long> downloads = new HashSet<>();

    /**
     * Receives all {@link Intent}s broadcasted to the application, and filters
     * them out to only handle downloaded files. If the file exists, an {@link Intent}
     * is initiated to allow the user to open the file with the application of their choice.
     * @param context The context broadcasting the {@link Intent}
     * @param intent The {@link Intent} being broadcasted
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,
                -1);

        // This is a broadcast receiver, so we need to check if the download
        // actually came from our application and the intent we anticipate
        if(downloadId == -1 || !downloads.contains(downloadId))
            return;

        // We won't be receiving a broadcast for this download again, so remove
        // the ID from the HashSet
        downloads.remove(downloadId);

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
            if(uriString != null && !uriString.isEmpty()) {
                Uri fileUri = Uri.parse(uriString);
                File downloadedFile = new File(fileUri.getPath());
                openDownloadedFile(context, downloadedFile);
            }
        }

    }

    /**
     * to add a {@code downloadId} to this {@code DownloadCompleteListener}'s list of
     * downloads to watch out for.
     * @param downloadId The ID of the download to open once the download is complete.
     */
    public static void addDownloadId(long downloadId) {
        downloads.add(downloadId);
    }

    public static void openDownloadedFile(Context context, File downloadedFile) {
        // In newer versions of Android (API 24+), the way files are shared
        // to other applications has changed, and must use the "content://"
        // scheme with a valid path. FileProvider allows us to expose the file
        // externally so that it that can be opened by a PDF viewer
        Uri fileUri = FileProvider.getUriForFile(context,
                "com.sakaimobile.development.sakaiclientandroid.fileprovider",
                downloadedFile);

        Intent selectViewerIntent = new Intent(Intent.ACTION_VIEW);
        selectViewerIntent.setData(fileUri);
        // Flag is necessary for opening application to be able to actually
        // read the file contents and render it.
        selectViewerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Allow the user to select what they want to open the file with.
        // (This is also a failsafe, because if there aren't any apps
        // that can open the file, Android will provide this message
        // and our app won't crash if it doesn't find a default app to open with).
        Intent openIntent = Intent.createChooser(selectViewerIntent,
                "Select an application to open this file:");
        openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(openIntent);
    }
}
