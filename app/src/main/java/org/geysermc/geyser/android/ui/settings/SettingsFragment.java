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

package org.geysermc.geyser.android.ui.settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.android.volley.ClientError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geysermc.geyser.android.R;
import org.geysermc.geyser.android.utils.AndroidDeviceDump;
import org.geysermc.geyser.android.utils.AndroidUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String DUMP_URL = "https://dump.geysermc.org/";

    @SuppressLint("NewApi")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference configReset = findPreference("geyser_reset_config");
        configReset.setOnPreferenceClickListener(preference -> {
            File configFile = AndroidUtils.getStoragePath(requireContext()).resolve("config.yml").toFile();
            if (configFile.exists()) {
                if (configFile.delete()) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle(getResources().getString(R.string.settings_reset_config_success_title))
                            .setMessage(getResources().getString(R.string.settings_reset_config_success_message))
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                } else {
                    new AlertDialog.Builder(requireContext())
                            .setTitle(getResources().getString(R.string.settings_reset_config_failed_title))
                            .setMessage(getResources().getString(R.string.settings_reset_config_failed_message))
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle(getResources().getString(R.string.settings_reset_config_missing_title))
                        .setMessage(getResources().getString(R.string.settings_reset_config_missing_message))
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
            return true;
        });

        Preference userAuthsReset = findPreference("geyser_reset_user_auths");
        userAuthsReset.setOnPreferenceClickListener(preference -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

            if (!("{}".equals(sharedPreferences.getString("geyser_user_auths", "{}")))) {
                sharedPreferences.edit().putString("geyser_user_auths", "{}").apply();

                new AlertDialog.Builder(requireContext())
                        .setTitle(getResources().getString(R.string.settings_reset_user_auths_success_title))
                        .setMessage(getResources().getString(R.string.settings_reset_user_auths_success_message))
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.settings_reset_user_auths_missing_title))
                        .setMessage(getResources().getString(R.string.settings_reset_user_auths_missing_message))
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
            return true;
        });

        // Handle the dump creation prefrence
        Preference createDeviceDump = findPreference("create_device_dump");
        createDeviceDump.setOnPreferenceClickListener(preference -> {
            try {
                // Let the user know we started
                AndroidUtils.showToast(getContext(), getResources().getString(R.string.settings_create_device_dump_uploading), Toast.LENGTH_LONG);

                // Dump the device info to JSON
                JSONObject data = new JSONObject(MAPPER.writeValueAsString(new AndroidDeviceDump(getContext())));

                // Create the request
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, DUMP_URL + "documents", data, response -> {
                        try {
                            // Check if we got a response key
                            if (!response.has("key")) {
                                new AlertDialog.Builder(getContext())
                                    .setTitle(getResources().getString(R.string.settings_create_device_dump_failed_title))
                                    .setMessage(getResources().getString(R.string.settings_create_device_dump_failed_message, (response.has("message") ? response.getString("message") : response.toString())))
                                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                                    .show();
                            }

                            String uploadedDumpUrl = DUMP_URL + response.getString("key");

                            final EditText input = new EditText(getContext());
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
                            input.setText(uploadedDumpUrl);

                            new AlertDialog.Builder(getContext())
                                    .setTitle(getResources().getString(R.string.settings_create_device_dump_success_title))
                                    .setView(input)
                                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                                    .setNegativeButton(getResources().getString(R.string.settings_create_device_dump_open), (dialog, which) -> {
                                        AndroidUtils.showURL(uploadedDumpUrl);
                                    })
                                    .show();
                        } catch (JSONException e) {
                            new AlertDialog.Builder(getContext())
                                .setTitle(getResources().getString(R.string.settings_create_device_dump_failed_title))
                                .setMessage(getResources().getString(R.string.settings_create_device_dump_failed_message, e.getMessage()))
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                                .show();
                        }
                }, error -> {
                    String message = error.getMessage();
                    try {
                        JSONObject response = new JSONObject(new String(((ClientError) error).networkResponse.data));
                        message = response.getString("message");
                    } catch (JSONException ignored) { }

                    new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.settings_create_device_dump_failed_title))
                        .setMessage(getResources().getString(R.string.settings_create_device_dump_failed_message, message))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                        .show();
                });

                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(jsonObjectRequest);
            } catch (JsonProcessingException | JSONException e) {
                new AlertDialog.Builder(getContext())
                    .setTitle(getResources().getString(R.string.settings_create_device_dump_failed_title))
                    .setMessage(getResources().getString(R.string.settings_create_device_dump_failed_message, e.getMessage()))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                    .show();
            }

            return true;
        });

        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("theme")) {
                String theme = sharedPreferences.getString(key, "system");
                assert theme != null;
                switch (theme) {
                    case "dark":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;

                    case "light":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;

                    default:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                }
            }
        });
    }
}
