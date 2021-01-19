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

package org.geysermc.geyser.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geysermc.geyser.android.MainActivity;
import org.geysermc.geyser.android.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AndroidUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static ProgressDialog appLoader;

    /**
     * Open the default browser at a given URL
     *
     * @param url The URL to show
     */
    public static void showURL(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        MainActivity.getContext().startActivity(browserIntent);
    }


    /**
     * Show a toast message with Toast.LENGTH_SHORT
     *
     * @param ctx The app context
     * @param message The message to show
     */
    public static void showToast(Context ctx, String message) {
        showToast(ctx, message, Toast.LENGTH_SHORT);
    }

    /**
     * Show a toast message with the given length
     *
     * @param ctx The app context
     * @param message The message to show
     * @param length The length to show the toast for
     */
    public static void showToast(Context ctx, String message, int length) {
        Toast toast = Toast.makeText(ctx, message, length);
        toast.show();
    }

    /**
     * Read the given file as a string
     *
     * @param file The file to read
     * @return The string contents of the file
     */
    public static String fileToString(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Remove all Minecraft color codes from a string
     *
     * @param line The string to use
     * @return The sanitised string
     */
    public static CharSequence purgeColorCodes(String line) {
        return line.replaceAll("\u00A7[0-9a-fA-F]", "");
    }

    /**
     * Get the storage path based on the config
     *
     * @param ctx The app context
     * @return The path of the chosen storage location
     */
    @SuppressLint("NewApi")
    public static Path getStoragePath(Context ctx) {
        File storageDir = ctx.getFilesDir();

        // Get the current storage preference and change the path accordingly
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        if ("external".equals(preferences.getString("geyser_storage", "internal"))) {
            storageDir = ctx.getExternalFilesDir("");
        }

        assert storageDir != null;
        return Paths.get(storageDir.getPath());
    }

    /**
     * Create a popup loader for the current context
     *
     * @param ctx Context to create the loader for
     */
    public static void ShowLoader(Context ctx) {
        if (appLoader != null && appLoader.isShowing()) {
            appLoader.hide();
        }

        appLoader = new ProgressDialog(ctx);
        appLoader.setTitle(ctx.getString(R.string.utils_loader));
        appLoader.setIndeterminate(false);
        appLoader.setCancelable(true);

        appLoader.show();
    }

    /**
     * Hide the loader if there is one
     */
    public static void HideLoader() {
        if (appLoader != null) {
            appLoader.dismiss();
        }
    }

    /**
     * Run an action on the UI thread.
     * Checks if the activity is not null first.
     *
     * @param activity Activity to run the action on
     * @param action The action to run
     */
    public static void runOnUiThread(Activity activity, Runnable action) {
        if (activity != null) {
            activity.runOnUiThread(action);
        }
    }

    /**
     * Set the clipboard text
     *
     * @param context Context to use
     * @param text Text to store
     */
    public static void setClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getResources().getString(R.string.utils_copy_success), text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Generate a {@link TextWatcher} that calls a {@link org.geysermc.geyser.android.utils.EventListeners.AfterTextChangeListener} after a text change
     *
     * @param listener The listener to call
     * @return The built {@link TextWatcher}
     */
    public static TextWatcher generateAfterTextChange(EventListeners.AfterTextChangeListener listener) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) listener.afterTextChange(s);
            }
        };
    }
}
