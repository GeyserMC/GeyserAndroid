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

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geysermc.app.android.R;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SharedPreferences sharedPreferences;
    private Handler handler;

    @Getter
    private ProgressDialog progressDialog;

    @Getter
    private ListPreference branchList;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        /*
        branchList = (ListPreference) findPreference("geyser_branch");

        String storedBranches = sharedPreferences.getString("geyser_branches", "[\"master\"]");
        CharSequence[] branches = new CharSequence[] { "master" };
        try {
            branches = MAPPER.readValue(storedBranches, CharSequence[].class);
        } catch (IOException ignored) { }
        branchList.setEntries(branches);
        branchList.setEntryValues(branches);

        Preference branchRefresh = (Preference) findPreference("branch_refresh");
        branchRefresh.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                progressDialog = ProgressDialog.show(getContext(), "Loading", "Please wait...", true);

                Runnable runnable = () -> updateBranches();
                Thread thread = new Thread(runnable);
                thread.start();

                return true;
            }
        });

        handler = new SettingsHandler(this);
        */
    }

    private void updateBranches() {
        CharSequence[] branches = new CharSequence[] { "master" };
        try {
            // Get the new branch list
            JsonNode jenkinsData = MAPPER.readTree(new URL("https://ci.nukkitx.com/job/GeyserMC/job/Geyser/api/json"));
            List<String> branchesTmpList = new ArrayList<>();
            for (String branch : jenkinsData.get("jobs").findValuesAsText("name")) {
                branchesTmpList.add(URLDecoder.decode(branch, StandardCharsets.UTF_8.name()));
            }

            // Convert the list to the CharSequence[]
            branches = branchesTmpList.toArray(new CharSequence[branchesTmpList.size()]);

            // Update the stored branches
            sharedPreferences.edit().putString("geyser_branches", MAPPER.writeValueAsString(branches)).commit();
        } catch (IOException ignored) { }

        // Close the please wait dialog and update the UI
        handler.sendMessage(handler.obtainMessage(0, branches));
    }
}
