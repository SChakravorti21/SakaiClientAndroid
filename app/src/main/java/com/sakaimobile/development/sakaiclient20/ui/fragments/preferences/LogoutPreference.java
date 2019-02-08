package com.sakaimobile.development.sakaiclient20.ui.fragments.preferences;

import android.content.Context;
import android.content.Intent;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.widget.Button;

import com.google.android.gms.auth.api.credentials.Credentials;
import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.ui.activities.LoginActivity;

public class LogoutPreference extends Preference {

    public LogoutPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preferences_footer);
    }

    public LogoutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preferences_footer);
    }

    public LogoutPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preferences_footer);
    }

    public LogoutPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.preferences_footer);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);

        Button logoutButton = holder.itemView.findViewById(R.id.logout_button);
        logoutButton.setClickable(true);
        logoutButton.setOnClickListener(v -> {
            Context context = getContext();
            if(context == null) return;

            // Removing the session cookie ensures that the login WebView
            // will allow user to login again (instead of automatically logging
            // in with saved cookies)
            CookieManager.getInstance().removeAllCookie();
            Credentials.getClient(context).disableAutoSignIn();
            // Start the login activity, clearing all activities in back stack
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
}
