package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.AttachmentDownloadListener;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.FileCompatWebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Shoumyo Chakravorti.
 *
 * A {@link Fragment} that represents content as a {@link android.webkit.WebView}.
 * This can be any type of content that can be accessed by a URL, such as
 * a site page, an assignment submission entity, or an attachment. Automatically
 * detaches itself from the screen if a download is started.
 */

public class WebFragment extends Fragment {

    private static final String URL_PARAM = "URL_PARAM";

    /**
     * The URL that the {@code WebFragment} represents. This can either be a
     * viewable URL (such as a website), or an attachment link.
     * If it is an attachment link, the {@code WebFragment} initiates
     * the download then detaches itself.
     */
    private String URL;

    /**
     * A {@code WeakReference} to the
     * {@link com.sakaimobile.development.sakaiclient20.ui.custom_components.FileCompatWebView} that
     * represents the content of this fragment. The <c>FileCompatWebView</c>
     * performs the majority of the important functions, including showing
     * web pages, downloading content, and allowing the user to upload content.
     */
    private FileCompatWebView webView;

    public WebFragment() {
        // Required empty public constructor
    }

    /**
     * Constructs a {@code WebFragment} with the given URL (shorthand
     * for creating a bundle with the URL and setting the fragment's arguments).
     * @param url The URL that this {@code WebFragment} should represent.
     * @return An instance of a {@code WebFragment} that will show the {@code url}.
     */
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
            URL = getArguments().getString(URL_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get a reference to the WebView that shows the page's contents
        // and initialize the WebView to be able to download and upload content
        this.webView = view.findViewById(R.id.data_webview);
        this.webView.initialize(this);

        // Present the expected URL
        this.webView.loadUrl(URL);
    }

    /**
     * Called when a result is received from another intent. For the purpose
     * of the {@code WebFragment}, the only result that matters is file upload,
     * which is handed off to the {@code FileCompatWebView} owned by this Fragment.
     * @param requestCode The request code for the {@code intent}
     * @param resultCode The status of the result
     * @param intent The original {@link Intent} that made the request
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Hand off the result to the webView, which will figure out
        // if it is of any importance to this fragment
        if(this.webView != null) {
            this.webView.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.webView = null;
    }

    /**
     * Called when a system permission is requested (such as reading from or writing to
     * external storage). For the purpose of {@code WebFragment}, the only important
     * permission is writing and reading external storage to be able to download or
     * upload assignments.
     * @param requestCode The request code
     *                    (usually {@code AttachmentDownloadListener.REQUEST_WRITE_PERMISSION_CODE})
     * @param permissions The permissions that were requested
     * @param grantResults The statuses for the permissions requested (Granted, denied, etc.)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the request code was for writing to external storage, and
        // if there are any permissions worth checking
        if(requestCode != AttachmentDownloadListener.REQUEST_WRITE_PERMISSION_CODE
                || permissions.length == 0
                || grantResults.length == 0) {
            return;
        }

        // If writing to external storage was granted and the WebView is still
        // around, retry downloading the file (which initially triggered the
        // the request for the permission).
        if(permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && this.webView != null) {
            this.webView.retryDownloadFile();
        } else {
            // Permission was not granted/action cannot be performed, so
            // this fragment should be popped off to return to the previous
            // screen.
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
