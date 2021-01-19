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

package org.geysermc.geyser.android.ui.geyser.config_editor;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;

import org.geysermc.geyser.android.geyser.GeyserAndroidConfiguration;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthTypeListener extends Activity implements AdapterView.OnItemSelectedListener {

    GeyserAndroidConfiguration configuration;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Set to lower case, then trim
        String authType = parent.getAdapter().getItem(position).toString().toLowerCase().trim();

        if (!authType.equals(configuration.getRemote().getAuthType())) {
            ConfigEditorSimpleActivity.setConfigChanged(true);
        }

        configuration.getRemote().setAuthType(authType);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
