package uu.toolbox.bluetooth;

import android.support.annotation.NonNull;

/**
 * Interface that callers of UUBluetoothScanner can use to manually filter
 * BTLE advertisements
 */
public interface UUPeripheralFilter
{
    enum Result
    {
        IgnoreOnce,
        IgnoreForever,
        Discover
    };

    /**
     * Return true if the peripheral should be included in the scan results
     *
     * @param peripheral the peripheral to check
     *
     * @return value indicating whether the peripheral should be ignored for this one advertisement or forever, or discovered
     */
    Result shouldDiscoverPeripheral(@NonNull UUPeripheral peripheral);
}
