package uu.toolbox.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface for delivering BTLE characteristic read events to callers
 */
public interface UUCharacteristicDataDelegate
{
    /**
     * Callback invoked when a BTLE read event is completed.
     *
     * @param peripheral the peripheral being interacted with
     * @param characteristic the characteristic being read
     * @param data the data in the characteristic
     * @param error an error if one occurs
     */
    void onComplete(final @NonNull UUPeripheral peripheral, final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable byte[] data, final @Nullable UUBluetoothError error);
}