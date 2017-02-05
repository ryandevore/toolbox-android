package uu.toolbox.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import uu.toolbox.core.UUString;
import uu.toolbox.core.UUThread;
import uu.toolbox.core.UUTimer;
import uu.toolbox.logging.UULog;

/**
 * A helpful set of wrapper methods around BluetoothGatt
 */
class UUBluetoothGatt
{
    // Internal Constants
    private static final String CONNECT_WATCHDOG_BUCKET = "UUCoreBluetoothConnectWatchdogBucket";
    private static final String SERVICE_DISCOVERY_WATCHDOG_BUCKET = "UUCoreBluetoothServiceDiscoveryWatchdogBucket";
    private static final String CHARACTERISTIC_NOTIFY_STATE_WATCHDOG_BUCKET = "UUCoreBluetoothCharacteristicNotifyStateWatchdogBucket";
    private static final String READ_CHARACTERISTIC_WATCHDOG_BUCKET = "UUCoreBluetoothReadCharacteristicValueWatchdogBucket";
    private static final String WRITE_CHARACTERISTIC_WATCHDOG_BUCKET = "UUCoreBluetoothWriteCharacteristicValueWatchdogBucket";
    private static final String READ_RSSI_WATCHDOG_BUCKET = "UUCoreBluetoothReadRssiWatchdogBucket";
    private static final String POLL_RSSI_BUCKET = "UUCoreBluetoothPollRssiBucket";

    private static final boolean DEBUG_LOGGING_ENABLED = true;

    private UUPeripheral peripheral;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCallback bluetoothGattCallback;

    private UUConnectionDelegate connectionDelegate;
    private UUPeripheralDelegate serviceDiscoveryDelegate;
    private UUDescriptorDelegate writeDescriptorDelegate;
    private UUDescriptorDelegate readDescriptorDelegate;
    private UUCharacteristicDelegate setNotifyDelegate;
    private UUCharacteristicDelegate writeCharacteristicDelegate;

    private final HashMap<String, UUCharacteristicDelegate> readCharacteristicDataDelegates = new HashMap<>();
    private final HashMap<String, UUCharacteristicDelegate> characteristicChangedDelegates = new HashMap<>();

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
        final String timerId = connectWatchdogTimerId();

        connectionDelegate = new UUConnectionDelegate()
        {
            @Override
            public void onConnected(@NonNull UUPeripheral peripheral)
            {
                debugLog("connect", "Connected to: " + peripheral);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onConnected(peripheral);
            }

            @Override
            public void onDisconnected(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                debugLog("connect", "Disconnected from: " + peripheral + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onDisconnected(peripheral, error);
            }
        };

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("connect", "Connect timeout: " + peripheral);

                connectionDelegate = null;

                disconnect();

                UUBluetoothError err = UUBluetoothError.timeoutError();
                UUTimer.cancelActiveTimer(timerId);
                delegate.onDisconnected(peripheral, err);
            }
        });

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
        final String timerId = serviceDiscoveryWatchdogTimerId();

        serviceDiscoveryDelegate = new UUPeripheralDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                debugLog("discoverServices", "Service Discovery complete: " + peripheral + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(peripheral, error);
            }
        };

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("discoverServices", "Service Discovery timeout: " + peripheral);

                serviceDiscoveryDelegate = null;

                disconnect();

                UUBluetoothError err = UUBluetoothError.timeoutError();
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(peripheral, err);
            }
        });

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

    void readCharacteristic(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        final String timerId = readDataWatchdogTimerId(characteristic);

        UUCharacteristicDelegate readCharacteristicDelegate = new UUCharacteristicDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattCharacteristic characteristic, @Nullable UUBluetoothError error)
            {
                debugLog("readCharacteristic", "Read characteristic complete: " + peripheral + ", error: " + error + ", data: " + UUString.byteToHex(characteristic.getValue()));
                removeReadCharacteristicDelegate(characteristic);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(peripheral, characteristic, error);
            }
        };

        registerReadCharacteristicDelegate(characteristic, readCharacteristicDelegate);

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("readCharacteristic", "bluetoothGatt is null!");
                    notifyCharacteristicRead(characteristic, UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("readCharacteristic", "characteristic: " + characteristic.getUuid());

                boolean success = bluetoothGatt.readCharacteristic(characteristic);

                debugLog("readCharacteristic", "readCharacteristic returned " + success);

                if (!success)
                {
                    notifyCharacteristicRead(characteristic, UUBluetoothError.operationFailedError("readCharacteristic"));
                }
            }
        });
    }

    void readDescriptor(
            final @NonNull BluetoothGattDescriptor descriptor,
            final long timeout,
            final @NonNull UUDescriptorDelegate delegate)
    {
        readDescriptorDelegate = delegate;

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("readDescriptor", "bluetoothGatt is null!");
                    notifyDescriptorRead(descriptor, UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("readDescriptor", "descriptor: " + descriptor.getUuid());

                boolean success = bluetoothGatt.readDescriptor(descriptor);

                debugLog("readCharacteristic", "readDescriptor returned " + success);

                if (!success)
                {
                    notifyDescriptorRead(descriptor, UUBluetoothError.operationFailedError("readDescriptor"));
                }
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

    void setNotifyState(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final boolean enabled,
            final long timeout,
            final @Nullable UUCharacteristicDelegate notifyDelegate,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        setNotifyDelegate = delegate;

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

                if (enabled && notifyDelegate != null)
                {
                    registerCharacteristicChangedDelegate(characteristic, notifyDelegate);
                }
                else
                {
                    removeCharacteristicChangedDelegate(characteristic);
                }

                debugLog("toggleNotifyState", "Setting characteristic notify for " + characteristic.getUuid().toString());
                boolean success = bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
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

                byte[] data = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
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

    BluetoothGatt getBluetoothGatt()
    {
        return bluetoothGatt;
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
        UUCharacteristicDelegate delegate = setNotifyDelegate;
        setNotifyDelegate = null;
        notifyCharacteristicDelegate(delegate, characteristic, error);
    }

    private void notifyCharacteristicWritten(final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error)
    {
        UUCharacteristicDelegate delegate = writeCharacteristicDelegate;
        writeCharacteristicDelegate = null;
        notifyCharacteristicDelegate(delegate, characteristic, error);
    }

    private void notifyCharacteristicRead(final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error)
    {
        UUCharacteristicDelegate delegate = getReadCharacteristicDelegate(characteristic);
        removeReadCharacteristicDelegate(characteristic);
        notifyCharacteristicDelegate(delegate, characteristic, error);
    }

    private void notifyCharacteristicChanged(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        UUCharacteristicDelegate delegate = getCharacteristicChangedDelegate(characteristic);
        notifyCharacteristicDelegate(delegate, characteristic, null);
    }

    private void notifyDescriptorRead(final @NonNull BluetoothGattDescriptor descriptor, final @Nullable UUBluetoothError error)
    {
        UUDescriptorDelegate delegate = readDescriptorDelegate;
        readDescriptorDelegate = null;
        notifyDescriptorDelegate(delegate, descriptor, error);
    }

    private void registerCharacteristicChangedDelegate(final @NonNull BluetoothGattCharacteristic characteristic, final @NonNull UUCharacteristicDelegate delegate)
    {
        characteristicChangedDelegates.put(safeUuidString(characteristic), delegate);
    }

    private void removeCharacteristicChangedDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        characteristicChangedDelegates.remove(safeUuidString(characteristic));
    }

    private @Nullable UUCharacteristicDelegate getCharacteristicChangedDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return characteristicChangedDelegates.get(safeUuidString(characteristic));
    }

    private void registerReadCharacteristicDelegate(final @NonNull BluetoothGattCharacteristic characteristic, final @NonNull UUCharacteristicDelegate delegate)
    {
        readCharacteristicDataDelegates.put(safeUuidString(characteristic), delegate);
    }

    private void removeReadCharacteristicDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        readCharacteristicDataDelegates.remove(safeUuidString(characteristic));
    }

    private @Nullable UUCharacteristicDelegate getReadCharacteristicDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return readCharacteristicDataDelegates.get(safeUuidString(characteristic));
    }

    private @NonNull String safeUuidString(final @Nullable BluetoothGattCharacteristic characteristic)
    {
        String result = null;

        if (characteristic != null)
        {
            result = safeUuidString(characteristic.getUuid());
        }

        if (result == null)
        {
            result = "";
        }

        result = result.toLowerCase();
        return result;
    }

    private @NonNull String safeUuidString(final @Nullable UUID uuid)
    {
        String result = null;

        if (uuid != null)
        {
            result = uuid.toString();
        }

        if (result == null)
        {
            result = "";
        }

        result = result.toLowerCase();
        return result;
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

    private @NonNull String formatPeripheralTimerId(final @NonNull String bucket)
    {
        return String.format(Locale.US, "%s__%s", peripheral.getAddress(), bucket);
    }

    private @NonNull String formatCharacteristicTimerId(final @NonNull BluetoothGattCharacteristic characteristic, final @NonNull String bucket)
    {
        return String.format(Locale.US, "%s__%s__%s", peripheral.getAddress(), safeUuidString(characteristic), bucket);
    }

    private @NonNull String connectWatchdogTimerId()
    {
        return formatPeripheralTimerId(CONNECT_WATCHDOG_BUCKET);
    }

    private @NonNull String serviceDiscoveryWatchdogTimerId()
    {
        return formatPeripheralTimerId(SERVICE_DISCOVERY_WATCHDOG_BUCKET);
    }

    private @NonNull String setNotifyStateWatchdogTimerId(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return formatCharacteristicTimerId(characteristic, CHARACTERISTIC_NOTIFY_STATE_WATCHDOG_BUCKET);
    }

    private @NonNull String readDataWatchdogTimerId(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return formatCharacteristicTimerId(characteristic, READ_CHARACTERISTIC_WATCHDOG_BUCKET);
    }

    private @NonNull String writeDataWatchdogTimerId(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return formatCharacteristicTimerId(characteristic, WRITE_CHARACTERISTIC_WATCHDOG_BUCKET);
    }

    private @NonNull String readRssiWatchdogTimerId(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return formatCharacteristicTimerId(characteristic, READ_RSSI_WATCHDOG_BUCKET);
    }

    private @NonNull String pollRssiTimerId(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return formatCharacteristicTimerId(characteristic, POLL_RSSI_BUCKET);
    }

    private void cancelAllTimers()
    {
        if (peripheral != null)
        {
            Collection<UUTimer> list = UUTimer.listActiveTimers();

            String prefix = peripheral.getAddress();
            if (prefix != null)
            {
                for (UUTimer t : list)
                {
                    if (t.getTimerId().startsWith(prefix))
                    {
                        t.cancel();
                    }
                }
            }
        }
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

            // TODO: Handle errors
            notifyCharacteristicRead(characteristic, null);
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

            notifyCharacteristicChanged(characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            debugLog("onDescriptorRead",
                    "descriptor: " + descriptor +
                            ", status: " + statusLog(status) +
                            ", char.data: " + UUString.byteToHex(descriptor.getValue()));

            // TODO: Handle errors
            notifyDescriptorRead(descriptor, null);
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
