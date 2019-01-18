package com.sakaimobile.development.sakaiclient20.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.ui.activities.WebViewActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private SparseArray<String> appInfoItemID_to_name;
    private SparseArray<String> appInfoItemID_to_icon;

    private Button logoutButton;

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

        initAppInfoMaps();
        setAppInfoItemsData(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.logoutButton.setOnClickListener(clickedView -> {
            // Removing the session cookie ensures that the login WebView
            // will allow user to login again (instead of automatically logging
            // in with saved cookies)
            CookieManager.getInstance().removeAllCookie();
            // Start the login activity, clearing all activities in back stack
            Intent intent = new Intent(getContext(), WebViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }


    private void initAppInfoMaps() {

        appInfoItemID_to_name = new SparseArray<>();
        appInfoItemID_to_name.put(R.id.about_item, "About");
        appInfoItemID_to_name.put(R.id.privacy_item, "Privacy Policy");
        appInfoItemID_to_name.put(R.id.shoutouts_item, "Thanks To");
        appInfoItemID_to_name.put(R.id.contactus_item, "Contact Us");
        appInfoItemID_to_name.put(R.id.rate_item, "Rate Rutgers Sakai Mobile");

        appInfoItemID_to_icon = new SparseArray<>();
        appInfoItemID_to_icon.put(R.id.about_item, "\uf518");
        appInfoItemID_to_icon.put(R.id.privacy_item, "\uf084");
        appInfoItemID_to_icon.put(R.id.shoutouts_item, "\uf118");
        appInfoItemID_to_icon.put(R.id.contactus_item, "\uf1fa");
        appInfoItemID_to_icon.put(R.id.rate_item, "\uf005");
    }

    private void setAppInfoItemsData(View parentView) {

        LinearLayout linearLayout = parentView.findViewById(R.id.appinfo_linearlayout);
        for(int i = 0; i < linearLayout.getChildCount(); i++) {
            View item = linearLayout.getChildAt(i);
            int itemId = item.getId();

            TextView iconTxt = item.findViewById(R.id.info_icon);
            iconTxt.setText(appInfoItemID_to_icon.get(itemId));

            TextView infoTxt = item.findViewById(R.id.info_txt);
            infoTxt.setText(appInfoItemID_to_name.get(itemId));
        }

    }
}
