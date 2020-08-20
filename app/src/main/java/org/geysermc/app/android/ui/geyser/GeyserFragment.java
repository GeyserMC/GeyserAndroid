package org.geysermc.app.android.ui.geyser;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_geyser, container, false);

        btnConfig = root.findViewById(R.id.btnConfig);
        btnStartStop = root.findViewById(R.id.btnStartStop);
        txtLogs = root.findViewById(R.id.txtLogs);
        txtCommand = root.findViewById(R.id.txtCommand);
        btnCommand = root.findViewById(R.id.btnCommand);

        txtLogs.setMovementMethod(new ScrollingMovementMethod());

        if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
            btnStartStop.setText(container.getResources().getString(R.string.geyser_stop));
            btnConfig.setEnabled(false);

            GeyserAndroidLogger.setListener(line -> {
                if (txtLogs != null) {
                    txtLogs.append(line + "\n");
                }
            });
        }

        btnStartStop.setOnClickListener(v -> {
            Button self = (Button) v;
            if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
                GeyserConnector.getInstance().shutdown();

                self.setText(container.getResources().getString(R.string.geyser_start));
                btnConfig.setEnabled(true);
            } else {
                Runnable runnable = () -> new GeyserAndroidBootstrap().onEnable(AndroidUtils.readRawTextFile(getContext(), R.raw.config));
                Thread thread = new Thread(runnable);
                thread.start();

                self.setText(container.getResources().getString(R.string.geyser_stop));
                btnConfig.setEnabled(false);

                GeyserAndroidLogger.setListener(line -> {
                    if (txtLogs != null) {
                        txtLogs.append(line + "\n");
                    }
                });
            }
        });

        btnCommand.setOnClickListener(v -> {
            if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
                ((GeyserAndroidLogger)GeyserConnector.getInstance().getLogger()).runCommand(txtCommand.getText().toString());
                txtCommand.setText("");
            } else {
                AndroidUtils.showToast(getContext(), container.getResources().getString(R.string.geyser_not_running));
            }
        });

        return root;
    }
}