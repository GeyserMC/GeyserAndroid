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

package org.geysermc.geyser.android.utils;

import android.text.Editable;

import org.geysermc.geyser.android.ui.geyser.user_auths.AuthPreference;

/**
 * This class is used to store various interfaces for event listeners
 */
public class EventListeners {

    /**
     * This is used for adding a listener to the onDisable method of the {@link org.geysermc.geyser.android.geyser.GeyserAndroidBootstrap}
     */
    public interface OnDisableEventListener {
        void onDisable();
    }

    /**
     * This is used for adding a listener to the log events in both
     * {@link org.geysermc.geyser.android.proxy.ProxyLogger} and {@link org.geysermc.geyser.android.geyser.GeyserAndroidLogger}
     */
    public interface LogEventListener {
        void onLogLine(String line);
    }

    /**
     * This is used for when the background service has finished starting
     */
    public interface StartedEventListener {
        void onStarted(boolean failed);
    }

    public interface UserAuthDialogListener {
        void applyTexts(String xboxUsername, String javaUsername, String javaPassword);
    }

    /**
     * This is used for long presses on the {@link AuthPreference}
     */
    public interface OnHoldListener {
        void onLongClick(AuthPreference preference);
    }

    /**
     * This is used for running a function after the text has changed in a {@link android.text.TextWatcher}
     */
    public interface AfterTextChangeListener {
        void afterTextChange(Editable editable);
    }
}
