package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
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

    private static final String SAKAI_ACCOUNT_TYPE = "https://cas.rutgers.edu";
    private static final int RC_READ = 9823;

    CredentialsClient credentialsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = findViewById(R.id.webview_toolbar);
        setSupportActionBar(toolbar);

        // Create a custom WebView client that will listen for when
        // authentication is complete and the main activity can be started
        final WebView loginWebView = findViewById(R.id.login_web_view);
        loginWebView.setWebViewClient(new CASWebViewClient(
            getString(R.string.COOKIE_URL_2),
            savedHeaders -> {
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
        ));

        //The CAS system requires Javascript for the login to even load
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.loadUrl(getString(R.string.CAS_BASE_URL));

        this.initializeAutoLogin();
    }

    private void initializeAutoLogin() {
        credentialsClient = Credentials.getClient(this);
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setAccountTypes(SAKAI_ACCOUNT_TYPE)
                .setPasswordLoginSupported(true)
                .build();

        credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // See "Handle successful credential requests"
                onCredentialRetrieved(task.getResult().getCredential());
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

    private void onCredentialRetrieved(Credential credential) {
        String accountType = credential.getAccountType();
        if (accountType != null && accountType.equals(SAKAI_ACCOUNT_TYPE)) {
            // Sign the user in with information from the Credential.
            signInWithPassword(credential.getId(), credential.getPassword());
        }
    }

    private void signInWithPassword(String id, String password) {
        // TODO
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
                onCredentialRetrieved(credential);
            } else {
                Log.e("Login", "Credential Read: NOT OK");
                Toast.makeText(this, "No saved credentials found, please log in manually.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
