package org.geysermc.app.android.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.geysermc.app.android.R;

public class AboutFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.about_preferences, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case "geyser":
                showURL("https://geysermc.org");
                return true;
            case "fontawesome":
                showURL("https://fontawesome.com/license");
                return true;
            default:
                break;
        }

        return super.onPreferenceTreeClick(preference);
    }

    private void showURL(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
