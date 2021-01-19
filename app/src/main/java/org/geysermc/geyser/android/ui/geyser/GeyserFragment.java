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

package org.geysermc.geyser.android.ui.geyser;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.geysermc.geyser.android.BuildConfig;
import org.geysermc.geyser.android.R;
import org.geysermc.geyser.android.geyser.GeyserAndroidBootstrap;
import org.geysermc.geyser.android.geyser.GeyserAndroidLogger;
import org.geysermc.geyser.android.service.GeyserService;
import org.geysermc.geyser.android.ui.geyser.config_editor.ConfigEditorSimpleActivity;
import org.geysermc.geyser.android.utils.AndroidUtils;
import org.geysermc.connector.GeyserConnector;

public class GeyserFragment extends Fragment {

    private Button btnConfig;
    private Button btnStartStop;
    private TextView txtLogs;
    private EditText txtCommand;
    private Button btnCommand;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_geyser, container, false);

        btnConfig = root.findViewById(R.id.btnConfig);
        btnStartStop = root.findViewById(R.id.btnStartStop);
        txtLogs = root.findViewById(R.id.txtLogs);
        txtCommand = root.findViewById(R.id.txtCommand);
        btnCommand = root.findViewById(R.id.btnCommand);

        // Set the movement method and contents for the logs
        txtLogs.setMovementMethod(new ScrollingMovementMethod());
        txtLogs.setText(GeyserAndroidLogger.getLog());

        // Check if the server is already running
        if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
            // Check if the server is still starting
            if (GeyserService.isFinishedStartup()) {
                btnStartStop.setText(container.getResources().getString(R.string.geyser_stop));
            } else {
                btnStartStop.setText(container.getResources().getString(R.string.geyser_starting));
                btnStartStop.setEnabled(false);
            }

            btnConfig.setEnabled(false);

            // Setup the listeners for the current screen
            setupListeners(container);
        }

        btnConfig.setOnClickListener(v -> {
            AndroidUtils.ShowLoader(getContext());

            Intent intent = new Intent(getActivity(), ConfigEditorSimpleActivity.class);
            startActivity(intent);
        });

        btnStartStop.setOnClickListener(v -> {
            Button self = (Button) v;
            if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
                self.setText(container.getResources().getString(R.string.geyser_start));
                btnConfig.setEnabled(true);

                Intent serviceIntent = new Intent(getContext(), GeyserService.class);
                requireContext().stopService(serviceIntent);
            } else {
                self.setText(container.getResources().getString(R.string.geyser_starting));
                self.setEnabled(false);
                btnConfig.setEnabled(false);

                // Clear all the current disable listeners to preserve memory usage
                GeyserAndroidBootstrap.getOnDisableListeners().clear();

                // Setup the listeners for the current screen
                setupListeners(container);

                // Start the Geyser service
                Intent serviceIntent = new Intent(getContext(), GeyserService.class);
                ContextCompat.startForegroundService(requireContext(), serviceIntent);
            }
        });

        btnCommand.setOnClickListener(v -> {
            if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
                // Disable the command input while its processing
                txtCommand.setEnabled(false);
                btnCommand.setEnabled(false);

                // Build the command runnable
                Runnable runnable = () -> {
                    try {
                        // Run the command
                        ((GeyserAndroidLogger) GeyserConnector.getInstance().getLogger()).runCommand(txtCommand.getText().toString());

                        // Clear the command input
                        AndroidUtils.runOnUiThread(getActivity(), () -> txtCommand.setText(""));
                    } catch (Exception e) {
                        // The command failed let the user know
                        AndroidUtils.runOnUiThread(getActivity(), () -> AndroidUtils.showToast(getContext(), getResources().getString(R.string.geyser_command_failed)));
                    }

                    // Re-enable the command input
                    AndroidUtils.runOnUiThread(getActivity(), () -> {
                        txtCommand.setEnabled(true);
                        btnCommand.setEnabled(true);
                    });
                };

                // Run the command in a new thread to prevent it locking up the UI
                Thread commandThread = new Thread(runnable);
                commandThread.start();
            } else {
                AndroidUtils.showToast(getContext(), container.getResources().getString(R.string.geyser_not_running));
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
        GeyserAndroidLogger.setListener(line -> {
            if (txtLogs != null) {
                // If we are in debug then print logs to the console aswell
                if (BuildConfig.DEBUG) {
                    System.out.println(AndroidUtils.purgeColorCodes(line));
                }

                AndroidUtils.runOnUiThread(getActivity(), () -> txtLogs.append(AndroidUtils.purgeColorCodes(line) + "\n"));
            }
        });

        // When the server is disabled toggle the button
        GeyserAndroidBootstrap.getOnDisableListeners().add(() -> AndroidUtils.runOnUiThread(getActivity(), () -> {
            btnStartStop.setText(container.getResources().getString(R.string.geyser_start));
            btnConfig.setEnabled(true);
        }));

        // When the server has started and its failed status
        GeyserService.setListener((failed) -> AndroidUtils.runOnUiThread(getActivity(), () -> {
            if (failed) {
                btnStartStop.setText(container.getResources().getString(R.string.geyser_start));
                btnStartStop.setEnabled(true);
                btnConfig.setEnabled(true);
            } else {
                btnStartStop.setText(container.getResources().getString(R.string.geyser_stop));
                btnStartStop.setEnabled(true);
            }
        }));
    }
}