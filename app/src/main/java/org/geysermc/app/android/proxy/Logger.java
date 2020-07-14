package org.geysermc.app.android.proxy;

import lombok.Getter;

public class Logger {
    @Getter
    private static String log = "";

    public void warning(String message) {
        log += "WARN - " + message + "\n";
        System.out.println("WARN - " + message);
    }

    public void info(String message) {
        log += "INFO - " + message + "\n";
        System.out.println("INFO - " + message);
    }

    public void error(String message, Throwable error) {
        log += "ERROR - " + message + "\n";
        System.out.println("ERROR - " + message + " - " + error.getMessage());
        error.printStackTrace();
    }

    public void debug(String message) {
        log += "DEBUG - " + message + "\n";
        System.out.println("DEBUG - " + message);
    }
}
