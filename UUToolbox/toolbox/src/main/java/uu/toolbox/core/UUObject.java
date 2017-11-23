package uu.toolbox.core;

import android.support.annotation.NonNull;

import java.lang.reflect.Method;

import uu.toolbox.logging.UULog;

/**
 * A helpful set of methods for use on the base Object class
 */
public class UUObject
{
    /**
     * Safely invokes a void/void method on an object.
     *
     * @param target object to invoke the method on
     * @param method the method name
     */
    public static void invokeMethod(@NonNull final Object target, @NonNull final String method)
    {
        try
        {
            Method m = target.getClass().getMethod(method);
            m.invoke(target);
        }
        catch (Exception ex)
        {
            UULog.error(UUObject.class, "invokeMethod", ex);
        }
    }

    /**
     * Safely invokes a void/void method on an object from the main thread.
     *
     * @param target object to invoke the method on
     * @param method the method name
     */
    public static void invokeMethodOnMainThread(@NonNull final Object target, @NonNull final String method)
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                invokeMethod(target, method);
            }
        });
    }
}
