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

package org.geysermc.app.android.ui.geyser.user_auths;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;

import com.fasterxml.jackson.core.type.TypeReference;

import org.geysermc.app.android.R;
import org.geysermc.app.android.utils.AndroidUtils;
import org.geysermc.app.android.utils.UserAuth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static org.geysermc.app.android.utils.AndroidUtils.OBJECT_MAPPER;

public class UserAuthsFragment extends PreferenceFragmentCompat {

    @Getter
    private static Map<String, UserAuth> userAuths = new HashMap();

    @Getter
    private static boolean changed = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());
        setPreferenceScreen(preferenceScreen);

        changed = false;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        TypeReference<HashMap<String, UserAuth>> typeRef = new TypeReference<HashMap<String, UserAuth>>() { };
        try {
            userAuths = OBJECT_MAPPER.readValue(sharedPreferences.getString("geyser_user_auths", "{}"), typeRef);

            generateUserAuthList(preferenceScreen);

            AndroidUtils.HideLoader();
        } catch (IOException e) {
            AndroidUtils.HideLoader();

            // Let the user know the user auths failed to load
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.user_auths_failed_title))
                    .setMessage(getString(R.string.user_auths_failed_message))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        this.getActivity().finish();
                    })
                    .show();
        }
    }

    private void generateUserAuthList(PreferenceScreen preferenceScreen) {
        preferenceScreen.removeAll();

        for (Map.Entry<String, UserAuth> userAuth : userAuths.entrySet()) {
            AuthPreference authPref = new AuthPreference(preferenceScreen.getContext());
            authPref.setTitle(userAuth.getKey());
            authPref.setSummary(userAuth.getValue().getEmail());
            authPref.setKey(userAuth.getKey());
            authPref.setOnPreferenceClickListener(preference -> {
                UserAuthsDialog userAuthsDialog = new UserAuthsDialog();
                userAuthsDialog.setXboxUsername(userAuth.getKey());
                userAuthsDialog.setJavaUsername(userAuth.getValue().getEmail());
                userAuthsDialog.setJavaPassword(userAuth.getValue().getPassword());
                userAuthsDialog.setListener((xboxUsername, javaUsername, javaPassword) -> {
                    changed = true;

                    preference.setTitle(xboxUsername);
                    preference.setSummary(javaUsername);

                    if (!xboxUsername.equals(userAuth.getKey())) {
                        userAuths.remove(userAuth.getKey());
                        userAuths.put(xboxUsername, new UserAuth(javaUsername, javaPassword));

                        generateUserAuthList(preferenceScreen);
                    } else {
                        userAuth.getValue().setEmail(javaUsername);
                        userAuth.getValue().setPassword(javaPassword);
                    }
                });
                userAuthsDialog.show(getParentFragmentManager(), "user_auth_dialog");

                return true;
            });
            authPref.setOnHoldListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.user_auths_delete_dialog_title));
                builder.setMessage(getString(R.string.user_auths_delete_dialog_message));
                builder.setPositiveButton(getString(R.string.user_auths_delete_dialog_positive), (dialog, which) -> {
                    changed = true;
                    dialog.dismiss();
                    userAuths.remove(userAuth.getKey());
                    generateUserAuthList(preferenceScreen);
                });
                builder.setNegativeButton(getString(R.string.user_auths_delete_dialog_negative), (dialog, which) -> {
                    dialog.dismiss();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            });

            preferenceScreen.addPreference(authPref);
        }

        Preference addAuthPref = new Preference(preferenceScreen.getContext());
        addAuthPref.setTitle(getString(R.string.user_auths_new));
        addAuthPref.setIcon(R.drawable.ic_add);
        addAuthPref.setKey("add_auth");
        addAuthPref.setOnPreferenceClickListener(preference -> {
            UserAuthsDialog userAuthsDialog = new UserAuthsDialog();
            userAuthsDialog.setListener((xboxUsername, javaUsername, javaPassword) -> {
                if (!userAuths.containsKey(xboxUsername)) {
                    changed = true;

                    userAuths.put(xboxUsername, new UserAuth(javaUsername, javaPassword));

                    generateUserAuthList(preferenceScreen);
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.user_auths_exists_title))
                            .setMessage(getString(R.string.user_auths_exists_message))
                            .setPositiveButton(getString(android.R.string.ok), null)
                            .show();
                }
            });
            userAuthsDialog.show(getParentFragmentManager(), "user_auth_dialog");

            return true;
        });

        preferenceScreen.addPreference(addAuthPref);
    }
}
