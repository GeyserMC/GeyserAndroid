package org.geysermc.app.android.ui.geyser;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.geysermc.app.android.R;
import org.geysermc.app.android.utils.AndroidUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigEditorActivity extends AppCompatActivity {

    private EditText txtConfig;
    private String configText = "Unable to locate config, please start the server first!";
    private File configFile;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_editor);

        // Enable the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Load the config
        txtConfig = findViewById(R.id.txtConfig);

        configFile = Paths.get(getApplicationContext().getFilesDir().getPath(), "config.yml").toFile();
        if (configFile.exists()) {
            // Enable horizontal scrolling
            txtConfig.setHorizontallyScrolling(true);

            configText = AndroidUtils.fileToString(configFile);
            txtConfig.setText(configText);
        } else {
            txtConfig.setText(configText);
            txtConfig.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (checkForChanges()) {
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkForChanges() {
        // Check if they have changed any values
        if (!configText.equals(txtConfig.getText().toString())) {
            AlertDialog confirmDialog = new AlertDialog.Builder(this).create();
            confirmDialog.setTitle("Save");
            confirmDialog.setMessage("Do you wish to save the config?");
            confirmDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", (dialog, id) -> {
                try {
                    FileWriter configWriter = new FileWriter(configFile);
                    configWriter.write(txtConfig.getText().toString());
                    configWriter.close();
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

    @Override
    public void onBackPressed() {
        if (checkForChanges()) {
            super.onBackPressed();
        }
    }
}