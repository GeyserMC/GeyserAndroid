package org.geysermc.app.android.proxy;

public class Logger {
    public void warning(String message) {
        System.out.println("WARN - " + message);
    }

    public void info(String message) {
        System.out.println("INFO - " + message);
    }

    public void error(String message, Throwable error) {
        System.out.println("ERROR - " + message + " - " + error.getMessage());
        error.printStackTrace();
    }

    public void debug(String message) {
        System.out.println("DEBUG - " + message);
    }
}
