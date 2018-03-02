package uu.toolbox.core;

import android.support.annotation.Nullable;


/**
 * UUErrorDelegate
 *
 * Useful Utilities - Callback interface used to deliver a UUError object from an async operation.
 *
 */
public interface UUErrorDelegate
{
    void onCompleted(@Nullable final UUError error);
}

