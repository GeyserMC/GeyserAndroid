/*
 * Copyright (c) 2020-2020 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/GeyserAndroid
 */

package org.geysermc.geyser.android.ui.about;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.geysermc.geyser.android.BuildConfig;
import org.geysermc.geyser.android.R;
import org.geysermc.geyser.android.utils.AndroidUtils;

public class AboutFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.about_preferences, rootKey);

        findPreference("version").setSummary(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            // About
            case "version":
                AndroidUtils.setClipboard(requireContext(), BuildConfig.VERSION_NAME);
                AndroidUtils.showToast(getContext(), getResources().getString(R.string.about_version_copied));
                return true;

            // Links
            case "github":
                AndroidUtils.showURL("https://github.com/GeyserMC/GeyserAndroid");
                return true;
            case "trello":
                AndroidUtils.showURL("https://trello.com/b/pPJpl9dZ/geyser-android");
                return true;
            case "ci":
                AndroidUtils.showURL("https://ci.opencollab.dev/job/GeyserMC/job/GeyserAndroid/");
                return true;

            // Credits
            case "rtm516":
                AndroidUtils.showURL("https://rtm516.co.uk/");
                return true;
            case "arcratist":
                AndroidUtils.showURL("https://github.com/Arcratist/");
                return true;
            case "geyser":
                AndroidUtils.showURL(getResources().getString(R.string.app_site));
                return true;

            // Licences
            case "fontawesome":
                AndroidUtils.showURL("https://fontawesome.com/license");
                return true;
            default:
                break;
        }

        return super.onPreferenceTreeClick(preference);
    }
}
