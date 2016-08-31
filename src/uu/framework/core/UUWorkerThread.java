package uu.framework.core;

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
