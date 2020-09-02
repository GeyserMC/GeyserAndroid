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

package org.geysermc.app.android.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.geysermc.app.android.R;
import org.geysermc.app.android.utils.AndroidUtils;

import java.io.File;

public class SettingsFragment extends PreferenceFragmentCompat {

    @SuppressLint("NewApi")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference branchRefresh = (Preference) findPreference("geyser_reset_config");
        branchRefresh.setOnPreferenceClickListener(preference -> {
            File configFile = AndroidUtils.getStoragePath(getContext()).resolve("config.yml").toFile();
            if (configFile.exists()) {
                if (configFile.delete()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.settings_reset_config_success_title))
                            .setMessage(getString(R.string.settings_reset_config_success_message))
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.settings_reset_config_failed_title))
                            .setMessage(getString(R.string.settings_reset_config_failed_message))
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.settings_reset_config_missing_title))
                        .setMessage(getString(R.string.settings_reset_config_missing_message))
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
            return true;
        });
    }
}
