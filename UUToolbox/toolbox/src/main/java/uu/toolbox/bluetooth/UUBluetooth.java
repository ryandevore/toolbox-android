package uu.toolbox.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import uu.toolbox.core.UUData;
import uu.toolbox.core.UUInteger;
import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

import static uu.toolbox.bluetooth.UUBluetoothConstants.BLUETOOTH_SPEC_NAMES;
import static uu.toolbox.bluetooth.UUBluetoothConstants.BLUETOOTH_UUID_SHORTCODE_FORMAT;

/**
 * Helpful Bluetooth methods, constants, interfaces
 */
@SuppressWarnings("unused")
public class UUBluetooth
{
    /**
     * Formats a full 128-bit UUID string from a 16-bit short code string
     *
     * @param shortCode the BTLE short code.  Must be exactly 4 chars long
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

    /**
     * Returns a developer friendly string for a BluetoothGatt.GATT_* response code
     *
     * @param gattStatus any integer, but assumes one of BluetoothGatt.GATT_* codes
     * @return a string
     */
    public static String gattStatusToString(final int gattStatus)
    {
        switch (gattStatus)
        {
            case BluetoothGatt.GATT_SUCCESS:
                return "Success";

            case BluetoothGatt.GATT_READ_NOT_PERMITTED:
                return "ReadNotPermitted";

            case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
                return "WriteNotPermitted";

            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
                return "InsufficientAuthentication";

            case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED:
                return "RequestNotSupported";

            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                return "InsufficientEncryption";

            case BluetoothGatt.GATT_INVALID_OFFSET:
                return "InvalidOffset";

            case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH:
                return "InvalidAttributeLength";

            case BluetoothGatt.GATT_CONNECTION_CONGESTED:
                return "ConnectionCongested";

            case BluetoothGatt.GATT_FAILURE:
                return "Failure";

            case UUBluetoothConstants.GATT_ERROR:
                return "GattError";

            case UUBluetoothConstants.GATT_DISCONNECTED_BY_PERIPHERAL:
                return "DisconnectedByPeripheral";

            default:
                return String.format(Locale.US, "Unknown-%d", gattStatus);
        }
    }

    /**
     * Returns a developer friendly string for a BluetoothProfile.STATE_* response code
     *
     * @param connectionState any integer, but assumes one of BluetoothProfile.STATE_* codes
     * @return a string
     */
    public static String connectionStateToString(final int connectionState)
    {
        switch (connectionState)
        {
            case BluetoothProfile.STATE_CONNECTED:
                return "Connected";

            case BluetoothProfile.STATE_CONNECTING:
                return "Connecting";

            case BluetoothProfile.STATE_DISCONNECTING:
                return "Disconnecting";

            case BluetoothProfile.STATE_DISCONNECTED:
                return "Disconnected";

            default:
                return String.format(Locale.US, "Unknown-%d", connectionState);
        }
    }

    /**
     * Returns a common name for a Bluetooth UUID.  These strings are directly
     * from the bluetooth.org website
     *
     * @param uuid the UUID to check
     * @return a string
     */
    public static @NonNull String bluetoothSpecName(final @Nullable UUID uuid)
    {
        if (uuid == null)
        {
            return "Unknown";
        }

        if (BLUETOOTH_SPEC_NAMES.containsKey(uuid))
        {
            return BLUETOOTH_SPEC_NAMES.get(uuid);
        }

        return "Unknown";
    }

    /**
     * Formats a string with human friendly of BluetoothGattCharacteristics
     *
     * @param properties properties bitmask
     * @return a string
     */
    public static @NonNull String characteristicPropertiesToString(final int properties)
    {
        ArrayList<String> parts = new ArrayList<>();

        if (UUInteger.isBitSet(properties, BluetoothGattCharacteristic.PROPERTY_BROADCAST))
        {
            parts.add("Broadcast");
        }

        if (UUInteger.isBitSet(properties, BluetoothGattCharacteristic.PROPERTY_READ))
        {
            parts.add("Read");
        }

        if (UUInteger.isBitSet(properties, BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE))
        {
            parts.add("WriteWithoutResponse");
        }

        if (UUInteger.isBitSet(properties, BluetoothGattCharacteristic.PROPERTY_WRITE))
        {
            parts.add("Write");
        }

        if (UUInteger.isBitSet(properties, BluetoothGattCharacteristic.PROPERTY_NOTIFY))
        {
            parts.add("Notify");
        }

        if (UUInteger.isBitSet(properties, BluetoothGattCharacteristic.PROPERTY_INDICATE))
        {
            parts.add("Indicate");
        }

        if (UUInteger.isBitSet(properties, BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE))
        {
            parts.add("SignedWrite");
        }

        if (UUInteger.isBitSet(properties, BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS))
        {
            parts.add("ExtendedProperties");
        }

        return UUString.componentsJoinedByString(parts, ", ");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Shared Bluetooth Gatt management
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static HashMap<String, UUBluetoothGatt> gattHashMap = new HashMap<>();

    static UUBluetoothGatt gattForPeripheral(final @NonNull UUPeripheral peripheral)
    {
        UUBluetoothGatt gatt = null;

        String address = peripheral.getAddress();
        if (UUString.isNotEmpty(address))
        {
            if (gattHashMap.containsKey(address))
            {
                gatt = gattHashMap.get(address);
            }

            if (gatt == null)
            {
                gatt = new UUBluetoothGatt(peripheral);
                gattHashMap.put(address, gatt);
            }
        }

        return gatt;
    }

    public static void connectPeripheral(
            final @NonNull Context context,
            final @NonNull UUPeripheral peripheral,
            final boolean connectGattAutoFlag,
            final long timeout,
            final @NonNull UUConnectionDelegate delegate)
    {
        UUBluetoothGatt gatt = gattForPeripheral(peripheral);
        if (gatt != null)
        {
            gatt.connect(context, connectGattAutoFlag, timeout, delegate);
        }
    }

    public static void disconnectPeripheral(final @NonNull UUPeripheral peripheral)
    {
        UUBluetoothGatt gatt = gattForPeripheral(peripheral);
        if (gatt != null)
        {
            gatt.disconnect();
        }
    }

    public static void discoverServices(final @NonNull UUPeripheral peripheral, final long timeout, final @NonNull UUPeripheralDelegate delegate)
    {
        UUBluetoothGatt gatt = gattForPeripheral(peripheral);
        if (gatt != null)
        {
            gatt.discoverServices(timeout, delegate);
        }
    }

    public static void toggleNotifyState(
        final @NonNull UUPeripheral peripheral,
        final @NonNull BluetoothGattCharacteristic characteristic,
        final boolean notifyState,
        final long timeout,
        final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = gattForPeripheral(peripheral);
        if (gatt != null)
        {
            gatt.toggleNotifyState(characteristic, notifyState, timeout, delegate);
        }
    }

    public static void writeCharacteristic(
            final @NonNull UUPeripheral peripheral,
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = gattForPeripheral(peripheral);
        if (gatt != null)
        {
            gatt.writeCharacteristic(characteristic, data, timeout, delegate);
        }
    }

    public static void writeCharacteristicWithoutResponse(
            final @NonNull UUPeripheral peripheral,
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = gattForPeripheral(peripheral);
        if (gatt != null)
        {
            gatt.writeCharacteristicWithoutResponse(characteristic, data, timeout, delegate);
        }
    }
}
