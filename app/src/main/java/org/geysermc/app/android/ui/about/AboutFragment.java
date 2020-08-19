package org.geysermc.app.android.ui.about;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.geysermc.app.android.R;
import org.geysermc.app.android.utils.AndroidUtils;

public class AboutFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.about_preferences, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case "geyser":
                AndroidUtils.showURL("https://geysermc.org");
                return true;
            case "fontawesome":
                AndroidUtils.showURL("https://fontawesome.com/license");
                return true;
            default:
                break;
        }

        return super.onPreferenceTreeClick(preference);
    }
}
