package com.example.dyckster.sebbiatesttask.utils;

import com.example.dyckster.sebbiatesttask.BuildConfig;
import com.example.dyckster.sebbiatesttask.SebbiaTestTaskApplication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

public final class Log {

    public static final String DEFAULT_TAG = "VezetVsem";
    public static final boolean LOG_ENABLED = BuildConfig.LOG_TO_LOGCAT || BuildConfig.LOG_TO_FILE;
    public static final boolean LOG_TO_LOGCAT = BuildConfig.LOG_TO_LOGCAT;
    public static final boolean LOG_TO_FILE = BuildConfig.LOG_TO_FILE;

    static {
        if (LOG_TO_FILE) {
            AdvancedLogger.initialize(SebbiaTestTaskApplication.getInstance());
        }
    }

    public static void logLongText(String text) {
        v("=========");
        while (text.length() > 0) {
            if (text.length() > 1000) {
                String firstSymbols = text.substring(0, 1000);
                text = text.substring(1000, text.length());
                v(firstSymbols);
            } else {
                v(text);
                text = "";
            }
        }
        v("=========");
    }

    private static String safeTrim(String str) {
        if (str == null)
            return "";
        if (str.length() > 3000) {
            return str.substring(0, 3000);
        }
        return str;
    }

    public static final void i(String tag, String string) {
        string = safeTrim(string);
        if (LOG_TO_FILE) {
            AdvancedLogger.info(String.format(Locale.US, "[%s] %s", tag, string));
        }
        if (LOG_TO_LOGCAT) {
            android.util.Log.i(tag, string);
        }
    }

    public static final void e(String tag, String string) {
        string = safeTrim(string);
        if (LOG_TO_FILE) {
            AdvancedLogger.error(String.format(Locale.US, "[%s] %s", tag, string));
        }
        if (LOG_TO_LOGCAT) {
            android.util.Log.e(tag, string);
        }
    }

    public static final void d(String tag, String string) {
        string = safeTrim(string);
        if (LOG_TO_FILE) {
            AdvancedLogger.debug(String.format(Locale.US, "[%s] %s", tag, string));
        }
        if (LOG_TO_LOGCAT) {
            android.util.Log.d(tag, string);
        }
    }

    public static final void v(String tag, String string) {
        string = safeTrim(string);
        if (LOG_TO_FILE) {
            AdvancedLogger.debug(String.format(Locale.US, "[%s] %s", tag, string));
        }
        if (LOG_TO_LOGCAT) {
            android.util.Log.v(tag, string);
        }
    }

    public static final void w(String tag, String string) {
        string = safeTrim(string);
        if (LOG_TO_FILE) {
            AdvancedLogger.warn(String.format(Locale.US, "[%s] %s", tag, string));
        }
        if (LOG_TO_LOGCAT) {
            android.util.Log.w(tag, string);
        }
    }

    public static final void i(String string) {
        i(DEFAULT_TAG, string);
    }

    public static final void e(String string) {
        e(DEFAULT_TAG, string);
    }

    public static final void d(String string) {
        d(DEFAULT_TAG, string);
    }

    public static final void v(String string) {
        v(DEFAULT_TAG, string);
    }

    public static final void w(String string) {
        w(DEFAULT_TAG, string);
    }

    private static String throwableToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public static final void i(String string, Throwable t) {
        i(DEFAULT_TAG, string + "\n" + throwableToString(t));
    }

    public static final void e(String string, Throwable t) {
        e(DEFAULT_TAG, string + "\n" + throwableToString(t));
    }

    public static final void d(String string, Throwable t) {
        d(DEFAULT_TAG, string + "\n" + throwableToString(t));
    }

    public static final void v(String string, Throwable t) {
        v(DEFAULT_TAG, string + "\n" + throwableToString(t));
    }

    public static final void w(String string, Throwable t) {
        w(DEFAULT_TAG, string + "\n" + throwableToString(t));
    }

}