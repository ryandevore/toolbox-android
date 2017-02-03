package uu.toolbox.bluetooth;

import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface for delivering BTLE characteristic descriptor specific async events to callers
 */
public interface UUDescriptorDelegate
{
    /**
     * Callback invoked when a BTLE event is completed.
     *
     * @param peripheral the peripheral being interacted with
     * @param descriptor the descriptor being interacted with
     * @param error an error if one occurs
     */
    void onComplete(final @NonNull UUPeripheral peripheral, final @NonNull BluetoothGattDescriptor descriptor, final @Nullable UUBluetoothError error);
}