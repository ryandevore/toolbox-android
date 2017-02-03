package uu.toolbox.bluetooth;

/**
 * UUBluetooth error codes.
 */
@SuppressWarnings("unused")
public enum UUBluetoothErrorCode
{
    /**
     *  An operation attempt was manually timed out by UUCoreBluetooth
     */
    UUCoreBluetoothErrorCodeTimeout(1),

    /**
     * A method call was not attempted because the BluetoothDevice was not connected.
     */
    UUCoreBluetoothErrorCodeNotConnected(2),

    /**
     * A Bluetooth operation failed for some reason. Check caught exception and user info for
     * more information.  This can be returned from any Bluetooth method that throws exceptions
     */
    UUCoreBluetoothErrorCodeOperationFailed(3),

    /**
     * A connection attempt failed.
     */
    UUCoreBluetoothErrorCodeConnectionFailed(4),

    /**
     * A peripheral device disconnected
     */
    UUCoreBluetoothErrorCodeDisconnected(5),

    // An operation was passed an invalid argument.  Inspect user info for
    // specific details
    UUCoreBluetoothErrorCodeInvalidParam(6);

    /**
     * The raw enum value
     */
    private int rawValue;

    /**
     * Returns the integer raw value
     *
     * @return an integer representation of the enum
     */
    public int getRawValue()
    {
        return rawValue;
    }

    UUBluetoothErrorCode(final int val)
    {
        rawValue = val;
    }
}
