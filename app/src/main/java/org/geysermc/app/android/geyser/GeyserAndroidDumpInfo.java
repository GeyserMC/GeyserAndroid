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

package org.geysermc.app.android.geyser;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import org.geysermc.connector.dump.BootstrapDumpInfo;

import lombok.Getter;

@Getter
public class GeyserAndroidDumpInfo extends BootstrapDumpInfo {

    private AndroidInfo androidInfo;
    private AppInfo appInfo;

    public GeyserAndroidDumpInfo(Context ctx) {
        super();

        this.androidInfo = new AndroidInfo();
        this.appInfo = new AppInfo(ctx);
    }

    @Getter
    private class AndroidInfo {

        public String androidVersion;
        public int androidAPIVersion;
        public String deviceManufacturer;
        public String deviceModel;

        private AndroidInfo() {
            androidVersion = Build.VERSION.RELEASE;
            androidAPIVersion = Build.VERSION.SDK_INT;
            deviceManufacturer = Build.MANUFACTURER;
            deviceModel = Build.MODEL;
        }
    }

    @Getter
    private class AppInfo {

        public long versionCode = 0;
        public String versionName = "Unknown";


        private AppInfo(Context ctx) {
            try {
                PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
                versionCode = packageInfo.versionCode;
                versionName = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) { }
        }
    }
}
