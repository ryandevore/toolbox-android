package uu.toolbox.core;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import uu.toolbox.logging.UULog;

/**
 * Some handy helpers for dealing with threads and loopers
 */
public class UUThread
{
    /**
     * Checks to see if the currently running thread is the main thread or not
     *
     * @return true if the main thread, false otherwise
     */
    public static boolean isMainThread()
    {
        return (Looper.myLooper() == Looper.getMainLooper());
    }

    /**
     * Safely runs a block of code on the main thread.
     *
     * @param r the runnable.
     */
    public static void runOnMainThread(final @Nullable Runnable r)
    {
        try
        {
            if (isMainThread())
            {
                safeInvokeRunnable(r);
            }
            else
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        runOnMainThread(r);
                    }
                });
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUThread.class, "runOnMainThread", ex);
        }
    }

    private static void safeInvokeRunnable(final @Nullable Runnable r)
    {
        try
        {
            if (r != null)
            {
                try
                {
                    r.run();
                }
                catch (Exception ex)
                {
                    UULog.debug(UUThread.class, "safeInvokeRunnable.run", ex);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUThread.class, "safeInvokeRunnable", ex);
        }
    }


}
