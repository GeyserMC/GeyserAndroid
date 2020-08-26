package org.geysermc.app.android.ui.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

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

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

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
