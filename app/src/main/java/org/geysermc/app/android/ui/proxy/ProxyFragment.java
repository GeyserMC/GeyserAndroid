package org.geysermc.app.android.ui.proxy;

import android.content.Context;
import android.content.SharedPreferences;
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
import org.geysermc.app.android.proxy.ProxyLogger;
import org.geysermc.app.android.proxy.ProxyServer;

public class ProxyFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private TextView txtAddress;
    private TextView txtPort;
    private Button btnStartStop;
    private TextView txtLogs;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_proxy, container, false);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        txtAddress = root.findViewById(R.id.txtAddress);
        txtPort = root.findViewById(R.id.txtPort);
        btnStartStop = root.findViewById(R.id.btnStartStop);
        txtLogs = root.findViewById(R.id.txtLogs);

        txtLogs.setMovementMethod(new ScrollingMovementMethod());

        txtLogs.setText(ProxyLogger.getLog());

        if (ProxyServer.getInstance() != null && !ProxyServer.getInstance().isShuttingDown()) {
            ProxyServer proxyServer = ProxyServer.getInstance();
            txtAddress.setText(proxyServer.getAddress());
            txtPort.setText(String.valueOf(proxyServer.getPort()));
            btnStartStop.setText(container.getResources().getString(R.string.proxy_stop));

            txtAddress.setEnabled(false);
            txtPort.setEnabled(false);

            ProxyLogger.setListener(line -> {
                if (txtLogs != null) {
                    txtLogs.append(line + "\n");
                }
            });
        } else {
            txtAddress.setText(sharedPreferences.getString("proxy_address", getResources().getString(R.string.proxy_default_ip)));
            txtPort.setText(sharedPreferences.getString("proxy_port", getResources().getString(R.string.proxy_default_port)));
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

                ProxyLogger.setListener(line -> {
                    if (txtLogs != null) {
                        txtLogs.append(line + "\n");
                    }
                });
            }
        });

        return root;
    }
}