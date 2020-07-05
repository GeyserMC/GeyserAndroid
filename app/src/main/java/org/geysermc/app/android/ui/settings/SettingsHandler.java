package org.geysermc.app.android.ui.settings;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class SettingsHandler extends Handler {
    private final WeakReference<SettingsFragment> settingsFragment;

    public SettingsHandler(SettingsFragment settingsFragment) {
        this.settingsFragment = new WeakReference<SettingsFragment>(settingsFragment);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                SettingsFragment fragment = settingsFragment.get();

                // Update the dropdown
                CharSequence[] branches = (CharSequence[]) msg.obj;
                fragment.getBranchList().setEntries(branches);
                fragment.getBranchList().setEntryValues(branches);

                // Close the progress dialog
                fragment.getProgressDialog().dismiss();
            default:
                break;
        }

    }
}
