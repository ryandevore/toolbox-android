package uu.toolbox.bluetooth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Locale;

import uu.toolbox.core.UUString;

/**
 * Container class for UUBluetooth errors
 */
@SuppressWarnings("unused")
public class UUBluetoothError
{
    /**
     * Lookup key for errorDetails for the failing underlying bluetooth method name.
     */
    public static final String DETAIL_KEY_METHOD_NAME = "methodName";

    private Exception caughtException = null;
    private UUBluetoothErrorCode errorCode;
    private final HashMap<String, String> errorDetails = new HashMap<>();

    /**
     * Creates a UUBluetoothError from an error code
     *
     * @param errorCode the error code
     */
    public UUBluetoothError(final @NonNull UUBluetoothErrorCode errorCode)
    {
        this.errorCode = errorCode;
    }

    /**
     * To string override
     *
     * @return string
     */
    @Override
    public String toString()
    {
        try
        {
            return String.format(Locale.US, "code: %s, details: %s, ex: %s",
                    UUString.safeToString(errorCode),
                    UUString.safeToString(errorDetails),
                    UUString.safeToString(caughtException));
        }
        catch (Exception ex)
        {
            return super.toString();
        }
    }

    /**
     * Returns the captured exception
     *
     * @return an Exception object
     */
    public @Nullable Exception getCaughtException()
    {
        return caughtException;
    }

    /**
     * Returns the error code
     *
     * @return an error code
     */
    public @NonNull UUBluetoothErrorCode getErrorCode()
    {
        return errorCode;
    }

    /**
     * Gets error details
     *
     * @return error details dictionary
     */
    public @NonNull HashMap<String, String> getErrorDetails()
    {
        return errorDetails;
    }

    /**
     * Wrapper method to return a not connected error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError notConnectedError()
    {
        return new UUBluetoothError(UUBluetoothErrorCode.NotConnected);
    }

    /**
     * Wrapper method to return a timeout error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError timeoutError()
    {
        return new UUBluetoothError(UUBluetoothErrorCode.Timeout);
    }

    /**
     * Wrapper method to return an underlying Bluetooth method failure.  This is returned when
     * a method returns false or null or othe error condition.
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError operationFailedError(final String method)
    {
        UUBluetoothError err = new UUBluetoothError(UUBluetoothErrorCode.OperationFailed);
        err.errorDetails.put(DETAIL_KEY_METHOD_NAME, method);
        return err;
    }
}
