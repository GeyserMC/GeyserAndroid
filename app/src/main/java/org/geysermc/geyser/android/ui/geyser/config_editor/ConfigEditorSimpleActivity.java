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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import androidx.appcompat.app.ActionBar;

import org.geysermc.geyser.android.R;
import org.geysermc.geyser.android.geyser.GeyserAndroidConfiguration;
import org.geysermc.geyser.android.ui.geyser.user_auths.UserAuthsActivity;
import org.geysermc.geyser.android.utils.AndroidUtils;
import org.geysermc.geyser.android.utils.ConfigUtils;
import org.geysermc.connector.common.serializer.AsteriskSerializer;
import org.geysermc.connector.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import lombok.Setter;

public class ConfigEditorSimpleActivity extends AppCompatActivity {

    private File configFile;

    private GeyserAndroidConfiguration configuration;

    @Setter
    private static boolean configChanged = false;

    private EditText txtAddress;
    private EditText txtPort;
    private Spinner dpdAuthType;

    private TextWatcher txtPortWatcher;
    private TextWatcher txtAddressWatcher;

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

        txtAddress = findViewById(R.id.txtAddress);
        txtPort = findViewById(R.id.txtPort);
        dpdAuthType = findViewById(R.id.dpdAuthType);
        Button btnUserAuths = findViewById(R.id.btnUserAuths);
        Button btnAdvanced = findViewById(R.id.btnAdvanced);

        Path configPath = AndroidUtils.getStoragePath(getApplicationContext()).resolve("config.yml");
        configFile = configPath.toFile();

        if (!configFile.exists()) {
            // Copy the default config from Geyser
            try {
                Files.copy(FileUtils.getResource("config.yml"), configPath);
            } catch (IOException ignored) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.config_editor_simple_generate_failed_title))
                        .setMessage(getResources().getString(R.string.config_editor_simple_generate_failed_message))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> this.finish())
                        .show();
                return;
            }
        }

        btnUserAuths.setOnClickListener(v -> {
            AndroidUtils.ShowLoader(this);

            Intent intent = new Intent(getApplicationContext(), UserAuthsActivity.class);
            startActivity(intent);
        });

        btnAdvanced.setOnClickListener(v -> {
            AndroidUtils.ShowLoader(this);

            Intent intent = new Intent(getApplicationContext(), ConfigEditorAdvancedActivity.class);
            startActivity(intent);
        });

        try {
            parseConfig();
        } catch (IOException e) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.config_editor_failed_title))
                    .setMessage(getResources().getString(R.string.config_editor_failed_message))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> this.finish())
                    .show();
            return;
        }

        AndroidUtils.HideLoader();
    }

    private void parseConfig() throws IOException {
        // Parse the configuration
        configuration = FileUtils.loadConfig(configFile, GeyserAndroidConfiguration.class);
        GeyserAndroidConfiguration configurationOrig = FileUtils.loadConfig(configFile, GeyserAndroidConfiguration.class);

        configChanged = false;

        // Get the available properties from the class
        List<BeanPropertyDefinition> availableProperties = ConfigUtils.getPOJOForClass(GeyserAndroidConfiguration.class);

        for (BeanPropertyDefinition property : availableProperties) {
            // Ignore anything that isn't remote since we're not dealing with that here.
            if (!"remote".equals(property.getName())) continue;
            // Loop the sub class properties
            for (BeanPropertyDefinition subProperty : ConfigUtils.getPOJOForClass(property.getRawPrimaryType())) {
                try {
                    switch (subProperty.getName()) {
                        case "address":
                            String address = configuration.getRemote().getAddress();
                            if (address.equals("auto")) { // Don't allow auto; it's just going to confuse people
                                address = getResources().getString(R.string.default_ip);
                                configChanged = true; // Since the config technically did change
                                configuration.getRemote().setAddress(getResources().getString(R.string.default_ip));
                            }

                            // Clear the existing before making a new one
                            if (txtAddressWatcher != null) {
                                txtAddress.removeTextChangedListener(txtAddressWatcher);
                            }

                            txtAddress.setText(address);

                            txtAddress.addTextChangedListener(txtAddressWatcher = AndroidUtils.generateAfterTextChange((editable) -> {
                                if (!editable.toString().equals(configurationOrig.getRemote().getAddress())) {
                                    configChanged = true;
                                }

                                configuration.getRemote().setAddress(editable.toString());
                            }));
                            break;
                        case "port":
                            // Clear the existing before making a new one
                            if (txtPortWatcher != null) {
                                txtPort.removeTextChangedListener(txtPortWatcher);
                            }

                            txtPort.setText(String.valueOf(configuration.getRemote().getPort()));

                            txtPort.addTextChangedListener(txtPortWatcher = AndroidUtils.generateAfterTextChange((editable) -> {
                                if (!editable.toString().equals(String.valueOf(configurationOrig.getRemote().getPort()))) {
                                    configChanged = true;
                                }

                                // Make sure the port isn't empty and if it is use default
                                if (editable.toString().isEmpty()) {
                                    configuration.getRemote().setPort(Integer.parseInt(getResources().getString(R.string.default_port_be)));
                                } else {
                                    configuration.getRemote().setPort(Integer.parseInt(editable.toString()));
                                }
                            }));
                            break;
                        case "auth-type":
                            // Create an ArrayAdapter using the string array and a default spinner layout
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.config_editor_simple_auth_type_entries, android.R.layout.simple_spinner_item);

                            // Specify the layout to use when the list of choices appears
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            dpdAuthType.setAdapter(adapter);

                            // Set position based on configuration
                            dpdAuthType.setSelection(configuration.getRemote().getAuthType().equals("online") ? 0 : 1);
                            dpdAuthType.setOnItemSelectedListener(new AuthTypeListener(configuration));
                            break;
                        default:
                            break;
                    }
                } catch (Exception ignored) { }
            }
            break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The back button
        if (item.getItemId() == android.R.id.home) {
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
            confirmDialog.setTitle(getResources().getString(R.string.config_editor_save_title));
            confirmDialog.setMessage(getResources().getString(R.string.config_editor_save_message));
            confirmDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.config_editor_save_save), (dialog, id) -> {
                try {
                    AsteriskSerializer.showSensitive = true;

                    // Build and write the updated config yml
                    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    mapper.writeValue(configFile, configuration);

                    AsteriskSerializer.showSensitive = false;

                    this.finish();
                } catch (IOException e) {
                    AndroidUtils.showToast(getApplicationContext(), getResources().getString(R.string.config_editor_save_failed));
                }
            });

            confirmDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.config_editor_save_discard), (dialog, id) -> this.finish());

            confirmDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(android.R.string.cancel), (dialog, id) -> {
                // Do nothing
            });

            confirmDialog.show();

            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        // Try to reparse the config
        try {
            parseConfig();
        } catch (IOException ignored) { }
        super.onResume();
    }
}