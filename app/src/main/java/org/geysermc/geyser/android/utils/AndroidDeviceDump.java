/*
 * Copyright (c) 2020-2021 GeyserMC. http://geysermc.org
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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import lombok.Getter;

@Getter
public class AndroidDeviceDump {

    private final AndroidDeviceDump.AndroidInfo androidInfo;
    private final AndroidDeviceDump.AppInfo appInfo;

    public AndroidDeviceDump(Context ctx) {
        this.androidInfo = new AndroidDeviceDump.AndroidInfo();
        this.appInfo = new AndroidDeviceDump.AppInfo(ctx);
    }

    @Getter
    public static class AndroidInfo {

        public final String androidVersion;
        public final int androidAPIVersion;
        public final String deviceManufacturer;
        public final String deviceModel;

        public AndroidInfo() {
            androidVersion = Build.VERSION.RELEASE;
            androidAPIVersion = Build.VERSION.SDK_INT;
            deviceManufacturer = Build.MANUFACTURER;
            deviceModel = Build.MODEL;
        }
    }

    @Getter
    public static class AppInfo {

        public long versionCode = 0;
        public String versionName = "Unknown";

        public AppInfo(Context ctx) {
            try {
                PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
                versionCode = packageInfo.versionCode;
                versionName = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException ignored) { }
        }
    }
}
