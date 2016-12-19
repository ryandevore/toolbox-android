package uu.toolbox.bluetooth;

import android.support.annotation.NonNull;

/**
 * Interface that callers of UUBluetoothScanner can use to manually filter
 * BTLE advertisements
 */
public interface UUPeripheralFilter
{
    /**
     * Return true if the peripheral should be included in the scan results
     *
     * @param peripheral the peripheral to check
     *
     * @return true or false
     */
    boolean shouldDiscoverPeripheral(@NonNull UUPeripheral peripheral);
}
