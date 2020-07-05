package org.geysermc.app.android.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import org.geysermc.app.android.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}
