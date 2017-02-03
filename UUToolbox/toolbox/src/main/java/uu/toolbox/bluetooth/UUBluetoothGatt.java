package uu.toolbox.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

import uu.toolbox.core.UUString;
import uu.toolbox.core.UUThread;
import uu.toolbox.logging.UULog;

/**
 * A helpful set of wrapper methods around BluetoothGatt
 */
class UUBluetoothGatt
{
    private static final boolean DEBUG_LOGGING_ENABLED = true;

    private UUPeripheral peripheral;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCallback bluetoothGattCallback;

    private UUConnectionDelegate connectionDelegate;
    private UUPeripheralDelegate serviceDiscoveryDelegate;
    private UUDescriptorDelegate writeDescriptorDelegate;
    private UUCharacteristicDelegate toggleNotifyDelegate;
    private UUCharacteristicDelegate writeCharacteristicDelegate;
    private UUCharacteristicDelegate writeWithoutResponseCharacteristicDelegate;

    UUBluetoothGatt(final @NonNull UUPeripheral peripheral)
    {
        this.peripheral = peripheral;
        bluetoothGattCallback = new UUBluetoothGattCallback();
    }

    boolean isGattConnected()
    {
        return (bluetoothGatt != null);
    }

    void connect(
            final @NonNull Context context,
            final boolean connectGattAutoFlag,
            final long timeout,
            final @NonNull UUConnectionDelegate delegate)
    {
        connectionDelegate = delegate;

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                debugLog("connect", "Connecting to: " + peripheral);
                bluetoothGatt = peripheral.getBluetoothDevice().connectGatt(context, connectGattAutoFlag, bluetoothGattCallback);
            }
        });
    }

    void disconnect()
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                debugLog("disconnect", "Disconnecting from: " + peripheral);
                disconnectGatt();
            }
        });
    }



    void discoverServices(
            final long timeout,
            final @NonNull UUPeripheralDelegate delegate)
    {
        serviceDiscoveryDelegate = delegate;

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("discoverServices", "bluetoothGatt is null!");
                    notifyServicesDiscovered(UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("discoverServices", "Discovering services for: " + peripheral);
                boolean ok = bluetoothGatt.discoverServices();
                debugLog("discoverServices", "returnCode:" + ok);

                if (!ok)
                {
                    notifyServicesDiscovered(UUBluetoothError.operationFailedError("discoverServices"));
                }
                // else
                //
                // wait for delegate or timeout
            }
        });
    }

    void writeDescriptor(
            final @NonNull BluetoothGattDescriptor descriptor,
            final byte[] data,
            final long timeout,
            final @NonNull UUDescriptorDelegate delegate)
    {
        writeDescriptorDelegate = delegate;
        descriptor.setValue(data);

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("writeDescriptor", "bluetoothGatt is null!");
                    notifyDescriptorWritten(descriptor, UUBluetoothError.notConnectedError());
                    return;
                }

                boolean success = bluetoothGatt.writeDescriptor(descriptor);
                UULog.debug(getClass(), "writeDescriptor", "writeDescriptor returned " + success);

                if (!success)
                {
                    notifyDescriptorWritten(descriptor, UUBluetoothError.operationFailedError("writeDescriptor"));
                }
                // else
                //
                // wait for delegate or timeout

            }
        });
    }

    void toggleNotifyState(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final boolean notifyState,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        toggleNotifyDelegate = delegate;

        final long start = System.currentTimeMillis();

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("toggleNotifyState", "bluetoothGatt is null!");
                    notifyCharacteristicNotifyStateChanged(characteristic, UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("toggleNotifyState", "Setting characteristic notify for " + characteristic.getUuid().toString());
                boolean success = bluetoothGatt.setCharacteristicNotification(characteristic, notifyState);
                debugLog("toggleNotifyState", "setCharacteristicNotification returned " + success);

                if (!success)
                {
                    notifyCharacteristicNotifyStateChanged(characteristic, UUBluetoothError.operationFailedError("setCharacteristicNotification"));
                    return;
                }

                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUBluetoothConstants.Descriptors.CLIENT_CHARACTERISTIC_CONFIGURATION_UUID);
                if (descriptor == null)
                {
                    notifyCharacteristicNotifyStateChanged(characteristic, UUBluetoothError.operationFailedError("getDescriptor"));
                    return;
                }

                byte[] data = notifyState ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                long timeoutLeft = timeout - (System.currentTimeMillis() - start);

                writeDescriptor(descriptor, data, timeoutLeft, new UUDescriptorDelegate()
                {
                    @Override
                    public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattDescriptor descriptor, @Nullable UUBluetoothError error)
                    {
                        notifyCharacteristicNotifyStateChanged(characteristic, error);
                    }
                });
            }
        });
    }

    void writeCharacteristic(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        writeCharacteristicDelegate = delegate;

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("writeCharacteristic", "bluetoothGatt is null!");
                    notifyCharacteristicWritten(characteristic, UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("writeCharacteristic", "characteristic: " + characteristic.getUuid() + ", data: " + UUString.byteToHex(data));

                characteristic.setValue(data);
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                boolean success = bluetoothGatt.writeCharacteristic(characteristic);

                debugLog("writeCharacteristic", "writeCharacteristic returned " + success);

                if (!success)
                {
                    notifyCharacteristicWritten(characteristic, UUBluetoothError.operationFailedError("writeCharacteristic"));
                }
            }
        });
    }

    void writeCharacteristicWithoutResponse(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        writeCharacteristicDelegate = delegate;

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("writeCharacteristicWithoutResponse", "bluetoothGatt is null!");
                    notifyCharacteristicWritten(characteristic, UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("writeCharacteristicWithoutResponse", "characteristic: " + characteristic.getUuid() + ", data: " + UUString.byteToHex(data));

                characteristic.setValue(data);
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                boolean success = bluetoothGatt.writeCharacteristic(characteristic);

                debugLog("writeCharacteristicWithoutResponse", "writeCharacteristic returned " + success);

                if (!success)
                {
                    notifyCharacteristicWritten(characteristic, UUBluetoothError.operationFailedError("writeCharacteristic"));
                }
                else
                {
                    notifyCharacteristicWritten(characteristic, null);
                }
            }
        });
    }

    private void notifyConnectDelegate(final @Nullable UUConnectionDelegate delegate)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onConnected(peripheral);
            }
        }
        catch (Exception ex)
        {
            logException("notifyConnectDelegate", ex);
        }
    }

    private void notifyDisconnectDelegate(final @Nullable UUConnectionDelegate delegate, final @Nullable UUBluetoothError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onDisconnected(peripheral, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyDisconnectDelegate", ex);
        }
    }

    private void notifyPeripheralDelegate(final @Nullable UUPeripheralDelegate delegate, final @Nullable UUBluetoothError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onComplete(peripheral, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyPeripheralDelegate", ex);
        }
    }

    private void notifyCharacteristicDelegate(final @Nullable UUCharacteristicDelegate delegate, final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onComplete(peripheral, characteristic, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyCharacteristicDelegate", ex);
        }
    }

    private void notifyDescriptorDelegate(final @Nullable UUDescriptorDelegate delegate, final @NonNull BluetoothGattDescriptor descriptor, final @Nullable UUBluetoothError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onComplete(peripheral, descriptor, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyDescriptorDelegate", ex);
        }
    }

    private void notifyConnected()
    {
        try
        {
            peripheral.setBluetoothGatt(bluetoothGatt);

            UUConnectionDelegate delegate = connectionDelegate;
            notifyConnectDelegate(delegate);
        }
        catch (Exception ex)
        {
            logException("notifyConnected", ex);
        }
    }

    private void notifyDisconnected(final @Nullable UUBluetoothError error)
    {
        closeGatt();
        bluetoothGatt = null;
        peripheral.setBluetoothGatt(null);

        UUConnectionDelegate delegate = connectionDelegate;
        connectionDelegate = null;
        notifyDisconnectDelegate(delegate, error);
    }

    private void notifyServicesDiscovered(final @Nullable UUBluetoothError error)
    {
        UUPeripheralDelegate delegate = serviceDiscoveryDelegate;
        serviceDiscoveryDelegate = null;
        notifyPeripheralDelegate(delegate, error);
    }

    private void notifyDescriptorWritten(final @NonNull BluetoothGattDescriptor descriptor, final @Nullable UUBluetoothError error)
    {
        UUDescriptorDelegate delegate = writeDescriptorDelegate;
        writeDescriptorDelegate = null;
        notifyDescriptorDelegate(delegate, descriptor, error);
    }

    private void notifyCharacteristicNotifyStateChanged(final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error)
    {
        UUCharacteristicDelegate delegate = toggleNotifyDelegate;
        toggleNotifyDelegate = null;
        notifyCharacteristicDelegate(delegate, characteristic, error);
    }

    private void notifyCharacteristicWritten(final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error)
    {
        UUCharacteristicDelegate delegate = writeCharacteristicDelegate;
        writeCharacteristicDelegate = null;
        notifyCharacteristicDelegate(delegate, characteristic, error);
    }

    private void disconnectGatt()
    {
        try
        {
            if (bluetoothGatt != null)
            {
                bluetoothGatt.disconnect();
            }
        }
        catch (Exception ex)
        {
            logException("disconnectGatt", ex);
        }
    }

    private void closeGatt()
    {
        try
        {
            if (bluetoothGatt != null)
            {
                bluetoothGatt.close();
            }
        }
        catch (Exception ex)
        {
            logException("closeGatt", ex);
        }
    }

    private void reconnectGatt()
    {
        try
        {
            boolean success = bluetoothGatt.connect();
            debugLog("reconnectGatt", "connect() returned " + success);
        }
        catch (Exception ex)
        {
            logException("reconnectGatt", ex);
        }
    }

    private void debugLog(final String method, final String message)
    {
        if (DEBUG_LOGGING_ENABLED && UULog.LOGGING_ENABLED)
        {
            UULog.debug(getClass(), method, message);
        }
    }

    private void logException(final String method, final Exception ex)
    {
        if (DEBUG_LOGGING_ENABLED && UULog.LOGGING_ENABLED)
        {
            UULog.error(getClass(), method, ex);
        }
    }

    private @NonNull String statusLog(final int status)
    {
        return String.format(Locale.US, "%s (%d)", UUBluetooth.gattStatusToString(status), status);
    }

    private class UUBluetoothGattCallback extends BluetoothGattCallback
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            debugLog("onConnectionStateChanged",
                    String.format(Locale.US, "status: %s, newState: %s (%d)",
                            statusLog(status), UUBluetooth.connectionStateToString(newState), newState));

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED)
            {
                notifyConnected();
            }
            else if (status == UUBluetoothConstants.GATT_ERROR)
            {
                // Sometimes when attempting a connection, the operation fails with status 133.  Through
                // trial and error, calling BluetoothGatt.connect() after this happens will make the
                // connection happen.
                reconnectGatt();
            }
            else if (newState == BluetoothGatt.STATE_DISCONNECTED)
            {
                // TODO: Handle disconnect errors
                notifyDisconnected(null);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            debugLog("onServicesDiscovered",
                    String.format(Locale.US, "status: %s", statusLog(status)));

            // TODO: Handle service discovery errors
            notifyServicesDiscovered(null);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            debugLog("onCharacteristicRead",
                    "characteristic: " + characteristic +
                            ", status: " + statusLog(status) +
                            ", char.data: " + UUString.byteToHex(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            debugLog("onCharacteristicWrite",
                    "characteristic: " + characteristic +
                            ", status: " + statusLog(status) +
                            ", char.data: " + UUString.byteToHex(characteristic.getValue()));

            // TODO: handle errors
            notifyCharacteristicWritten(characteristic, null);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            debugLog("onCharacteristicChanged",
                    "characteristic: " + characteristic +
                            ", char.data: " + UUString.byteToHex(characteristic.getValue()));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            debugLog("onDescriptorRead",
                    "descriptor: " + descriptor +
                            ", status: " + statusLog(status) +
                            ", char.data: " + UUString.byteToHex(descriptor.getValue()));
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            debugLog("onDescriptorWrite",
                    "descriptor: " + descriptor +
                            ", status: " + statusLog(status) +
                            ", char.data: " + UUString.byteToHex(descriptor.getValue()));

            // TODO: Handle errors
            notifyDescriptorWritten(descriptor, null);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
        {
            debugLog("onReliableWriteCompleted", ", status: " + status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
        {
            debugLog("onReadRemoteRssi", "rssi: " + rssi + ", status: " + status);
        }
    };
}
