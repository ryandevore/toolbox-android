package uu.toolbox.core;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class UUWorkerThread extends Thread
{
    private static String LOG_TAG = UUWorkerThread.class.getName();

    private Handler handler;

    public UUWorkerThread()
    {
        start();
        safeWait();
    }

    public void post(final Runnable runnable)
    {
        if (handler != null)
        {
            handler.post(runnable);
        }
        else
        {
            Log.d(LOG_TAG, "Handler is null! unable to post!");
        }
    }

    public void postDelayed(final Runnable runnable, final long delay)
    {
        if (handler != null)
        {
            handler.postDelayed(runnable, delay);
        }
        else
        {
            Log.d(LOG_TAG, "Handler is null! unable to post!");
        }
    }

    public void removeRunnable(final Runnable runnable)
    {
        if (handler != null)
        {
            handler.removeCallbacks(runnable);
        }
        else
        {
            Log.d(LOG_TAG, "Handler is null! unable to remove runnable!");
        }
    }

    public void run()
    {
        try
        {
            Looper.myLooper();
            Looper.prepare();
            handler = new Handler();
            safeNotify();
            Looper.loop();
        }
        catch (Exception ex)
        {
            Log.d(LOG_TAG, "Caught exception in run()", ex);
        }
    }

    private void safeWait()
    {
        try
        {
            synchronized (this)
            {
                wait();
            }
        }
        catch (Exception ex)
        {
            Log.d(LOG_TAG, "safeWait", ex);
        }
    }

    private void safeNotify()
    {
        try
        {
            synchronized (this)
            {
                notify();
            }
        }
        catch (Exception ex)
        {
            Log.d(LOG_TAG, "safeNotify", ex);
        }
    }
}
