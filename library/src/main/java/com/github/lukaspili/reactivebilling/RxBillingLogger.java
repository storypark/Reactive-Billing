package com.github.lukaspili.reactivebilling;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by lukasz on 04/05/16.
 */
/**
 * Api adopted from from Timber:
 *
 * Copyright 2013 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@SuppressWarnings("unused")
public final class RxBillingLogger {
    private RxBillingLogger() {/* no instances */}

    /*package*/ static final void setLogger(@NonNull RxBilling.Logger logger) {
        RxBillingLogger.logger = logger;
    }

    /** Log a verbose message with optional format args. */
    public static void v(@NonNull String message, Object... args) {
        prepareLog(Log.VERBOSE, null, message, args);
    }

    /** Log a verbose exception and a message with optional format args. */
    public static void v(Throwable t, @NonNull String message, Object... args) {
        prepareLog(Log.VERBOSE, t, message, args);
    }

    /** Log a verbose exception. */
    public static void v(Throwable t) {
        prepareLog(Log.VERBOSE, t, null);
    }

    /** Log a debug message with optional format args. */
    public static void d(@NonNull String message, Object... args) {
        prepareLog(Log.DEBUG, null, message, args);
    }

    /** Log a debug exception and a message with optional format args. */
    public static void d(Throwable t, @NonNull String message, Object... args) {
        prepareLog(Log.DEBUG, t, message, args);
    }

    /** Log a debug exception. */
    public static void d(Throwable t) {
        prepareLog(Log.DEBUG, t, null);
    }

    /** Log an info message with optional format args. */
    public static void i(@NonNull String message, Object... args) {
        prepareLog(Log.INFO, null, message, args);
    }

    /** Log an info exception and a message with optional format args. */
    public static void i(Throwable t, @NonNull String message, Object... args) {
        prepareLog(Log.INFO, t, message, args);
    }

    /** Log an info exception. */
    public static void i(Throwable t) {
        prepareLog(Log.INFO, t, null);
    }

    /** Log a warning message with optional format args. */
    public static void w(@NonNull String message, Object... args) {
        prepareLog(Log.WARN, null, message, args);
    }

    /** Log a warning exception and a message with optional format args. */
    public static void w(Throwable t, @NonNull String message, Object... args) {
        prepareLog(Log.WARN, t, message, args);
    }

    /** Log a warning exception. */
    public static void w(Throwable t) {
        prepareLog(Log.WARN, t, null);
    }

    /** Log an error message with optional format args. */
    public static void e(@NonNull String message, Object... args) {
        prepareLog(Log.ERROR, null, message, args);
    }

    /** Log an error exception and a message with optional format args. */
    public static void e(Throwable t, @NonNull String message, Object... args) {
        prepareLog(Log.ERROR, t, message, args);
    }

    /** Log an error exception. */
    public static void e(Throwable t) {
        prepareLog(Log.ERROR, t, null);
    }

    /** Log an assert message with optional format args. */
    public static void wtf(@NonNull String message, Object... args) {
        prepareLog(Log.ASSERT, null, message, args);
    }

    /** Log an assert exception and a message with optional format args. */
    public static void wtf(Throwable t, @NonNull String message, Object... args) {
        prepareLog(Log.ASSERT, t, message, args);
    }

    /** Log an assert exception. */
    public static void wtf(Throwable t) {
        prepareLog(Log.ASSERT, t, null);
    }

    /** Log at {@code priority} a message with optional format args. */
    public static void log(int priority, @NonNull String message, Object... args) {
        prepareLog(priority, null, message, args);
    }

    /** Log at {@code priority} an exception and a message with optional format args. */
    public static void log(int priority, Throwable t, @NonNull String message, Object... args) {
        prepareLog(priority, t, message, args);
    }

    /** Log at {@code priority} an exception. */
    public static void log(int priority, Throwable t) {
        prepareLog(priority, t, null);
    }

    private static void prepareLog(int priority, Throwable t, String message, Object... args) {
        if (message != null && message.length() == 0) {
            message = null;
        }
        if (message == null) {
            if (t == null) {
                return; // Swallow message if it's null and there's no throwable.
            }
            message = getStackTraceString(t);
        } else {
            if (args.length > 0) {
                message = String.format(message, args);
            }
            if (t != null) {
                message += "\n" + getStackTraceString(t);
            }
        }

        logger.log(priority, message, t);
    }

    private static String getStackTraceString(Throwable t) {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private static RxBilling.Logger logger = NopLogger.INSTANCE;

    private static final class NopLogger implements RxBilling.Logger {
        private static final RxBilling.Logger INSTANCE = new NopLogger();

        private NopLogger() {}
        @Override public void log(int priority, String message, Throwable t) {}
    }

    public static final class DebugLogger implements RxBilling.Logger {
        public static final RxBilling.Logger INSTANCE = new DebugLogger();

        private static final int MAX_LOG_LENGTH = 4000;
        private static final String DEFAULT_TAG = "RxBilling";

        private DebugLogger() {}

        @Override
        public void log(int priority, String message, Throwable t) {
            if (message.length() < MAX_LOG_LENGTH) {
                if (priority == Log.ASSERT) {
                    Log.wtf(DEFAULT_TAG, message);
                } else {
                    Log.println(priority, DEFAULT_TAG, message);
                }
                return;
            }

            // Split by line, then ensure each line can fit into Log's maximum length.
            for (int i = 0, length = message.length(); i < length; i++) {
                int newline = message.indexOf('\n', i);
                newline = newline != -1 ? newline : length;
                do {
                    int end = Math.min(newline, i + MAX_LOG_LENGTH);
                    String part = message.substring(i, end);
                    if (priority == Log.ASSERT) {
                        Log.wtf(DEFAULT_TAG, part);
                    } else {
                        Log.println(priority, DEFAULT_TAG, part);
                    }
                    i = end;
                } while (i < newline);
            }
        }
    }

}
