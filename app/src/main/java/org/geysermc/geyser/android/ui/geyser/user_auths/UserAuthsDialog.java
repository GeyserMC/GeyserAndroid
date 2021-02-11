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

package org.geysermc.geyser.android.ui.geyser.user_auths;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.geysermc.geyser.android.R;
import org.geysermc.geyser.android.utils.EventListeners;

import lombok.Setter;

public class UserAuthsDialog extends AppCompatDialogFragment {

    private EditText txtXbox;
    private EditText txtUser;
    private EditText txtPass;
    private Switch swMicrosoft;

    @Setter
    private String xboxUsername = "";

    @Setter
    private String javaUsername = "";

    @Setter
    private String javaPassword = "";

    @Setter
    private boolean microsoftAccount = false;

    @Setter
    private EventListeners.UserAuthDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_user_auth, null);
        builder.setView(view);

        // Set the title and buttons
        builder.setTitle(getResources().getString(R.string.user_auth_dialog_title))
                .setNegativeButton(getResources().getString(R.string.user_auth_dialog_negative), null)
                .setPositiveButton(getResources().getString((R.string.user_auth_dialog_positive)), (dialog, which) -> {
                    if (listener != null) {
                        listener.applyTexts(txtXbox.getText().toString(), txtUser.getText().toString(), txtPass.getText().toString(), swMicrosoft.isChecked());
                    }
                });

        txtXbox = view.findViewById(R.id.txtXbox);
        txtXbox.setText(xboxUsername);

        txtUser = view.findViewById(R.id.txtUser);
        txtUser.setText(javaUsername);

        txtPass = view.findViewById(R.id.txtPass);
        txtPass.setText(javaPassword);

        swMicrosoft = view.findViewById(R.id.swMicrosoft);
        swMicrosoft.setChecked(microsoftAccount);

        return builder.create();
    }
}
