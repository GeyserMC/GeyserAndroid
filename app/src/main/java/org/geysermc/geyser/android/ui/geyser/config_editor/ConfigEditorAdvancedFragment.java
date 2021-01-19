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

package org.geysermc.geyser.android.ui.geyser.config_editor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import org.geysermc.geyser.android.R;
import org.geysermc.geyser.android.geyser.GeyserAndroidConfiguration;
import org.geysermc.geyser.android.ui.geyser.user_auths.UserAuthsActivity;
import org.geysermc.geyser.android.utils.AndroidUtils;
import org.geysermc.geyser.android.utils.ConfigUtils;
import org.geysermc.connector.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lombok.Getter;

public class ConfigEditorAdvancedFragment extends PreferenceFragmentCompat {

    @Getter
    private static GeyserAndroidConfiguration configuration;

    @Getter
    private static boolean configChanged;

    @SuppressLint("NewApi")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(requireContext());
        setPreferenceScreen(preferenceScreen);

        configChanged = false;

        File configFile = AndroidUtils.getStoragePath(requireContext()).resolve("config.yml").toFile();

        try {
            // Try and parse the config file
            parseConfig(preferenceScreen, configFile);

            // Hide the loader
            AndroidUtils.HideLoader();
        } catch (IOException e) {
            // Hide the loader
            AndroidUtils.HideLoader();

            // Let the user know the config failed to load
            new AlertDialog.Builder(requireContext())
                    .setTitle(getResources().getString(R.string.config_editor_failed_title))
                    .setMessage(getResources().getString(R.string.config_editor_failed_message))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> this.requireActivity().finish())
                    .show();
        }
    }

    public void parseConfig(PreferenceScreen preferenceScreen, File configFile) throws IOException {
        // parse the configuration
        configuration = FileUtils.loadConfig(configFile, GeyserAndroidConfiguration.class);

        // Get the available properties from the class
        List<BeanPropertyDefinition> availableProperties = ConfigUtils.getPOJOForClass(GeyserAndroidConfiguration.class);

        // Make the bedrock category
        PreferenceCategory bedrockCategory = new PreferenceCategory(preferenceScreen.getContext());
        bedrockCategory.setTitle(getResources().getString(R.string.config_editor_advanced_section_bedrock));
        preferenceScreen.addPreference(bedrockCategory);

        // Make the remote (Java) category
        PreferenceCategory remoteCategory = new PreferenceCategory(preferenceScreen.getContext());
        remoteCategory.setTitle(getResources().getString(R.string.config_editor_advanced_section_remote));
        preferenceScreen.addPreference(remoteCategory);

        // Make the advanced category
        PreferenceCategory advancedCategory = new PreferenceCategory(preferenceScreen.getContext());
        advancedCategory.setTitle(getResources().getString(R.string.config_editor_advanced_section_advanced));
        preferenceScreen.addPreference(advancedCategory);

        // Make the metrics category
        PreferenceCategory metricsCategory = new PreferenceCategory(preferenceScreen.getContext());
        metricsCategory.setTitle(getResources().getString(R.string.config_editor_advanced_section_metrics));
        preferenceScreen.addPreference(metricsCategory);

        for (BeanPropertyDefinition property : availableProperties) {
            // Ignore the autoconfiguredRemote property
            if ("autoconfiguredRemote".equals(property.getName())) {
                continue;
            }

            if ("bedrock".equals(property.getName()) || "remote".equals(property.getName())
                    || "metrics".equals(property.getName())) {

                // Get the category for the property
                PreferenceCategory currentCategory = advancedCategory;
                switch (property.getName()) {
                    case "bedrock":
                        currentCategory = bedrockCategory;
                        break;
                    case "remote":
                        currentCategory = remoteCategory;
                        break;
                    case "metrics":
                        currentCategory = metricsCategory;
                        break;
                }

                // Loop the sub class properties
                for (BeanPropertyDefinition subProperty : ConfigUtils.getPOJOForClass(property.getRawPrimaryType())) {
                    try {
                        Object subConfig = property.getGetter().callOn(configuration);
                        createPreference(currentCategory, subProperty, subConfig);
                    } catch (Exception ignored) { }
                }

                continue;
            }

            createPreference(advancedCategory, property, configuration);
        }
    }

    /**
     * Create a preference from the given data.
     * Will try to generate a relevant preference for the property.
     *
     * @param category The category to add the preference to
     * @param property The property definition to use
     * @param parentObject The parent object of the current property
     */
    private void createPreference(PreferenceCategory category, BeanPropertyDefinition property, Object parentObject) {
        Preference newPreference;

        // Do any specific initialisation for the property
        if ("config-version".equals(property.getName())) {
            newPreference = new Preference(category.getParent().getContext());
            newPreference.setEnabled(false);
        } else if ("uuid".equals(property.getName())) {
            newPreference = new Preference(category.getParent().getContext());
            newPreference.setEnabled(false);
        } else if ("userAuths".equals(property.getName())) {
            newPreference = new Preference(category.getParent().getContext());
            newPreference.setSummary(getString(R.string.config_editor_advanced_user_auth_desc));
            newPreference.setOnPreferenceClickListener((preference) -> {
                AndroidUtils.ShowLoader(getContext());

                Intent intent = new Intent(getContext(), UserAuthsActivity.class);
                startActivity(intent);

                return true;
            });
        } else if (boolean.class.equals(property.getRawPrimaryType())) {
            newPreference = new SwitchPreference(category.getParent().getContext());
            try {
                ((SwitchPreference) newPreference).setChecked((boolean) ConfigUtils.forceGet(property, parentObject));
            } catch (Exception ignored) {
                ((SwitchPreference) newPreference).setChecked(true);
            }
        } else if (int.class.equals(property.getRawPrimaryType())) {
            newPreference = new EditTextPreference(category.getParent().getContext());
            ((EditTextPreference) newPreference).setOnBindEditTextListener((editText) -> {
                try {
                    editText.setText(ConfigUtils.forceGet(property, parentObject).toString());
                } catch (Exception ignored) { }
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setSelection(editText.getText().length());
            });
        } else {
            newPreference = new EditTextPreference(category.getParent().getContext());
            ((EditTextPreference) newPreference).setOnBindEditTextListener((editText) -> {
                try {
                    editText.setText(ConfigUtils.forceGet(property, parentObject).toString());
                } catch (Exception ignored) { }
                editText.setSelection(editText.getText().length());
            });
        }

        // Make sure we set the preference
        // Only set the summary if its not already been set
        if (newPreference.getSummary() == null) {
            try {
                newPreference.setSummary(ConfigUtils.forceGet(property, parentObject).toString());
            } catch (Exception ignored) { }
        }

        newPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            // Update the preference
            preference.setSummary(newValue.toString());
            configChanged = true;

            Object parsedValue = newValue;
            if (int.class.equals(property.getRawPrimaryType())) {
                parsedValue = Integer.valueOf((String) newValue);
            }

            // Get the value and force the update
            try {
                AnnotatedField field = property.getField();
                field.fixAccess(true);
                field.setValue(parentObject, parsedValue);
            } catch (Exception ignored) { }

            return true;
        });

        newPreference.setTitle(property.getName());
        newPreference.setKey(property.getName() + Math.random()); // Randomise the keys to prevent overlap
        newPreference.setPersistent(false);

        // Add the preference to the category
        category.addPreference(newPreference);
    }
}
