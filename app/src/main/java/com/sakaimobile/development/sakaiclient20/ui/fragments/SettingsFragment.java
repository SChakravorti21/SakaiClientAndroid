package com.sakaimobile.development.sakaiclient20.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.ui.WebViewActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Make the button a red color
        // Setting android:backgroundColor makes the button lose its original shape
        // and ripple effect, and android:backgroundTint is not available below API 21
        this.logoutButton = view.findViewById(R.id.logout_button);
        ViewCompat.setBackgroundTintList(logoutButton, ContextCompat.getColorStateList(getContext(), R.color.sakai_medium_red));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.logoutButton.setOnClickListener(clickedView -> {
            // Removing the session cookie ensures that the login WebView
            // will allow user to login again (instead of automatically logging in)
            CookieManager.getInstance().removeSessionCookie();
            // Start the login activity, clearing all activities in back stack
            Intent intent = new Intent(getContext(), WebViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}
