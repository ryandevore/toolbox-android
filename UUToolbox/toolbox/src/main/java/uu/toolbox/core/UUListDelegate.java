package uu.toolbox.core;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import uu.toolbox.logging.UULog;


/**
 * UUListDelegate
 *
 * Useful Utilities - Callback interface used to deliver a result list from an async operation.
 *
 */
public interface UUListDelegate<T>
{
    void onCompleted(@Nullable final ArrayList<T> list);

    static <T extends Object> void safeInvoke(@Nullable final UUListDelegate<T> delegate, @Nullable final ArrayList<T> list)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onCompleted(list);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUListDelegate.class, "safeInvoke", ex);
        }
    }
}
