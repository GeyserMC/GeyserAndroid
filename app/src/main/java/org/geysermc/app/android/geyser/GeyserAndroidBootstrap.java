/*
 * Copyright (c) 2019-2020 GeyserMC. http://geysermc.org
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
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.app.android.geyser;

import android.annotation.SuppressLint;
import android.content.Context;

import org.geysermc.app.android.geyser.command.GeyserCommandManager;
import org.geysermc.connector.GeyserConnector;
import org.geysermc.connector.bootstrap.GeyserBootstrap;
import org.geysermc.connector.command.CommandManager;
import org.geysermc.connector.common.PlatformType;
import org.geysermc.connector.configuration.GeyserConfiguration;
import org.geysermc.connector.dump.BootstrapDumpInfo;
import org.geysermc.connector.ping.GeyserLegacyPingPassthrough;
import org.geysermc.connector.ping.IGeyserPingPassthrough;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.LanguageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class GeyserAndroidBootstrap implements GeyserBootstrap {

    private GeyserCommandManager geyserCommandManager;
    private GeyserAndroidConfiguration geyserConfig;
    private GeyserAndroidLogger geyserLogger;
    private IGeyserPingPassthrough geyserPingPassthrough;

    private GeyserConnector connector;

    private Context ctx;

    public void onEnable(Context ctx) {
        this.ctx = ctx;
        this.onEnable();
    }

    @SuppressLint("NewApi")
    public void onEnable() {
        geyserLogger = new GeyserAndroidLogger();

        try {
            File configFile = FileUtils.fileOrCopiedFromResource(getConfigFolder().resolve("config.yml").toFile(), "config.yml", (x) -> x.replaceAll("generateduuid", UUID.randomUUID().toString()));
            geyserConfig = FileUtils.loadConfig(configFile, GeyserAndroidConfiguration.class);

            // Set the 'auto' server to the test server
            if (this.geyserConfig.getRemote().getAddress().equalsIgnoreCase("auto")) {
                geyserConfig.getRemote().setAddress("test.geysermc.org");
            }
        } catch (IOException ex) {
            geyserLogger.severe(LanguageUtils.getLocaleStringLog("geyser.config.failed"), ex);
            System.exit(0);
        }
        GeyserConfiguration.checkGeyserConfiguration(geyserConfig, geyserLogger);

        connector = GeyserConnector.start(PlatformType.STANDALONE, this);
        geyserCommandManager = new GeyserCommandManager(connector);

        geyserPingPassthrough = GeyserLegacyPingPassthrough.init(connector);
    }

    @Override
    public void onDisable() {
        connector.shutdown();
        System.exit(0);
    }

    @Override
    public GeyserConfiguration getGeyserConfig() {
        return geyserConfig;
    }

    @Override
    public GeyserAndroidLogger getGeyserLogger() {
        return geyserLogger;
    }

    @Override
    public CommandManager getGeyserCommandManager() {
        return geyserCommandManager;
    }

    @Override
    public IGeyserPingPassthrough getGeyserPingPassthrough() {
        return geyserPingPassthrough;
    }

    @SuppressLint("NewApi")
    @Override
    public Path getConfigFolder() {
        // Return the current working directory
        return Paths.get(ctx.getFilesDir().getPath());
    }

    @Override
    public BootstrapDumpInfo getDumpInfo() {
        return new BootstrapDumpInfo();
    }
}
