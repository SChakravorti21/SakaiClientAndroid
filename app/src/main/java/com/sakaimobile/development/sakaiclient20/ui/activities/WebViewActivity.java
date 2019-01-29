package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.autofill.AutofillValue;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.networking.utilities.CASWebViewClient;
import com.sakaimobile.development.sakaiclient20.networking.utilities.LoginPersistenceWorker;

public class WebViewActivity extends AppCompatActivity {

    private static final String SAKAI_ACCOUNT_TYPE = "https://sakai.rutgers.edu";
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
                .forceEnableSaveDialog()
                .build();
        this.credentialsClient = Credentials.getClient(this, options);

        // Create a custom WebView client that will listen for when
        // authentication is complete and the main activity can be started
        this.loginWebView = findViewById(R.id.login_web_view);
        WebViewClient webViewClient = new CASWebViewClient(getString(R.string.COOKIE_URL_2), this::onLoginSuccess);
        loginWebView.setWebViewClient(webViewClient);

        //The CAS system requires Javascript for the login to even load
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.loadUrl(getString(R.string.CAS_BASE_URL));

        this.initializeAutoLogin();
    }

    private void onLoginSuccess(String username, String password) {
        // Evaluate javascript does not work on API Level < 19
        if(this.isAutoLoggingIn || username == null || password == null) {
            showContent();
            return;
        }

        Credential credential = new Credential.Builder(username)
                .setPassword(password)
                .build();

        credentialsClient.save(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("", "SAVE: OK");
                Toast.makeText(WebViewActivity.this, "Credentials saved", Toast.LENGTH_SHORT).show();
                showContent();
                return;
            }

            Exception e = task.getException();
            if (e instanceof ResolvableApiException) {
                // Try to resolve the save request. This will prompt the user if
                // the credential is new.
                ResolvableApiException rae = (ResolvableApiException) e;
                try {
                    rae.startResolutionForResult(WebViewActivity.this, RC_SAVE);
                } catch (IntentSender.SendIntentException intentSenderException) {
                    // Could not resolve the request
                    Log.e("", "Failed to send resolution.", e);
                    Toast.makeText(WebViewActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
                    showContent();
                }
            } else {
                // Request has no resolution
                Toast.makeText(WebViewActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
                showContent();
            }
        });
    }

    private void showContent() {
        // Ensure that the cookies persist even when the app is closed
        // (Allows users to restart the app without logging in again
        // since the cookies allow login to be bypassed if valid)
        // Unfortunately only works on API 21+ (but that's good enough for us)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
            // Start background task to keep cookies active
            // LoginPersistenceWorker.startLoginPersistenceTask();
        }

        Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
        startActivity(intent);

        // Exit app if the user presses the back button from the MainActivity
        finish();
    }

    private void initializeAutoLogin() {
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setAccountTypes(SAKAI_ACCOUNT_TYPE)
                .setPasswordLoginSupported(true)
                .build();

        credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // See "Handle successful credential requests"
                signInWithCredentials(task.getResult().getCredential());
                return;
            }

            Exception e = task.getException();
            if(e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                int statusCode = apiException.getStatusCode();
                if(statusCode == CommonStatusCodes.SIGN_IN_REQUIRED
                        || statusCode == CommonStatusCodes.CANCELED) {
                    // The user must create an account or sign in manually.
                    Log.e("Login", "Unsuccessful credential request.", e);
                } else if(apiException instanceof ResolvableApiException) {
                    // This is most likely the case where the user has multiple saved
                    // credentials and needs to pick one. This requires showing UI to
                    // resolve the read request.
                    ResolvableApiException rae = (ResolvableApiException) e;
                    resolveResult(rae, RC_READ);
                }
            }
        });
    }

    private void signInWithCredentials(Credential credential) {
        // TODO
        this.isAutoLoggingIn = true;
        String username = credential.getId();
        String password = credential.getPassword();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String query = "document.querySelector('#username').value = '%s';" +
                           "document.querySelector('#password').value = '%s';" +
                           "document.querySelector('input.btn-submit[value=\"LOGIN\"]').click();";
            query = String.format(query, username, password);
            this.loginWebView.evaluateJavascript(query, null);
            this.loginWebView.setEnabled(false);
        }
    }

    private void resolveResult(ResolvableApiException rae, int requestCode) {
        try {
            rae.startResolutionForResult(this, requestCode);
        } catch (IntentSender.SendIntentException e) {
            Log.e("Login", "Failed to send resolution.", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_READ) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                signInWithCredentials(credential);
            } else {
                Log.e("Login", "Credential Read: NOT OK");
                Toast.makeText(this, "No saved credentials found, please log in manually.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Log.d("Login", "SAVE: OK");
                Toast.makeText(this, "Credentials saved", Toast.LENGTH_SHORT).show();
                showContent();
            } else {
                Log.e("Login", "SAVE: Canceled by user");
            }
        }
    }
}
