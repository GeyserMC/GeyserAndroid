package org.geysermc.app.android.ui.geyser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.geysermc.app.android.R;

public class GeyserFragment extends Fragment {

    private GeyserViewModel geyserViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        geyserViewModel =
                ViewModelProviders.of(this).get(GeyserViewModel.class);
        View root = inflater.inflate(R.layout.fragment_geyser, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        geyserViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}