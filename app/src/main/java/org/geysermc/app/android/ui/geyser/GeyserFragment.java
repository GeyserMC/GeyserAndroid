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
import androidx.fragment.app.Fragment;

import org.geysermc.app.android.BuildConfig;
import org.geysermc.app.android.R;
import org.geysermc.app.android.geyser.GeyserAndroidBootstrap;
import org.geysermc.app.android.geyser.GeyserAndroidLogger;
import org.geysermc.app.android.utils.AndroidUtils;
import org.geysermc.connector.GeyserConnector;


public class GeyserFragment extends Fragment {

    private Button btnConfig;
    private Button btnStartStop;
    private TextView txtLogs;
    private EditText txtCommand;
    private Button btnCommand;
    private Thread statusUpdater;

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
            btnStartStop.setText(container.getResources().getString(R.string.geyser_stop));
            btnConfig.setEnabled(false);

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
        }

        btnConfig.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ConfigEditorActivity.class);
            startActivity(intent);
        });

        btnStartStop.setOnClickListener(v -> {
            Button self = (Button) v;
            if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
                // Catch any errors to prevent the app crashing
                try {
                    GeyserConnector.getInstance().shutdown();
                } catch (Exception ignored) { }

                self.setText(container.getResources().getString(R.string.geyser_start));
                btnConfig.setEnabled(true);
            } else {
                Runnable runnable = () -> new GeyserAndroidBootstrap().onEnable(getContext());
                Thread thread = new Thread(runnable);
                thread.start();

                self.setText(container.getResources().getString(R.string.geyser_stop));
                btnConfig.setEnabled(false);

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
            }
        });

        btnCommand.setOnClickListener(v -> {
            if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
                try {
                    ((GeyserAndroidLogger)GeyserConnector.getInstance().getLogger()).runCommand(txtCommand.getText().toString());
                    txtCommand.setText("");
                } catch (Exception e) {
                    AndroidUtils.showToast(getContext(), "Failed to run command!");
                }
            } else {
                AndroidUtils.showToast(getContext(), container.getResources().getString(R.string.geyser_not_running));
            }
        });

        // Create a thread that runs every 1s updating the log text
        statusUpdater = new Thread() {
            @Override
            public void run() {
                try {
                    while (!statusUpdater.isInterrupted()) {
                        Thread.sleep(1000);
                        if (GeyserConnector.getInstance() != null && GeyserConnector.getInstance().getAuthType() == null) {
                            getActivity().runOnUiThread(() -> {
                                btnStartStop.setText(container.getResources().getString(R.string.geyser_start));
                                btnConfig.setEnabled(true);
                            });
                        }
                    }
                } catch (InterruptedException | NullPointerException e) { }
            }
        };

        statusUpdater.start();

        return root;
    }
}