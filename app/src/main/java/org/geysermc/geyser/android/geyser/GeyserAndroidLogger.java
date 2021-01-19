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

import org.geysermc.geyser.android.utils.EventListeners;
import org.geysermc.connector.GeyserConnector;
import org.geysermc.connector.command.CommandSender;

import java.io.PrintWriter;
import java.io.StringWriter;

import lombok.Getter;
import lombok.Setter;

public class GeyserAndroidLogger implements org.geysermc.connector.GeyserLogger, CommandSender {

    @Getter
    private static String log = "";

    @Setter
    private static EventListeners.LogEventListener listener;

    @Setter
    @Getter
    private boolean debug = false;

    public void runCommand(String line) {
        GeyserConnector.getInstance().getCommandManager().runCommand(this, line);
    }

    @Override
    public void severe(String message) {
        printConsole("SEVERE - " + message);
    }

    @Override
    public void severe(String message, Throwable error) {
        printConsole("SEVERE - " + message + "\n" + getStackTrace(error));
    }

    @Override
    public void error(String message) {
        printConsole("ERROR - " + message);
    }

    @Override
    public void error(String message, Throwable error) {
        printConsole("ERROR - " + message + "\n" + getStackTrace(error));
    }

    private String getStackTrace(Throwable error) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);

        return sw.toString();
    }

    @Override
    public void warning(String message) {
        printConsole("WARN - " + message);
    }

    @Override
    public void info(String message) {
        printConsole("INFO - " + message);
    }

    @Override
    public void debug(String message) {
        if (debug) {
            printConsole("DEBUG - " + message);
        }
    }

    public static void printConsole(String message) {
        log += message + "\n";
        if (listener != null) listener.onLogLine(message);
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String message) {
        info(message);
    }

    @Override
    public boolean isConsole() {
        return true;
    }
}
