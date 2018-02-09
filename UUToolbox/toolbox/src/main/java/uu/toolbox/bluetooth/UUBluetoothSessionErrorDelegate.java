package uu.toolbox.bluetooth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uu.toolbox.bluetooth.classic.UUBluetoothSession;

/**
 * Interface for delivering async results from a UUBluetoothSession action
 */
public interface UUBluetoothSessionErrorDelegate
{
    /**
     * Callback invoked when a session action is completed.
     *
     * @param session the session being interacted with
     * @param error an error if one occurs
     */
    void onComplete(final @NonNull UUBluetoothSession session, final @Nullable UUBluetoothError error);
}
