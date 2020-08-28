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

package org.geysermc.app.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import org.geysermc.app.android.MainActivity;
import org.geysermc.app.android.R;
import org.geysermc.app.android.proxy.ProxyServer;

import lombok.Getter;

public class ProxyService extends Service {

    private final int NOTIFCATION_ID = 1338;
    private final String ACTION_STOP_SERVICE = "STOP_PROXY_SERVICE";

    private ProxyServer proxy;

    @Getter
    private static boolean running = false;

    @Override
    public void onCreate() {
        super.onCreate();

        running = true;

        Intent stopSelf = new Intent(this, ProxyService.class);
        stopSelf.setAction(this.ACTION_STOP_SERVICE);

        PendingIntent stopPendingIntent = PendingIntent.getService(this, NOTIFCATION_ID + 1, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent openLogs = new Intent(this, MainActivity.class);
        openLogs.putExtra("nav_element", R.id.nav_proxy);

        PendingIntent openLogsPendingIntent = PendingIntent.getActivity(this, NOTIFCATION_ID + 2, openLogs, 0);

        Notification notification = new NotificationCompat.Builder(this, "proxy_channel")
                .setSmallIcon(R.drawable.ic_menu_proxy)
                .setContentTitle(getString(R.string.proxy_background_notification))
                .addAction(R.drawable.ic_menu_manage, getString(R.string.proxy_stop), stopPendingIntent)
                .addAction(R.drawable.ic_notification_logs, getString(R.string.proxy_logs), openLogsPendingIntent)
                .build();

        createNotificationChannel();
        startForeground(NOTIFCATION_ID, notification);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        proxy = new ProxyServer(sharedPreferences.getString("proxy_address", getResources().getString(R.string.proxy_default_ip)), Integer.parseInt(sharedPreferences.getString("proxy_port", getResources().getString(R.string.proxy_default_port))));
        proxy.getOnDisableListeners().add(this::stopSelf);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        running = false;

        // Catch any errors to prevent the app crashing
        try {
            ProxyServer.getInstance().onDisable();
        } catch (Exception ignored) { }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            stopSelf();
        } else {
            Runnable runnable = () -> proxy.onEnable();
            Thread thread = new Thread(runnable);
            thread.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("proxy_channel", getString(R.string.menu_proxy), NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
