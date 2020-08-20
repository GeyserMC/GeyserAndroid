package org.geysermc.app.android.proxy;

import lombok.Getter;
import lombok.Setter;

public class ProxyLogger {

    @Getter
    private static String log = "";

    @Setter
    private static LogEventListener listener;

    public interface LogEventListener {
        void onLogLine (String line);
    }

    public void warning(String message) {
        log += "WARN - " + message + "\n";
        if (listener != null) listener.onLogLine("WARN - " + message);
        // System.out.println("WARN - " + message);
    }

    public void info(String message) {
        log += "INFO - " + message + "\n";
        if (listener != null) listener.onLogLine("INFO - " + message);
        // System.out.println("INFO - " + message);
    }

    public void error(String message, Throwable error) {
        log += "ERROR - " + message + "\n";
        if (listener != null) listener.onLogLine("ERROR - " + message);
        // System.out.println("ERROR - " + message + " - " + error.getMessage());
        // error.printStackTrace();
    }

    public void debug(String message) {
        log += "DEBUG - " + message + "\n";
        if (listener != null) listener.onLogLine ("DEBUG - " + message);
        // System.out.println("DEBUG - " + message);
    }
}
