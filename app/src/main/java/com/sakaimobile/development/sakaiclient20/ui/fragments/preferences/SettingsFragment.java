package com.sakaimobile.development.sakaiclient20.ui.fragments.preferences;


import android.os.Bundle;
import android.view.View;

import com.sakaimobile.development.sakaiclient20.BuildConfig;
import com.sakaimobile.development.sakaiclient20.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        Preference about = findPreference("version");
        about.setSummary("Version " + BuildConfig.VERSION_NAME);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
