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

package org.geysermc.app.android.ui.geyser;

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

import org.geysermc.app.android.BuildConfig;
import org.geysermc.app.android.R;
import org.geysermc.app.android.geyser.GeyserAndroidBootstrap;
import org.geysermc.app.android.geyser.GeyserAndroidLogger;
import org.geysermc.app.android.service.GeyserService;
import org.geysermc.app.android.utils.AndroidUtils;
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

        txtLogs.setMovementMethod(new ScrollingMovementMethod());

        txtLogs.setText(GeyserAndroidLogger.getLog());

        if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
            if (GeyserService.isFinishedStartup()) {
                btnStartStop.setText(container.getResources().getString(R.string.geyser_stop));
            } else {
                btnStartStop.setText(container.getResources().getString(R.string.geyser_starting));
                btnStartStop.setEnabled(false);
            }

            btnConfig.setEnabled(false);

            setupListeners(container);
        }

        btnConfig.setOnClickListener(v -> {
            AndroidUtils.ShowLoader(getContext());

            Intent intent = new Intent(getActivity(), ConfigEditorActivity.class);
            startActivity(intent);
        });

        btnStartStop.setOnClickListener(v -> {
            Button self = (Button) v;
            if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
                Intent serviceIntent = new Intent(getContext(), GeyserService.class);
                getContext().stopService(serviceIntent);

                self.setText(container.getResources().getString(R.string.geyser_start));
                btnConfig.setEnabled(true);
            } else {
                self.setText(container.getResources().getString(R.string.geyser_starting));
                self.setEnabled(false);
                btnConfig.setEnabled(false);

                // Clear all the current disable listeners to preserve memory usage
                GeyserAndroidBootstrap.getOnDisableListeners().clear();

                setupListeners(container);

                Intent serviceIntent = new Intent(getContext(), GeyserService.class);
                ContextCompat.startForegroundService(getContext(), serviceIntent);
            }
        });

        btnCommand.setOnClickListener(v -> {
            if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
                txtCommand.setEnabled(false);
                btnCommand.setEnabled(false);

                // Build the command runnable
                Runnable runnable = () -> {
                    try {
                        ((GeyserAndroidLogger) GeyserConnector.getInstance().getLogger()).runCommand(txtCommand.getText().toString());

                        getActivity().runOnUiThread(() -> {
                            txtCommand.setText("");
                        });
                    } catch (Exception e) {
                        AndroidUtils.showToast(getContext(), "Failed to run command!");
                    }

                    getActivity().runOnUiThread(() -> {
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

    private void setupListeners(ViewGroup container) {
        GeyserAndroidLogger.setListener(line -> {
            if (txtLogs != null) {
                if (BuildConfig.DEBUG) {
                    System.out.println(AndroidUtils.purgeColorCodes(line));
                }

                getActivity().runOnUiThread(() -> {
                    txtLogs.append(AndroidUtils.purgeColorCodes(line) + "\n");
                });
            }
        });

        GeyserAndroidBootstrap.getOnDisableListeners().add(() -> {
            getActivity().runOnUiThread(() -> {
                btnStartStop.setText(container.getResources().getString(R.string.geyser_start));
                btnConfig.setEnabled(true);
            });
        });

        GeyserService.setListener((failed) -> {
            getActivity().runOnUiThread(() -> {
                if (failed) {
                    btnStartStop.setText(container.getResources().getString(R.string.geyser_start));
                    btnStartStop.setEnabled(true);
                    btnConfig.setEnabled(true);
                } else {
                    btnStartStop.setText(container.getResources().getString(R.string.geyser_stop));
                    btnStartStop.setEnabled(true);
                }
            });
        });
    }
}