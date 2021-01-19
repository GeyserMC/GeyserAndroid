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

package org.geysermc.geyser.android.ui.geyser.user_auths;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import org.geysermc.geyser.android.R;
import org.geysermc.geyser.android.utils.AndroidUtils;

import java.io.IOException;

import static org.geysermc.geyser.android.utils.AndroidUtils.OBJECT_MAPPER;

public class UserAuthsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Show the pretty editor
        setContentView(R.layout.activity_user_auths);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new UserAuthsFragment())
                .commit();
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
        if (UserAuthsFragment.isChanged()) {
            AlertDialog confirmDialog = new AlertDialog.Builder(this).create();
            confirmDialog.setTitle(getResources().getString(R.string.user_auths_save_title));
            confirmDialog.setMessage(getResources().getString(R.string.user_auths_save_message));
            confirmDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.user_auths_save_save), (dialog, id) -> {
                try {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    // Build and write the updated config yml
                    String data = OBJECT_MAPPER.writeValueAsString(UserAuthsFragment.getUserAuths());
                    sharedPreferences.edit().putString("geyser_user_auths", data).apply();

                    this.finish();
                } catch (IOException e) {
                    AndroidUtils.showToast(getApplicationContext(), getResources().getString(R.string.user_auths_save_failed));
                }
            });

            confirmDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.user_auths_save_discard), (dialog, id) -> this.finish());

            confirmDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(android.R.string.cancel), (dialog, id) -> {
                // Do nothing
            });

            confirmDialog.show();

            return false;
        } else {
            return true;
        }
    }
}