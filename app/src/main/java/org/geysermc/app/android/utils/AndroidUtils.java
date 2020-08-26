package org.geysermc.app.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.geysermc.app.android.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AndroidUtils {

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
            String line = null;
            String ls = System.getProperty("line.separator");
            while (true) {
                if (!((line = reader.readLine()) != null)) break;
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
}
