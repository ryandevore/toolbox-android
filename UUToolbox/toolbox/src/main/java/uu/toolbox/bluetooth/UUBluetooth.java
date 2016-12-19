package uu.toolbox.bluetooth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;
import java.util.UUID;

import uu.toolbox.core.UUData;
import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

import static uu.toolbox.bluetooth.UUBluetoothConstants.BLUETOOTH_UUID_SHORTCODE_FORMAT;

/**
 * Helpful Bluetooth methods
 */
@SuppressWarnings("unused")
public class UUBluetooth
{
    /**
     * Formats a full 128-bit UUID string from a 16-bit short code string
     *
     * @param shortCode the BTLE short code.  Must be exactly 4 chars long
     *
     * @return a valid UUID string, or null if the short code is not valid.
     */
    public static @Nullable String shortCodeToFullUuidString(final @NonNull String shortCode)
    {
        if (!isValidShortCode(shortCode))
        {
            return null;
        }

        return String.format(Locale.US, BLUETOOTH_UUID_SHORTCODE_FORMAT, shortCode);
    }

    /**
     * Creates a UUID object from a UUID short code string
     *
     * @param shortCode the short code
     *
     * @return a UUID, or null if the short code is not valid.
     */
    public static @Nullable UUID shortCodeToUuid(final @NonNull String shortCode)
    {
        try
        {
            String str = shortCodeToFullUuidString(shortCode);
            if (UUString.isNotEmpty(str))
            {
                return UUID.fromString(str);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUBluetooth.class, "shortCodeToUuid", ex);
        }

        return null;
    }

    /**
     * Checks a string to see if it is a valid BTLE shortcode
     *
     * @param shortCode the string to check
     *
     * @return true if the string is a valid 2 byte hex value
     */
    public static boolean isValidShortCode(final @Nullable String shortCode)
    {
        if (UUString.isEmpty(shortCode))
        {
            return false;
        }

        byte[] hex = UUString.hexToByte(shortCode);
        if (UUData.isEmpty(hex))
        {
            return false;
        }

        return (hex.length == 2);
    }
}
