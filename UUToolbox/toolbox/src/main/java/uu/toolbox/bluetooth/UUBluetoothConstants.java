package uu.toolbox.bluetooth;

import java.util.UUID;

/**
 * Common bluetooth related constants
 */
@SuppressWarnings("unused")
public class UUBluetoothConstants
{
    /**
     * String formatter for building full 128 UUID's from a 16 bit BTLE shortcode
     */
    public static final String BLUETOOTH_UUID_SHORTCODE_FORMAT = "0000%s-0000-1000-8000-00805F9B34FB";

    /**
     * Client configuration UUID
     */
    public static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = UUBluetooth.shortCodeToUuid("2902");
}
