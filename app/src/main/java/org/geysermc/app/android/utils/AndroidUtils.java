package org.geysermc.app.android.utils;

import android.content.Intent;
import android.net.Uri;

import org.geysermc.app.android.MainActivity;

public class AndroidUtils {
    public static void showURL(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        MainActivity.getContext().startActivity(browserIntent);
    }
}
