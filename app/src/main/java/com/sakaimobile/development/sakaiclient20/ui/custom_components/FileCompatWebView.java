package com.sakaimobile.development.sakaiclient20.ui.custom_components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.view.NestedScrollingChild;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;

/**
 * Created by Shoumyo Chakravorti on 7/21/18.
 *
 * A custom {@link WebView} implementation that is able to manage
 * the downloading and uploading of file to and from the {@link WebView}.
 * This is particularly useful in allowing users to fully interact with
 * site content, such as downloading attachments or uploading assignments
 * through the submission dialog.
 */

public class FileCompatWebView extends WebView implements NestedScrollingChild {

    /**
     * The request code for {@link Intent}s that allow the user to upload
     * attachments for assignments.
     */
    private static final int FILE_REQUEST_CODE = 28374;

    /**
     * The {@link AttachmentDownloadListener} that handles attachment
     * downloads in its {@code onDownloadStart} method.
     */
    private AttachmentDownloadListener attachmentDownloadListener;

    /**
     * A {@code WeakReference} to he parent {@code Fragment}, necessary
     * for file uploads to be able to initiate an {@link Intent}.
     */
    private WeakReference<Fragment> parentFragment;

    /**
     * Legacy {@link ValueCallback} used for versions below Android 5.0.
     */
    private ValueCallback<Uri> valueCallbackCompat;

    /**
     * A {@link ValueCallback} that can be called with a local file URL
     * to allow file uploads to this {@link WebView}.
     */
    private ValueCallback<Uri[]> valueCallback;

    // Mandatory constructors for WebView
    public FileCompatWebView(Context context) {
        super(context);
    }

    public FileCompatWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FileCompatWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public FileCompatWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Initializes the {@code FileCompatWebView}'s {@link WebSettings},
     * {@link WebViewClient}, and {@link WebChromeClient}.
     */
    public void initialize() {
        this.initializeSettings();
        this.initializeWebViewClient();
        this.initializeWebChromeClient();
    }

    /**
     * An initialization method that has been separated from the mandatory constructors
     * to ensure proper construction is not affected by introducing a {@link Fragment}
     * into the method signature.
     *
     * Calls the base {@code initialize} method and also initializes the download
     * listener.
     */
    public void initialize(Fragment fragment) {
        this.initialize();
        this.parentFragment = new WeakReference<>(fragment);
        this.attachmentDownloadListener = new AttachmentDownloadListener(fragment);
        this.setDownloadListener(this.attachmentDownloadListener);
    }

    /**
     * Initializes various settings for the {@link WebView} to display
     * its content properly/allow the expected user interactions.
     */
    private void initializeSettings() {
        WebSettings settings = this.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        // Some links redirect to other sites.
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        // Plugin setting for playing videos
        settings.setPluginState(WebSettings.PluginState.ON);

        // Allowing file content access that is necessary for the
        // WebView to access uploaded attachments for assignments.
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
    }

    /**
     * Initializes the {@link WebView}'s {@link WebViewClient},
     * mainly dictating that the URL loading/redirecting should ALWAYS
     * be handled by the WebView. This is necessary because only this
     * application has the cookies necessary for the user to view their
     * content, so redirecting to an external application would just give
     * them the Sakai login page.
     */
    private void initializeWebViewClient() {
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // For attachment links, the URLs are often broken by the way
                // redirects work in this fragment. Intercept the url and adjust it
                // if the link is broken.
                if(url.contains("access/null/content")) {
                    url = url.replaceFirst("/null", "");
                }

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Remove the annoying banner if possible (if no such DOM elements exist,
                // nothing will happen, normal execution continues)
                if(view != null)
                    view.loadUrl("javascript:document.querySelector('nav#skipNav').remove();" +
                            "document.querySelector('div.Mrphs-topHeader').remove();" +
                            "document.querySelector('nav.Mrphs-siteHierarchy').remove();" +
                            "document.querySelector('main#content').style.marginTop = 0;" +
                            "document.querySelector('.workspace').style.paddingTop = 0;");
            }
        });
    }

    /**
     * Initializes the {@link WebChromeClient} of this {@link WebView}.
     * The {@link WebChromeClient} primarily handles file uploads
     * to this {@link WebView}.
     */
    private void initializeWebChromeClient() {
        this.setWebChromeClient(new WebChromeClient() {

            // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (
            // hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType,
                                        String capture) {
                openFileInput(uploadMsg, null);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                return openFileInput(null, filePathCallback);
            }
        });
    }

    private boolean openFileInput(final ValueCallback<Uri> valueCallbackCompat,
                                  final ValueCallback<Uri[]> valueCallback) {

        // Invalidate the value callbacks if they still exist
        if(this.valueCallbackCompat != null) {
            this.valueCallbackCompat.onReceiveValue(null);
        }
        this.valueCallbackCompat = valueCallbackCompat;

        if(this.valueCallback != null) {
            this.valueCallback.onReceiveValue(null);
        }
        this.valueCallback = valueCallback;

        // Create an intent for the user to choose a file
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // user should be able to upload whatever file type they want

        // Create a chooser intent in case the user has multiple applications
        // that can handle opening files other than the native file chooser).
        Intent chooserIntent = Intent.createChooser(intent, "Choose a file");

        // Start the activity for result to get the file back
        if(parentFragment != null && parentFragment.get() != null) {
            parentFragment.get().startActivityForResult(chooserIntent, FILE_REQUEST_CODE);
            return true; // Intent was handled successfully
        }

        // Intent could not be handled (parentFragment is no longer active).
        return false;
    }

    /**
     * Called from the parent
     *  {@link com.sakaimobile.development.sakaiclient20.ui.fragments.WebFragment}'s
     *  {@code onActivityResult} to handle file uploads to the {@link WebView}.
     * @param requestCode The request code
     * @param resultCode The status of the request
     * @param intent The {@link Intent} that initiated the request for a file.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Check if there is any data worth sending to the WebView
        if(requestCode != FILE_REQUEST_CODE
                || resultCode != Activity.RESULT_OK
                || intent == null) {
            return;
        }

        // Use whichever valueCallback is not null (only one of them will ever be
        // non-null because only one of the methods in the WebChromeClient
        // will be called based on the Android version.
        if(valueCallbackCompat != null) {
            valueCallbackCompat.onReceiveValue(intent.getData());
            valueCallbackCompat = null;
        } else if (valueCallback != null) {
            Uri[] fileUris = null;

            if(intent.getDataString() != null) {
                try {
                    fileUris = new Uri[] { Uri.parse(intent.getDataString()) };
                } catch (Exception exception) {
                    // Exception is ignored, nothing can be done with the URI
                }
            } else if(intent.getClipData() != null) {
                ClipData clipData = intent.getClipData();
                int numFiles = clipData.getItemCount();
                fileUris = new Uri[ numFiles ];

                for(int i = 0; i < numFiles; i++) {
                    fileUris[i] = clipData.getItemAt(i).getUri();
                }
            }

            valueCallback.onReceiveValue(fileUris);
            valueCallback = null;
        }
    }

    /**
     * Always returns true so that the WebView can scroll inside other containers.
     * This is mainly implemented so that scrolling works inside the bottom sheet dialog
     * for assignment submission since putting the WebView inside a NestedScrollView
     * breaks rendering.
     * @return true (WebView always behaves like a nested scrolling child)
     */
    @Override
    public boolean isNestedScrollingEnabled() {
        return true;
    }

    /**
     * Exposes a method for the parent
     *  {@link com.sakaimobile.development.sakaiclient20.ui.fragments.WebFragment} to retry
     *  a download after it fails for the first time due to missing permissions
     *  (writing to and reading from external storage).
     */
    public void retryDownloadFile() {
        if(attachmentDownloadListener != null)
            attachmentDownloadListener.retryDownloadFile();
    }
}
