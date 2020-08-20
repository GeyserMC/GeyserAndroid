package org.geysermc.app.android.ui.geyser;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.geysermc.app.android.R;
import org.geysermc.app.android.proxy.ProxyServer;
import org.geysermc.connector.GeyserConnector;

public class GeyserFragment extends Fragment {

    private Button btnConfig;
    private Button btnStartStop;
    private TextView txtLogs;
    private Thread logUpdater;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_geyser, container, false);

        btnConfig = root.findViewById(R.id.btnConfig);
        btnStartStop = root.findViewById(R.id.btnStartStop);
        txtLogs = root.findViewById(R.id.txtLogs);

        txtLogs.setMovementMethod(new ScrollingMovementMethod());

        if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
            //ProxyServer proxyServer = ProxyServer.getInstance();

            btnStartStop.setText(container.getResources().getString(R.string.geyser_stop));
            btnConfig.setEnabled(false);
        }

        btnStartStop.setOnClickListener(v -> {
            Button self = (Button) v;
            if (GeyserConnector.getInstance() != null && !GeyserConnector.getInstance().isShuttingDown()) {
                GeyserConnector.getInstance().shutdown();

                self.setText(container.getResources().getString(R.string.geyser_start));
                btnConfig.setEnabled(true);
            } else {
                /*Runnable runnable = () -> new ProxyServer(txtAddress.getText().toString(), Integer.parseInt(txtPort.getText().toString()));
                Thread thread = new Thread(runnable);
                thread.start();*/

                self.setText(container.getResources().getString(R.string.geyser_stop));
                btnConfig.setEnabled(false);
            }
        });

        return root;
    }
}