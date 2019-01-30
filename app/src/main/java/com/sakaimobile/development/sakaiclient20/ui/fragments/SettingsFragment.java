package com.sakaimobile.development.sakaiclient20.ui.fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credentials;
import com.sakaimobile.development.sakaiclient20.BuildConfig;
import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.ui.activities.CreditsActivity;
import com.sakaimobile.development.sakaiclient20.ui.activities.LoginActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private static final String ABOUT_URL = "https://rutgerssakai.github.io/SakaiMobile/";
    private static final String PRIVACY_URL = "https://rutgerssakai.github.io/SakaiMobile/privacy.html";
    private static final String RATE_URI = "market://details?id=" + BuildConfig.APPLICATION_ID;
    private static final String DEVELOPER_EMAIL = "rutgerssakaiapp@gmail.com";

    private static final String DEFAULT_MAIL_BODY =
            "Dear Rutgers Sakai Mobile developers,\n\n" +
            "Here is some feedback to improve the app:\n" +
            "\n\n\n" +
            "Regards,";


    // maps the View ID to the text and to the icon
    private SparseArray<String> appInfoItemIDToName;
    private SparseArray<String> appInfoItemIDToIcon;

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

        // init the maps then set the data in the views
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
            Credentials.getClient(getContext()).disableAutoSignIn();
            // Start the login activity, clearing all activities in back stack
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.logoutButton.setOnClickListener(null);
        this.logoutButton = null;
    }

    /**
     * Maps from the ID of the app info View to
     * the String representation and correct icon
     */
    private void initAppInfoMaps() {

        // Sparse Arrays are more efficient than HashMaps for integer keys
        // since it doesn't need to autobox the keys (good for small datasets)
        appInfoItemIDToName = new SparseArray<>();
        appInfoItemIDToName.put(R.id.about_item, "About");
        appInfoItemIDToName.put(R.id.privacy_item, "Privacy Policy");
        appInfoItemIDToName.put(R.id.credits_item, "Thanks To");
        appInfoItemIDToName.put(R.id.contactus_item, "Contact Us");
        appInfoItemIDToName.put(R.id.rate_item, "Rate Rutgers Sakai Mobile");

        appInfoItemIDToIcon = new SparseArray<>();
        appInfoItemIDToIcon.put(R.id.about_item, "\uf518");
        appInfoItemIDToIcon.put(R.id.privacy_item, "\uf084");
        appInfoItemIDToIcon.put(R.id.credits_item, "\uf118");
        appInfoItemIDToIcon.put(R.id.contactus_item, "\uf1fa");
        appInfoItemIDToIcon.put(R.id.rate_item, "\uf005");

    }

    /**
     * Generic on click listener for each AppInfoItem
     * @param v clicked view
     */
    private void onClickAppInfoItem(View v) {

        switch(v.getId()) {
            case R.id.about_item:
                openURL(ABOUT_URL);
                return;
            case R.id.privacy_item:
                openURL(PRIVACY_URL);
                return;
            case R.id.credits_item:
                startCreditsActivity();
                return;
            case R.id.contactus_item:
                openSendMailPage();
                return;
            case R.id.rate_item:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RATE_URI)));
        }

    }

    /**
     * Sets the data in each AppInfoItem (which are assumed to be children of the LinearLayout
     * found in the fragment)
     * @param parentView inflated fragment view
     */
    private void setAppInfoItemsData(View parentView) {

        LinearLayout linearLayout = parentView.findViewById(R.id.appinfo_linearlayout);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View item = linearLayout.getChildAt(i);
            int itemId = item.getId();

            TextView iconTxt = item.findViewById(R.id.info_icon);
            iconTxt.setText(appInfoItemIDToIcon.get(itemId));

            TextView infoTxt = item.findViewById(R.id.info_txt);
            infoTxt.setText(appInfoItemIDToName.get(itemId));

            item.setOnClickListener(this::onClickAppInfoItem);
        }
    }


    private void startCreditsActivity() {
        Intent intent = new Intent(getActivity(), CreditsActivity.class);
        startActivity(intent);
    }


    /**
     * Opens a mail client for the user to easily send feedback to developers
     */
    private void openSendMailPage() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + DEVELOPER_EMAIL));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Rutgers Sakai Android: Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, DEFAULT_MAIL_BODY);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send feedback to developers"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Opens a given URL in chrome browser
     * @param url
     */
    private void openURL(String url) {

        Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        viewIntent.setPackage("com.android.chrome");

        try {
            startActivity(viewIntent);
        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed so let the user choose
            viewIntent.setPackage(null);
            startActivity(viewIntent);
        }
    }

}
