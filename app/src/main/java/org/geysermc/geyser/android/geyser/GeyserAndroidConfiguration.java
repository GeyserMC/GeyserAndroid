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

package org.geysermc.geyser.android.geyser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import org.geysermc.connector.configuration.GeyserJacksonConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static org.geysermc.geyser.android.utils.AndroidUtils.OBJECT_MAPPER;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeyserAndroidConfiguration extends GeyserJacksonConfiguration {

    @Setter
    @JsonIgnore
    private Context context;
    
    @SuppressLint("NewApi")
    @JsonIgnore // This fixes dumps getting a JsonMappingException
    @Override
    public Path getFloodgateKeyPath() {
        return Paths.get(getFloodgateKeyFile());
    }

    @Override
    public Map<String, UserAuthenticationInfo> getUserAuths() {
        Map<String, UserAuthenticationInfo> configValues = super.getUserAuths();

        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            TypeReference<HashMap<String, UserAuthenticationInfo>> typeRef = new TypeReference<HashMap<String, UserAuthenticationInfo>>() { };

            String userAuthsPref = sharedPreferences.getString("geyser_user_auths", "{}");

            if ((configValues == null || configValues.size() == 0) && !"{}".equals(userAuthsPref)) {
                try {
                    return OBJECT_MAPPER.readValue(userAuthsPref, typeRef);
                } catch (IOException ignored) { }
            }
        }

        return configValues;
    }
}
