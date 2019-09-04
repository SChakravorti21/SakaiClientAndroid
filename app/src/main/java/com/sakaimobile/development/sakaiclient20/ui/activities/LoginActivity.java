package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.networking.utilities.CASWebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LoginActivity extends AppCompatActivity implements CASWebViewClient.SakaiLoadedListener {

    private static final int RC_READ = 9823;
    private static final int RC_SAVE = 9824;

    private WebView loginWebView;
    private ProgressBar loadingIndicator;
    private View webViewOverlay;

    private CredentialsClient credentialsClient;
    private boolean isAutoLoggingIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = findViewById(R.id.webview_toolbar);
        setSupportActionBar(toolbar);

        // Initialize Credentials Client for Smart Lock integration
        CredentialsOptions options = new CredentialsOptions.Builder()
                // ensure that the user gets to make the decision regarding saving login info
                .forceEnableSaveDialog()
                .build();
        this.credentialsClient = Credentials.getClient(this, options);

        // Create a custom WebView client that will listen for when
        // authentication is complete and the main activity can be started
        this.loginWebView = findViewById(R.id.login_web_view);
        this.loadingIndicator = findViewById(R.id.loading_indicator);
        this.webViewOverlay = findViewById(R.id.webview_overlay);

        String baseUrl = getString(R.string.CAS_BASE_URL);
        String loginCompletionUrl = getString(R.string.COOKIE_URL_2);
        WebViewClient webViewClient = new CASWebViewClient(baseUrl, loginCompletionUrl, this);
        this.loginWebView.setWebViewClient(webViewClient);

        //The CAS system requires Javascript for the login to even load
        this.loginWebView.getSettings().setJavaScriptEnabled(true);
        this.loginWebView.loadUrl(baseUrl);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // We make two types of requests, either to read existing credentials,
        // or save new ones. Saving might fail if the user decides to "Never" save _any_ info
        if (requestCode == RC_READ && resultCode == RESULT_OK) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            signInWithCredentials(credential);
        } else if (requestCode == RC_SAVE) {
            if(resultCode == RESULT_OK)
                Toast.makeText(this, "Sakai credentials saved", Toast.LENGTH_SHORT).show();
            // Regardless of whether the user decided to save or not, they need to be shown
            // the main content
            this.showContent();
        }
    }

    /**
     * Attempts to auto-login the user by requesting their saved credentials
     * from Smart Lock and auto-filling the login form. If the user has multiple
     * Sakai accounts (unlikely, but best practice to implement), then the account
     * they would like to log in with is resolved with a secondary request.
     *
     * This auto-login attempt is deferred until the login page finishes loading,
     * otherwise we end up with race conditions where the CredentialRequest completes
     * before the login form is available to be auto-filled.
     * {@link CASWebViewClient#onPageFinished} to see how this occurs.
     */
    @Override
    public void onLoginPageLoaded() {
        // We don't need to set the account type because Smart Lock defaults
        // to looking for credentials associated with our app package
        Log.e("Login", "Starting login");
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        this.credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {
            // If credentials available, perform auto-login
            if (task.isSuccessful() && task.getResult() != null) {
                signInWithCredentials(task.getResult().getCredential());
            } else if(task.getException() instanceof ApiException) {
                // Otherwise, it is possible that the user has multiple accounts,
                // in which case we need to resolve which account they want to use.
                // If SIGN_IN_REQUIRED or CANCELLED, the user has the intention of manually logging in
                ApiException apiException = (ApiException) task.getException();
                int statusCode = apiException.getStatusCode();
                Log.i("Login status code", "" + statusCode);

                // If we encounter an INTERNAL_ERROR, just give it another try
                if(statusCode == CommonStatusCodes.INTERNAL_ERROR) {
                    this.onLoginPageLoaded();
                    return;
                }

                if(statusCode != CommonStatusCodes.SIGN_IN_REQUIRED
                        && statusCode != CommonStatusCodes.CANCELED
                        && apiException instanceof ResolvableApiException) {
                    // This is most likely the case where the user has multiple saved
                    // credentials and needs to pick one. This requires showing UI to
                    // resolve the read request.
                    ResolvableApiException rae = (ResolvableApiException) apiException;
                    try {
                        rae.startResolutionForResult(this, RC_READ);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("Login", "Failed to send resolution.", e);
                    }
                }
            }
        });
    }

    /**
     * Automatically populates the login form with the given credentials.
     * @param credential the username and password
     */
    private void signInWithCredentials(Credential credential) {
        // Ensure the user doesn't keep spamming buttons/it is obvious that login is loading
        this.hideLogin();

        // set isAutoLoggingIn so that we don't try to make redundant save calls
        this.isAutoLoggingIn = true;
        String username = credential.getId();
        String password = credential.getPassword();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // inject JS to auto-login
            String query = "document.querySelector('#username').value = '%s';" +
                    "document.querySelector('#password').value = '%s';" +
                    "document.querySelector('input.btn-submit[value=\"LOGIN\"]').click();";
            query = String.format(query, username, password);
            this.loginWebView.evaluateJavascript(query, null);
        }
    }

    @Override
    public void onLoginSuccess(String username, String password) {
        // Ensure the user doesn't keep spamming buttons/it is obvious that login is loading
        runOnUiThread(this::hideLogin);
        // Disable auto sign-in so that users can have a chance to close and reopen
        // the app to sign in if their password changes
        this.credentialsClient.disableAutoSignIn();

        // Evaluate javascript does not work on API Level < 19, in which case
        // username and password are null
        if(this.isAutoLoggingIn || username == null || password == null) {
            this.showContent();
            return;
        }

        // We save the credential here because we only want to save if it is a successful
        // login attempt
        Credential credential = new Credential.Builder(username)
                .setPassword(password)
                .build();

        this.credentialsClient.save(credential).addOnCompleteListener(task -> {
            Exception e = task.getException();
            if (task.isSuccessful() || !(e instanceof ResolvableApiException)) {
                this.showContent();
            } else {
                // Try to resolve the save request. This will prompt the user if
                // the credential is new.
                ResolvableApiException rae = (ResolvableApiException) e;
                try {
                    rae.startResolutionForResult(LoginActivity.this, RC_SAVE);
                } catch (IntentSender.SendIntentException intentSenderException) {
                    // Could not resolve the request, proceed to content (no point retrying)
                    Toast.makeText(LoginActivity.this, "Credential save failed", Toast.LENGTH_SHORT).show();
                    this.showContent();
                }
            }
        });
    }

    private void showContent() {
        // In case the phone is not logged into a Google account, at least flush the cookies
        // for session persistence for the next two-ish hours
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        }

        Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
        startActivity(intent);
        finish();
    }

    private void hideLogin() {
        // Make sure user does not spam login button and indicate that login is in progress
        this.loginWebView.setEnabled(false);
        this.webViewOverlay.setVisibility(View.VISIBLE);
        this.loadingIndicator.setVisibility(View.VISIBLE);
    }
}
