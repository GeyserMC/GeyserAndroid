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

package org.geysermc.app.android.ui.proxy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import org.geysermc.app.android.BuildConfig;
import org.geysermc.app.android.R;
import org.geysermc.app.android.proxy.ProxyLogger;
import org.geysermc.app.android.proxy.ProxyServer;
import org.geysermc.app.android.service.ProxyService;
import org.geysermc.app.android.utils.AndroidUtils;

public class ProxyFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private TextView txtAddress;
    private TextView txtPort;
    private Button btnStartStop;
    private TextView txtLogs;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_proxy, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        txtAddress = root.findViewById(R.id.txtAddress);
        txtPort = root.findViewById(R.id.txtPort);
        btnStartStop = root.findViewById(R.id.btnStartStop);
        txtLogs = root.findViewById(R.id.txtLogs);

        // Set the movement method for the logs
        txtLogs.setMovementMethod(new ScrollingMovementMethod());

        // Set the initial text for all the UI elements
        txtLogs.setText(ProxyLogger.getLog());
        txtAddress.setText(sharedPreferences.getString("proxy_address", getResources().getString(R.string.default_ip)));
        txtPort.setText(sharedPreferences.getString("proxy_port", getResources().getString(R.string.default_port_be)));

        // Check if the server is already running
        if (ProxyServer.getInstance() != null && !ProxyServer.getInstance().isShuttingDown()) {
            // Check if the server is still starting
            if (ProxyService.isFinishedStartup()) {
                btnStartStop.setText(container.getResources().getString(R.string.proxy_stop));
            } else {
                btnStartStop.setText(container.getResources().getString(R.string.proxy_starting));
                btnStartStop.setEnabled(false);
            }

            txtAddress.setEnabled(false);
            txtPort.setEnabled(false);

            // Setup the listeners for the current screen
            setupListeners(container);
        }

        // Update the preference when the user has finished changing
        txtAddress.addTextChangedListener(AndroidUtils.generateAfterTextChange((editable) -> sharedPreferences.edit().putString("proxy_address", editable.toString()).apply()));

        // Update the preference when the user has finished changing
        txtPort.addTextChangedListener(AndroidUtils.generateAfterTextChange((editable) -> sharedPreferences.edit().putString("proxy_port", editable.toString()).apply()));

        btnStartStop.setOnClickListener(v -> {
            Button self = (Button) v;
            if (ProxyServer.getInstance() != null && !ProxyServer.getInstance().isShuttingDown()) {
                Intent serviceIntent = new Intent(getContext(), ProxyService.class);
                getContext().stopService(serviceIntent);
                
                self.setText(container.getResources().getString(R.string.proxy_start));
                txtAddress.setEnabled(true);
                txtPort.setEnabled(true);
            } else {
                self.setText(container.getResources().getString(R.string.proxy_starting));
                self.setEnabled(false);
                txtAddress.setEnabled(false);
                txtPort.setEnabled(false);

                // Clear all the current disable listeners to preserve memory usage
                ProxyServer.getOnDisableListeners().clear();

                // Setup the listeners for the current screen
                setupListeners(container);

                // Start the proxy service
                Intent serviceIntent = new Intent(getContext(), ProxyService.class);
                ContextCompat.startForegroundService(getContext(), serviceIntent);
            }
        });

        return root;
    }

    /**
     * Setup the listeners for all the events of the logger and service
     *
     * @param container The container to use for getting resources
     */
    private void setupListeners(ViewGroup container) {
        // When we have a new log line add it to txtLogs
        ProxyLogger.setListener(line -> {
            if (txtLogs != null) {
                // If we are in debug then print logs to the console aswell
                if (BuildConfig.DEBUG) {
                    System.out.println(AndroidUtils.purgeColorCodes(line));
                }

                AndroidUtils.runOnUiThread(getActivity(), () -> txtLogs.append(AndroidUtils.purgeColorCodes(line) + "\n"));
            }
        });

        // When the server is disabled toggle the button
        ProxyServer.getOnDisableListeners().add(() -> AndroidUtils.runOnUiThread(getActivity(), () -> {
            btnStartStop.setText(container.getResources().getString(R.string.proxy_start));
            txtAddress.setEnabled(true);
            txtPort.setEnabled(true);
        }));

        // When the server has started and its failed status
        ProxyService.setListener((failed) -> AndroidUtils.runOnUiThread(getActivity(), () -> {
            if (failed) {
                btnStartStop.setText(container.getResources().getString(R.string.proxy_start));
                btnStartStop.setEnabled(true);
                txtAddress.setEnabled(true);
                txtPort.setEnabled(true);
            } else {
                btnStartStop.setText(container.getResources().getString(R.string.proxy_stop));
                btnStartStop.setEnabled(true);
            }
        }));
    }
}