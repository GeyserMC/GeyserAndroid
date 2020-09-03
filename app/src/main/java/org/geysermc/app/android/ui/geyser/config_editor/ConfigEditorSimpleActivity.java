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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.geysermc.app.android.R;
import org.geysermc.app.android.utils.AndroidUtils;
import org.geysermc.connector.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigEditorSimpleActivity extends AppCompatActivity {

    private File configFile;

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

        btnAdvanced.setOnClickListener(v -> {
            AndroidUtils.ShowLoader(this);

            Intent intent = new Intent(getApplicationContext(), ConfigEditorAdvancedActivity.class);
            startActivity(intent);
        });

        AndroidUtils.HideLoader();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // The back button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}