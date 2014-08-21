package com.golovin.notes.log;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    private static boolean DEBUG = true;

    private static boolean DEBUG_WITH_STACKTRACE = false;

    private enum LogType {
        DEBUG,
        WARNING,
        ERROR
    }

    public static <T> void logDebug(Class<T> cls, String message) {
        log(cls, message, LogType.DEBUG);
    }

    public static <T> void logWarning(Class<T> cls, String message) {
        log(cls, message, LogType.WARNING);
    }

    public static <T> void logError(Class<T> cls, String message) {
        log(cls, message, LogType.ERROR);
    }

    public static <T> void logError(Class<T> cls, String message, Throwable e) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();
            Log.e(tag, "-----");
            Log.e(tag, String.format("%s: %s", LogType.ERROR, message), e);

            if (DEBUG_WITH_STACKTRACE) {
                Log.e(tag, getStackTrace());
            }
        }
    }

    private static <T> void log(Class<T> cls, String message, LogType logType) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();

            Log.d(tag, "-----");
            Log.d(tag, String.format("%s: %s", logType, message));

            if (DEBUG_WITH_STACKTRACE) {
                Log.d(tag, getStackTrace());
            }
        }
    }

    private static String getStackTrace() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        new Throwable().printStackTrace(pw);

        return sw.toString();
    }
}