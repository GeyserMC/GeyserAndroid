package org.geysermc.app.android.ui.proxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.geysermc.app.android.R;
import org.geysermc.app.android.proxy.Logger;
import org.geysermc.app.android.proxy.ProxyServer;

import java.util.Timer;

public class ProxyFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private TextView txtAddress;
    private TextView txtPort;
    private Button btnStartStop;
    private TextView txtLogs;
    private Thread logUpdater;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_proxy, container, false);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        txtAddress = root.findViewById(R.id.txtAddress);
        txtPort = root.findViewById(R.id.txtPort);
        btnStartStop = root.findViewById(R.id.btnStartStop);
        txtLogs = root.findViewById(R.id.txtLogs);

        if (ProxyServer.getInstance() != null && !ProxyServer.getInstance().isShuttingDown()) {
            ProxyServer proxyServer = ProxyServer.getInstance();
            txtAddress.setText(proxyServer.getAddress());
            txtPort.setText(String.valueOf(proxyServer.getPort()));
            btnStartStop.setText(container.getResources().getString(R.string.proxy_stop));

            txtAddress.setEnabled(false);
            txtPort.setEnabled(false);
        } else {
            txtAddress.setText(sharedPreferences.getString("proxy_address", ""));
            txtPort.setText(sharedPreferences.getString("proxy_port", "19132"));
        }

        txtAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                sharedPreferences.edit().putString("proxy_address", ((TextView) v).getText().toString()).commit();
            }
        });

        txtPort.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                sharedPreferences.edit().putString("proxy_port", ((TextView) v).getText().toString()).commit();
            }
        });

        btnStartStop.setOnClickListener(v -> {
            Button self = (Button) v;
            if (ProxyServer.getInstance() != null && !ProxyServer.getInstance().isShuttingDown()) {
                ProxyServer.getInstance().shutdown();
                
                self.setText(container.getResources().getString(R.string.proxy_start));
                txtAddress.setEnabled(true);
                txtPort.setEnabled(true);
            } else {
                Runnable runnable = () -> new ProxyServer(txtAddress.getText().toString(), Integer.parseInt(txtPort.getText().toString()));
                Thread thread = new Thread(runnable);
                thread.start();

                self.setText(container.getResources().getString(R.string.proxy_stop));
                txtAddress.setEnabled(false);
                txtPort.setEnabled(false);
            }
        });

        logUpdater = new Thread() {

            @Override
            public void run() {
                try {
                    while (!logUpdater.isInterrupted()) {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtLogs.setText(Logger.getLog());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        logUpdater.start();

        return root;
    }
}