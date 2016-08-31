package uu.framework.logging;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

import uu.framework.core.UUWorkerThread;

/**
 * Simple logging wrapper.  Set LOGGING_ENABLED to false to turn off all Logging.  This is useful
 * when you want to disable all Log output for release builds in one common location.
 */
public class UULog
{
    public static final boolean LOGGING_ENABLED = true;
    private static final String NEW_LINE = "\n";

    private static UUWorkerThread workerThread = new UUWorkerThread();

    private UULog()
    {
    }

    public synchronized static void error(final Class callingClass, final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.ERROR, tag, method, exception);
        }
    }

    public synchronized static void error(final Class callingClass, final String method, final String message, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.ERROR, tag, method + ": " + message);
        }
    }

    public static void debug(final Class callingClass, final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.DEBUG, tag, method + ": " + message);
        }
    }

    public synchronized static void debug(final Class callingClass, final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.DEBUG, tag, method, exception);
        }
    }

    public synchronized static void debug(final Class callingClass, final String method, final String message, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.DEBUG, tag, method + ": " + message, exception);
        }
    }

    public static void warn(final Class callingClass, final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.WARN, tag, method + ": " + message);
        }
    }

    public static void error(final Class callingClass, final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.ERROR, tag, method + ": " + message);
        }
    }

    public static void logIntent(final Class callingClass, final String method, final String message, final Intent intent)
    {
        if (LOGGING_ENABLED)
        {
            if (intent != null)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(message);
                sb.append(NEW_LINE);

                String action = intent.getAction();
                sb.append("Action: " + (action != null ? action : "null"));
                sb.append(NEW_LINE);

                Uri data = intent.getData();
                sb.append(("Data: " + (data != null ? data : "null")));
                sb.append(NEW_LINE);

                String scheme = intent.getScheme();
                sb.append(("Scheme: " + (scheme != null ? scheme : "null")));
                sb.append(NEW_LINE);

                sb.append("Categories: ");
                Set<String> categories = intent.getCategories();
                if (categories != null)
                {
                    sb.append(categories.size());
                    sb.append(NEW_LINE);

                    for (String c : categories)
                    {
                        sb.append(c);
                        sb.append(NEW_LINE);
                    }
                }
                else
                {
                    sb.append("null");
                    sb.append(NEW_LINE);
                }

                sb.append("Extras: ");
                Bundle b = intent.getExtras();
                if (b != null)
                {
                    Set<String> keys = b.keySet();
                    if (keys != null)
                    {
                        sb.append(keys.size());
                        sb.append(NEW_LINE);

                        for (String key : keys)
                        {
                            Object val = b.get(key);
                            sb.append(key);
                            sb.append(" => ");

                            if (val != null)
                            {
                                sb.append(val.toString());
                                sb.append(" (" + val.getClass().toString() + ")");
                                sb.append(NEW_LINE);
                            }
                            else
                            {
                                sb.append("null");
                                sb.append(NEW_LINE);
                            }
                        }
                    }
                }
                else
                {
                    sb.append("null");
                    sb.append(NEW_LINE);
                }

                debug(callingClass, method, sb.toString());
            }
        }
    }

    private static void writeToLog(int level, String tag, String logLine)
    {
        writeToLog(level, tag, logLine, null);
    }

    private static void writeToLog(final int level, final String tag, final String logLine, final Throwable exception)
    {
        postRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                threadDoLogWrite(level, tag, logLine, exception);
            }
        });
    }

    private static void postRunnable(final Runnable runnable)
    {
        try
        {
            workerThread.post(runnable);
        }
        catch (Exception ex)
        {
            Log.e("AppLog", "Error posting runnable", ex);
        }
    }

    private static void threadDoLogWrite(int level, String tag, String logLine, Throwable exception)
    {
        try
        {
            if (exception != null)
            {
                logLine += ", Exception: " + exception.toString();
            }

            int expectedToWrite = logLine.length();
            int totalWritten = 0;

            while (totalWritten < expectedToWrite)
            {
                totalWritten += Log.println(level, tag, logLine.substring(totalWritten));

                // On some devices, logging seems to fail and return zero.  In this case, we have to just
                // abort and let the app keep running.
                if (totalWritten <= 0)
                {
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("AppLog", "Error writing to log", ex);
        }
    }
}
