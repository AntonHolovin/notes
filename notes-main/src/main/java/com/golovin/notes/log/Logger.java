package com.golovin.notes.log;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
    private static boolean DEBUG = true;
    private static boolean DEBUG_WITH_STACKTRACE = false;

    public static <T> void logDebug(Class cls, String message) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();
            Log.d(tag, "-----");
            Log.d(tag, LogType.DEBUG + ": " + message);

            if (DEBUG_WITH_STACKTRACE) {
                Log.d(tag, getStackTrace());
            }
        }
    }

    public static <T> void logWarning(Class cls, String message) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();
            Log.w(tag, "-----");
            Log.w(tag, LogType.WARNING + ": " + message);

            if (DEBUG_WITH_STACKTRACE) {
                Log.w(tag, getStackTrace());
            }
        }
    }

    public static <T> void logError(Class cls, String message) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();
            Log.e(tag, "-----");
            Log.e(tag, LogType.ERROR + ": " + message);

            if (DEBUG_WITH_STACKTRACE) {
                Log.e(tag, getStackTrace());
            }
        }
    }

    public static <T> void logError(Class cls, String message, Throwable e) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();
            Log.e(tag, "-----");
            Log.e(tag, LogType.ERROR + ": " + message, e);

            if (DEBUG_WITH_STACKTRACE) {
                Log.e(tag, getStackTrace());
            }
        }
    }

    private enum LogType {
        DEBUG,
        WARNING,
        ERROR
    }

    private static String getStackTrace() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        new Throwable().printStackTrace(pw);

        return sw.toString();
    }
}