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

package org.geysermc.app.android.ui.geyser.config_editor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import androidx.appcompat.app.ActionBar;

import org.geysermc.app.android.R;
import org.geysermc.app.android.geyser.GeyserAndroidConfiguration;
import org.geysermc.app.android.utils.AndroidUtils;
import org.geysermc.app.android.utils.ConfigUtils;
import org.geysermc.connector.common.serializer.AsteriskSerializer;
import org.geysermc.connector.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import lombok.Setter;

public class ConfigEditorSimpleActivity extends AppCompatActivity {

    private File configFile;

    private GeyserAndroidConfiguration configuration;
    @Setter
    private static boolean configChanged = false;

    private EditText addressText;
    private EditText portText;
    private Spinner authTypeSpinner;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_editor_simple);

        // Enable the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button btnAdvanced = findViewById(R.id.btnAdvanced);

        configFile = AndroidUtils.getStoragePath(getApplicationContext()).resolve("config.yml").toFile();
        configChanged = false;

        if (!configFile.exists()) {
            // Copy the default config from Geyser
            try {
                Files.copy(FileUtils.getResource("config.yml"), configFile.toPath());
            } catch (IOException ignored) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.config_editor_simple_generate_failed_title))
                        .setMessage(getString(R.string.config_editor_simple_generate_failed_message))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            this.finish();
                        })
                        .show();
                return;
            }
        }

        addressText = findViewById(R.id.txtAddress);
        portText = findViewById(R.id.txtPort);
        authTypeSpinner = findViewById(R.id.dpdAuthType);

        btnAdvanced.setOnClickListener(v -> {
            AndroidUtils.ShowLoader(this);

            Intent intent = new Intent(getApplicationContext(), ConfigEditorAdvancedActivity.class);
            startActivity(intent);
        });

        try {
            parseConfig();
        } catch (IOException e) {
            new AlertDialog.Builder(this)
                    .setMessage("Failed to read config")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        this.finish();
                    })
                    .show();
            return;
        }

        AndroidUtils.HideLoader();
    }

    private void parseConfig() throws IOException {
        // Parse the configuration
        configuration = FileUtils.loadConfig(configFile, GeyserAndroidConfiguration.class);

        // Get the available properties from the class
        List<BeanPropertyDefinition> availableProperties = ConfigUtils.getPOJOForClass(GeyserAndroidConfiguration.class);

        for (BeanPropertyDefinition property : availableProperties) {
            // Ignore anything that isn't remote since we're not dealing with that here.
            if (!"remote".equals(property.getName())) continue;
            // Loop the sub class properties
            for (BeanPropertyDefinition subProperty : ConfigUtils.getPOJOForClass(property.getRawPrimaryType())) {
                try {
                    Object subConfig = property.getGetter().callOn(configuration);
                    switch (subProperty.getName()) {
                        case "address":
                            String address = configuration.getRemote().getAddress();
                            if (address.equals("auto")) { // Don't allow auto; it's just going to confuse people
                                address = getString(R.string.default_ip);
                                configChanged = true; // Since the config technically did change
                                configuration.getRemote().setAddress(getString(R.string.default_ip));
                            }
                            addressText.setText(address);
                            addressText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    configChanged = true;

                                    configuration.getRemote().setAddress(s.toString());
                                }
                            });
                            break;
                        case "port":
                            portText.setText(String.valueOf(configuration.getRemote().getPort()));
                            portText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    configChanged = true;

                                    configuration.getRemote().setPort(Integer.parseInt(s.toString()));
                                }
                            });
                            break;
                        case "auth-type":
                            // Create an ArrayAdapter using the string array and a default spinner layout
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                    R.array.config_editor_simple_auth_type_entries, android.R.layout.simple_spinner_item);
                            // Specify the layout to use when the list of choices appears
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            authTypeSpinner.setAdapter(adapter);
                            // Set position based on configuration
                            authTypeSpinner.setSelection(configuration.getRemote().getAuthType().equals("online") ? 0 : 1);
                            authTypeSpinner.setOnItemSelectedListener(new AuthTypeListener(configuration));

                    }
                } catch (Exception e) { }
            }
            break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // The back button
            case android.R.id.home:
                if (checkForChanges()) {
                    super.onBackPressed();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (checkForChanges()) {
            super.onBackPressed();
        }
    }

    private boolean checkForChanges() {
        if (configChanged) {
            AlertDialog confirmDialog = new AlertDialog.Builder(this).create();
            confirmDialog.setTitle("Save");
            confirmDialog.setMessage("Do you wish to save the config?");
            confirmDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", (dialog, id) -> {
                try {
                    AsteriskSerializer.showSensitive = true;

                    // Build and write the updated config yml
                    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    mapper.writeValue(configFile, configuration);

                    AsteriskSerializer.showSensitive = false;

                    this.finish();
                } catch (IOException e) {
                    AndroidUtils.showToast(getApplicationContext(), "Unable to write config!");
                }
            });

            confirmDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Discard", (dialog, id) -> {
                this.finish();
            });

            confirmDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", (dialog, id) -> {
                // Do nothing
            });

            confirmDialog.show();

            return false;
        } else {
            return true;
        }
    }
}