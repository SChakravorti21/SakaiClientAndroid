package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class WebViewActivity extends AppCompatActivity implements CASWebViewClient.SakaiLoadedListener {

    private static final int RC_READ = 9823;
    private static final int RC_SAVE = 9824;

    private WebView loginWebView;
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
        WebViewClient webViewClient = new CASWebViewClient(getString(R.string.COOKIE_URL_2), this);
        loginWebView.setWebViewClient(webViewClient);

        //The CAS system requires Javascript for the login to even load
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.loadUrl(getString(R.string.CAS_BASE_URL));

        // Attempt to log in automatically through Google Smart Lock for Passwords
        this.tryAutoLogin();
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

    private void tryAutoLogin() {
        // We don't need to set the account type because Smart Lock defaults
        // to looking for credentials associated with our app package
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {
            // If credentials available, perform auto-login
            if (task.isSuccessful() && task.getResult() != null) {
                signInWithCredentials(task.getResult().getCredential());
            } else if(task.getException() instanceof ApiException) {
                // Otherwise, it is possible that the user has multiple accounts,
                // in which case we need to resolve which account they want to use.
                // If SIGN_IN_REQUIRED or CANCELLED, the user has the intention of manually logging in
                ApiException apiException = (ApiException) task.getException();
                int statusCode = apiException.getStatusCode();
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

    private void signInWithCredentials(Credential credential) {
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
            // make sure user does not spam login button
            this.loginWebView.setEnabled(false);
        }
    }

    @Override
    public void onLoginSuccess(String username, String password) {
        // Evaluate javascript does not work on API Level < 19, in which case
        // username and password are null
        if(this.isAutoLoggingIn || username == null || password == null) {
            this.showContent();
            return;
        }

        // This is the credential we will be saving
        Credential credential = new Credential.Builder(username)
                .setPassword(password)
                .build();

        credentialsClient.save(credential).addOnCompleteListener(task -> {
            Exception e = task.getException();
            if (task.isSuccessful() || !(e instanceof ResolvableApiException)) {
                this.showContent();
            } else {
                // Try to resolve the save request. This will prompt the user if
                // the credential is new.
                ResolvableApiException rae = (ResolvableApiException) e;
                try {
                    rae.startResolutionForResult(WebViewActivity.this, RC_SAVE);
                } catch (IntentSender.SendIntentException intentSenderException) {
                    // Could not resolve the request, proceed to content (no point retrying)
                    Toast.makeText(WebViewActivity.this, "Credential save failed", Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
