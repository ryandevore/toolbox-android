package uu.toolbox.bluetooth.classic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import uu.toolbox.core.UUString;

public class UUBluetoothSession
{
    private static final String LOG_TAG = "UUBluetoothSession";

    private static final UUID SERIAL_PORT_PROFILE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    enum UUBluetoothSessionState
    {
        Idle,
        WaitingForPairing,
        WaitingForConnect,
        WaitingForServiceDiscovery,
        Connected,
        WaitingForDisconnect,
        WaitingForUnpairing,
    }

    enum UUBluetoothSessionErrorCode
    {
        PairingFailed,
        PairingFailedFromException,
        ConnectFailedInvalidState,
        ConnectFailedFromException,
        ServiceDiscoveryFailed,
        ServiceDiscoveryFailedFromException,
        DisconnectFailedFromException,
        UnpairingFailed,
        UnpairingFailedFromException,
        SocketNotConnected,
        WriteFailedFromException,
        ReadFailedInvalidState,
        ReadFailedFromException,
    }

    private Context context;

    private BluetoothDevice device;
    private String deviceAddress;
    private BroadcastReceiver broadcastReceiver;
    private UUBluetoothSessionState currentState;
    private BluetoothSocket bluetoothSocket;
    private ErrorRunnable connectRunnable;
    private DisconnectRunnable disconnectRunnable;

    public UUBluetoothSession(Context context, BluetoothDevice device)
    {
        this.context = context;
        this.device = device;
        if (device != null)
        {
            this.deviceAddress = device.getAddress().toLowerCase();
        }

        currentState = UUBluetoothSessionState.Idle;
    }

    private void registerReceivers()
    {
        initBroadcastReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
    }

    private void unregisterReceivers()
    {
        try
        {
            if (context != null && broadcastReceiver != null)
            {
                debugLog("unregisterReceivers", "Unregistering broadcast receiver");
                context.unregisterReceiver(broadcastReceiver);
            }
        }
        catch (Exception ex)
        {
            logException("unregisterReceivers", ex);
        }
        finally
        {
            broadcastReceiver = null;
        }
    }

    private void registerReceiver(BroadcastReceiver receiver, IntentFilter intentFilter)
    {
        context.registerReceiver(receiver, intentFilter);
    }

    private void initBroadcastReceiver()
    {
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();

                if (shouldHandleIntent(intent))
                {
                    debugLog("initBroadcastReceiver.onReceive", "Handled Bluetooth Action: " + action + ", device: " + getIntentDeviceAddress(intent));

                    switch (action)
                    {
                        case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                            handleBondStateChanged(intent);
                            break;

                        case BluetoothDevice.ACTION_UUID:
                            handleActionUuid(intent);
                            break;
                    }
                }
            }
        };
    }

    private void handleBondStateChanged(final Intent intent)
    {
        int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
        int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        debugLog("handleBondStateChanged", "Bond State for device " + device.getAddress() + " changed from " + bondStateToString(previousBondState) + " to " + bondStateToString(bondState));

        if ((previousBondState == BluetoothDevice.BOND_BONDING) &&
                (bondState == BluetoothDevice.BOND_BONDED || bondState == BluetoothDevice.BOND_NONE) &&
                currentState == UUBluetoothSessionState.WaitingForPairing)
        {
            connectRunnable.safeNotify("Pairing finished");
        }
        else if ((previousBondState == BluetoothDevice.BOND_BONDING || previousBondState == BluetoothDevice.BOND_BONDED) &&
                (bondState == BluetoothDevice.BOND_NONE) &&
                currentState == UUBluetoothSessionState.WaitingForUnpairing)
        {
            disconnectRunnable.safeNotify("Unpairing finished");
        }
    }

    private void handleActionUuid(final Intent intent)
    {
        Parcelable extraUuids[] = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
        if (extraUuids != null)
        {
            boolean foundSpp = false;

            for (Parcelable p : extraUuids)
            {
                ParcelUuid parcelUuid = (ParcelUuid)p;
                UUID uuid = parcelUuid.getUuid();
                debugLog("handleActionUuid", "uuid: " + uuid.toString());

                if (uuid.compareTo(SERIAL_PORT_PROFILE_UUID) == 0)
                {
                    foundSpp = true;
                }
            }

            if (foundSpp && currentState == UUBluetoothSessionState.WaitingForServiceDiscovery)
            {
                debugLog("handleActionUuid", "Discovered Serial Port profile, kicking wait thread");
                connectRunnable.safeNotify("SPP found uuid");
            }
        }
    }

    private boolean shouldHandleIntent(final Intent intent)
    {
        boolean shouldHandleEvent = false;

        try
        {
            if (intent != null)
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null)
                {
                    String address = device.getAddress();
                    if (address != null && deviceAddress != null)
                    {
                        shouldHandleEvent = address.equalsIgnoreCase(deviceAddress);
                    }
                }
            }

        }
        catch (Exception ex)
        {
            logException("shouldHandleIntent", ex);
            shouldHandleEvent = false;
        }

        return shouldHandleEvent;
    }

    private String getIntentDeviceAddress(final Intent intent)
    {
        String address = "";

        try
        {
            if (intent != null)
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null)
                {
                    address = device.getAddress();
                }
            }

        }
        catch (Exception ex)
        {
            logException("getIntentDeviceAddress", ex);
            address = "";
        }

        return address;
    }

    private Thread startThread(final Runnable runnable)
    {
        Thread t = null;

        try
        {
            t = new Thread(runnable);
            t.start();
        }
        catch (Exception ex)
        {
            logException("startThread", ex);
        }

        return t;
    }

    public void startSession(final ErrorCallback callback)
    {
        registerReceivers();
        connectRunnable = connectRunnable();
        connectRunnable.setCallback(callback);
        startThread(connectRunnable);
    }

    public void endSession(final boolean unpair, final ErrorCallback callback)
    {
        debugLog("endSession", "unpair: " + unpair);
        disconnectRunnable = disconnectRunnable();
        disconnectRunnable.setUnpair(unpair);
        disconnectRunnable.setCallback(callback);
        startThread(disconnectRunnable);
    }

    public void writeData(final byte[] data, final ErrorCallback callback)
    {
        SendRunnable sendRunnable = sendRunnable();
        sendRunnable.setTxData(data);
        sendRunnable.setCallback(callback);
        startThread(sendRunnable);
    }

    public void readData(final long count, final long timeout, final DataCallback callback)
    {
        ReceiveRunnable receiveRunnable = receiveRunnable();
        receiveRunnable.setNumberToReceive(count);
        receiveRunnable.setReadTimeout(timeout);
        receiveRunnable.setCallback(callback);
        startThread(receiveRunnable);
    }

    private UUBluetoothSessionError pair(final SessionRunnable runnable)
    {
        UUBluetoothSessionError error = null;

        int bondState = -1;

        try
        {
            if (device != null)
            {
                bondState = device.getBondState();
                debugLog("pair", "BondState is " + bondStateToString(bondState));
                if (bondState != BluetoothDevice.BOND_BONDED)
                {
                    changeState(UUBluetoothSessionState.WaitingForPairing);

                    debugLog("pair", "Attempting to pair with device " + deviceAddress);
                    boolean returnValue = device.createBond();
                    debugLog("pair", "createBond returned: " + returnValue);

                    runnable.safeWait("pair");

                    bondState = device.getBondState();
                    debugLog("pair", "BondState after pairing is " + bondStateToString(bondState));
                }
            }
        }
        catch (Exception ex)
        {
            logException("pair", ex);
            error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.PairingFailedFromException, ex);
        }
        finally
        {
            changeState(UUBluetoothSessionState.Idle);

            if (error == null)
            {
                if (bondState != BluetoothDevice.BOND_BONDED)
                {
                    error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.PairingFailed);
                }
            }
        }

        return error;
    }

    private UUBluetoothSessionError serviceDiscovery(final SessionRunnable runnable)
    {
        UUBluetoothSessionError error = null;

        try
        {
            if (device != null)
            {
                changeState(UUBluetoothSessionState.WaitingForServiceDiscovery);

                debugLog("serviceDiscovery", "Calling fetchUuidsWithSdp");
                boolean result = device.fetchUuidsWithSdp();
                debugLog("serviceDiscovery", "device.fetchUuidsWithSdp returned " + result);

                if (!result)
                {
                    error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.ServiceDiscoveryFailed);
                }
                else
                {
                    runnable.safeWait("fetchUuidsWithSdp");
                }
            }
        }
        catch (Exception ex)
        {
            logException("serviceDiscovery", ex);
            error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.ServiceDiscoveryFailedFromException, ex);
        }
        finally
        {
            changeState(UUBluetoothSessionState.Idle);
        }

        return error;
    }

    private UUBluetoothSessionError connect(final boolean tryInsecure)
    {
        UUBluetoothSessionError error = null;

        try
        {
            if (device != null)
            {
                changeState(UUBluetoothSessionState.WaitingForConnect);

                if (tryInsecure)
                {
                    debugLog("connect", "Creating socket with createInsecureRfcommSocketToServiceRecord");
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(SERIAL_PORT_PROFILE_UUID);
                    debugLog("connect", "createInsecureRfcommSocketToServiceRecord returned " + ((bluetoothSocket != null) ? bluetoothSocket.toString() : "null"));
                }
                else
                {
                    debugLog("connect", "Creating socket with createRfcommSocketToServiceRecord");
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(SERIAL_PORT_PROFILE_UUID);
                    debugLog("connect", "createRfcommSocketToServiceRecord returned " + ((bluetoothSocket != null) ? bluetoothSocket.toString() : "null"));
                }

                debugLog("connect", "Opening Bluetooth Socket");
                bluetoothSocket.connect();
                debugLog("connect", "Bluetooth Socket Open");
            }
        }
        catch (Exception ex)
        {
            logException("connect", ex);
            error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.ConnectFailedFromException, ex);
        }
        finally
        {
            changeState(UUBluetoothSessionState.Idle);
        }

        return error;
    }

    private UUBluetoothSessionError disconnect()
    {
        UUBluetoothSessionError error = null;

        try
        {
            changeState(UUBluetoothSessionState.WaitingForDisconnect);
            closeSocket();
        }
        catch (Exception ex)
        {
            logException("disconnect", ex);
            error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.DisconnectFailedFromException, ex);
        }
        finally
        {
            changeState(UUBluetoothSessionState.Idle);
        }

        return error;
    }

    private UUBluetoothSessionError unpair(final SessionRunnable runnable)
    {
        UUBluetoothSessionError error = null;

        try
        {
            if (device != null)
            {
                int bondState = device.getBondState();
                debugLog("unpair", "BondState is " + bondStateToString(bondState));
                if (bondState == BluetoothDevice.BOND_BONDED)
                {
                    changeState(UUBluetoothSessionState.WaitingForUnpairing);

                    debugLog("unpair", "Removing bond with device: " + deviceAddress);
                    Method m =  device.getClass().getMethod("removeBond", (Class[])null);
                    Boolean returnValue = (Boolean)m.invoke(device, (Object[])null);
                    debugLog("unpair", "removeBond returned: " + returnValue.toString());

                    runnable.safeWait("unpair");

                    bondState = device.getBondState();
                    debugLog("unpair", "BondState after unpairing is " + bondStateToString(bondState));
                    if (bondState != BluetoothDevice.BOND_NONE)
                    {
                        error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.UnpairingFailed);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logException("unpair", ex);
            error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.UnpairingFailedFromException, ex);
        }
        finally
        {
            changeState(UUBluetoothSessionState.Idle);
        }

        return error;
    }

    private UUBluetoothSessionError writeData(final byte[] data)
    {
        UUBluetoothSessionError error = null;

        try
        {
            debugLog("writeData", "TX: " + UUString.byteToHex(data));
            if (bluetoothSocket != null)
            {
                OutputStream os = bluetoothSocket.getOutputStream();
                os.write(data, 0, data.length);
                os.flush();
            }
            else
            {
                error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.SocketNotConnected);
            }
        }
        catch (Exception ex)
        {
            error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.WriteFailedFromException, ex);
            changeState(UUBluetoothSessionState.Idle);
        }

        return error;
    }

    private Pair<UUBluetoothSessionError, byte[]> readData(final SessionRunnable runnable, final long count, final long timeout)
    {
        UUBluetoothSessionError error = null;
        byte[] response = null;

        try
        {
            debugLog("readData", "count: " + count + ", timeout: " + timeout);

            if (bluetoothSocket != null)
            {
                InputStream is = bluetoothSocket.getInputStream();

                long bytesRead = 0;
                response = new byte[(int)count];

                while (bytesRead < count)
                {
                    startReadDataWatchdog(timeout, runnable);

                    int read = is.read(response, (int)bytesRead, (int)(response.length - bytesRead));

                    if (read > 0)
                    {
                        debugLog("readData", "RXChunk: " + UUString.byteToHex(response, (int)bytesRead, read));
                        bytesRead += read;
                    }
                    else
                    {
                        debugLog("readData", "inputStream.read returned " + read + ", totalRead: " + bytesRead + ", expectedToRead: " + count);
                    }
                }
            }
            else
            {
                error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.SocketNotConnected);
            }
        }
        catch (Exception ex)
        {
            error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.ReadFailedFromException, ex);
        }
        finally
        {
            cancelReadDataWatchdog();
        }

        return new Pair<>(error, response);
    }

    private Timer readDataWatchdogTimeout = null;
    private void startReadDataWatchdog(final long timeout, final SessionRunnable runnable)
    {
        TimerTask task = new TimerTask()
        {
            public void run()
            {
                try
                {
                    disconnect();

                    runnable.safeNotify("readDataWatchdog.run");
                }
                catch (Exception ex)
                {
                    logException("readDataWatchdogTimer.run", ex);
                }
            }
        };

        // Cancel if its going.
        cancelReadDataWatchdog();

        readDataWatchdogTimeout = new Timer();
        readDataWatchdogTimeout.schedule(task, timeout);
    }

    private void cancelReadDataWatchdog()
    {
        try
        {
            if (readDataWatchdogTimeout != null)
            {
                readDataWatchdogTimeout.cancel();
            }
        }
        catch (Exception ex)
        {
            logException("cancelReadDataWatchdog", ex);
        }
        finally
        {
            readDataWatchdogTimeout = null;
        }
    }

    private static void logException(String method, Exception ex)
    {
        Log.e(LOG_TAG, method, ex);
    }

    protected static void debugLog(String method, String msg)
    {
        Log.d(LOG_TAG, method + ": " + msg);
    }

    private void changeState(UUBluetoothSessionState state)
    {
        if (currentState != state)
        {
            currentState = state;
            debugLog("changeState", "New State: " + state);
        }
    }

    private void closeSocket()
    {
        try
        {
            if (bluetoothSocket != null)
            {
                bluetoothSocket.close();
            }
        }
        catch (Exception ex)
        {
            logException("closeSocket", ex);
        }
        finally
        {
            bluetoothSocket = null;
        }
    }

    abstract class SessionRunnable implements Runnable
    {
        private Thread thread;
        private UUBluetoothSessionError error;
        private boolean isActive = false;

        public UUBluetoothSessionError getError()
        {
            return error;
        }

        public boolean isActive()
        {
            return isActive;
        }

        abstract UUBluetoothSessionError execute();

        @Override
        public void run()
        {
            try
            {
                isActive = true;
                thread = Thread.currentThread();
                error = execute();
            }
            catch (Exception ex)
            {
                logException("run", ex);
            }
            finally
            {
                isActive = false;
            }
        }

        public void safeWait(final String fromWhere)
        {
            try
            {
                debugLog("safeWait", "Waiting from " + fromWhere);
                if (thread != null)
                {
                    synchronized (thread)
                    {
                        thread.wait();
                    }
                }
            }
            catch (Exception ex)
            {
                logException("safeWait", ex);
            }
        }

        public void safeNotify(String fromWhere)
        {
            try
            {
                debugLog("safeNotify", "Notify from " + fromWhere);
                if (thread != null)
                {
                    synchronized (thread)
                    {
                        thread.notify();
                    }
                }
            }
            catch (Exception ex)
            {
                logException("safeNotify", ex);
            }
        }
    }

    abstract class ErrorRunnable extends SessionRunnable
    {
        private ErrorCallback callback;

        public void setCallback(final ErrorCallback callback)
        {
            this.callback = callback;
        }

        @Override
        public void run()
        {
            super.run();
            notifyCallback();
        }

        private void notifyCallback()
        {
            try
            {
                if (callback != null)
                {
                    callback.onComplete(getError());
                }
            }
            catch (Exception ex)
            {
                logException("notifyCallback", ex);
            }
        }
    }

    abstract class DataRunnable extends SessionRunnable
    {
        private DataCallback callback;
        private byte[] data;

        public void setCallback(final DataCallback callback)
        {
            this.callback = callback;
        }

        protected void setData(final byte[] data)
        {
            this.data = data;
        }

        @Override
        public void run()
        {
            super.run();
            notifyCallback();
        }

        private void notifyCallback()
        {
            try
            {
                if (callback != null)
                {
                    callback.onComplete(getError(), data);
                }
            }
            catch (Exception ex)
            {
                logException("notifyCallback", ex);
            }
        }
    }

    abstract class DisconnectRunnable extends ErrorRunnable
    {
        private boolean unpair = false;

        public void setUnpair(final boolean unpair)
        {
            this.unpair = unpair;
        }

        public boolean shouldUnpair()
        {
            return unpair;
        }
    }

    abstract class SendRunnable extends ErrorRunnable
    {
        private byte[] txData = null;

        public void setTxData(final byte[] buffer)
        {
            txData = buffer;
        }

        public byte[] txData()
        {
            return txData;
        }
    }

    abstract class ReceiveRunnable extends DataRunnable
    {
        private long numberToReceive;
        private long readTimeout;

        public void setNumberToReceive(final long numberToReceive)
        {
            this.numberToReceive = numberToReceive;
        }

        public void setReadTimeout(final long readTimeout)
        {
            this.readTimeout = readTimeout;
        }

        public long getNumberToReceive()
        {
            return numberToReceive;
        }

        public long getReadTimeout()
        {
            return readTimeout;
        }
    }

    private ErrorRunnable connectRunnable()
    {
        return new ErrorRunnable()
        {
            @Override
            UUBluetoothSessionError execute()
            {
                UUBluetoothSessionError error;

                if (currentState == UUBluetoothSessionState.Connected)
                {
                    debugLog("connectRunnable.run", "Already connected");
                    return null;
                }

                if (currentState != UUBluetoothSessionState.Idle)
                {
                    debugLog("connectRunnable.run", "Current State is " + currentState + ", unable to connect now");
                    return new UUBluetoothSessionError(UUBluetoothSessionErrorCode.ConnectFailedInvalidState);
                }

                error = pair(this);
                debugLog("connectRunnable.run", "pair returned " + errToString(error));
                if (error != null)
                {
                    return error;
                }

                error = serviceDiscovery(this);
                debugLog("connectRunnable.run", "serviceDiscovery returned " + errToString(error));
                if (error != null)
                {
                    return error;
                }

                error = connect(true);
                debugLog("connectRunnable.run", "connect(true) returned " + errToString(error));
                if (error != null)
                {
                    error = connect(false);
                    debugLog("connectRunnable.run", "connect(false) returned " + errToString(error));
                }

                if (error == null)
                {
                    changeState(UUBluetoothSessionState.Connected);
                }
                else
                {
                    changeState(UUBluetoothSessionState.Idle);
                }

                return error;
            }
        };
    }

    private DisconnectRunnable disconnectRunnable()
    {
        return new DisconnectRunnable()
        {
            @Override
            UUBluetoothSessionError execute()
            {
                UUBluetoothSessionError error = disconnect();
                debugLog("disconnectRunnable.run", "disconnect returned " + errToString(error));
                if (error != null)
                {
                    return error;
                }

                if (shouldUnpair())
                {
                    error = unpair(this);
                    debugLog("disconnectRunnable.run", "unpair returned " + errToString(error));
                }

                unregisterReceivers();
                return error;
            }
        };
    }

    private SendRunnable sendRunnable()
    {
        return new SendRunnable()
        {
            @Override
            UUBluetoothSessionError execute()
            {
                UUBluetoothSessionError error = writeData(txData());
                debugLog("sendRunnable.run", "writeData returned " + errToString(error));
                return error;
            }
        };
    }

    private ReceiveRunnable receiveRunnable()
    {
        return new ReceiveRunnable()
        {
            @Override
            UUBluetoothSessionError execute()
            {
                Pair<UUBluetoothSessionError, byte[]> response = readData(this, getNumberToReceive(), getReadTimeout());
                UUBluetoothSessionError error = response.first;
                debugLog("receiveRunnable.run", "readData returned " + errToString(error));
                setData(response.second);
                return error;
            }
        };
    }

    public class UUBluetoothSessionError
    {
        private UUBluetoothSessionErrorCode errorCode;
        private Exception exception;

        public UUBluetoothSessionErrorCode getErrorCode()
        {
            return errorCode;
        }

        public Exception getException()
        {
            return exception;
        }

        public UUBluetoothSessionError(UUBluetoothSessionErrorCode errCode)
        {
            this(errCode, null);
        }

        public UUBluetoothSessionError(UUBluetoothSessionErrorCode errCode, Exception ex)
        {
            errorCode = errCode;
            exception = ex;
        }

        @Override
        public String toString()
        {
            return String.format(Locale.getDefault(), "ErrorCode: %s, Exception: %s", errorCode, exception != null ? exception.toString() : "null");
        }
    }

    static String errToString(UUBluetoothSessionError error)
    {
        if (error != null)
        {
            return error.toString();
        }
        else
        {
            return "null";
        }
    }

    public interface ScanFilter
    {
        boolean shouldScanDevice(BluetoothDevice device);
    }

    interface ScanCallback
    {
        void deviceFound(BluetoothDevice device);
    }

    public interface SessionCallback
    {
        void onComplete(UUBluetoothSessionError error, UUBluetoothSession session);
    }

    public interface ErrorCallback
    {
        void onComplete(UUBluetoothSessionError error);
    }

    public interface DataCallback
    {
        void onComplete(UUBluetoothSessionError error, byte[] data);
    }

    public static String bondStateToString(final int state)
    {
        switch (state)
        {
            case BluetoothDevice.BOND_NONE:
                return "BOND_NONE";
            case BluetoothDevice.BOND_BONDED:
                return "BOND_BONDED";
            case BluetoothDevice.BOND_BONDING:
                return "BOND_BONDING";
            default:
                return String.valueOf(state);
        }
    }



    public static void startSession(final Context context, final ScanFilter filter, final long timeout, final SessionCallback callback)
    {
        final Scanner scanner = new Scanner(context);
        scanner.scan(filter, new ScanCallback()
        {
            @Override
            public void deviceFound(BluetoothDevice device)
            {
                scanner.stopScan();
                notifyStartSession(context, device, callback);
            }
        });
    }

    private static void notifyStartSession(final Context context, final BluetoothDevice device, final SessionCallback callback)
    {
        try
        {
            if (device != null && callback != null)
            {
                final UUBluetoothSession session = new UUBluetoothSession(context, device);
                session.startSession(new ErrorCallback()
                {
                    @Override
                    public void onComplete(final UUBluetoothSessionError error)
                    {
                        notifySessionCallback(callback, error, session);
                    }
                });
            }
        }
        catch (Exception ex)
        {
            logException("notifyStartSession", ex);
        }
    }

    private static void notifySessionCallback(final SessionCallback callback, final UUBluetoothSessionError error, final UUBluetoothSession session)
    {
        try
        {
            if (callback != null)
            {
                callback.onComplete(error, session);
            }
        }
        catch (Exception ex)
        {
            logException("notifySessionCallback", ex);
        }
    }















    enum ScannerState
    {
        Idle,
        Discovery,
        StoppingDiscovery,
    }

    static class Scanner
    {
        private BluetoothAdapter bluetoothAdapter;
        private BroadcastReceiver broadcastReceiver;
        private ScanFilter scanFilter;
        private ScanCallback scanCallback;
        private ScannerState currentState;

        public Scanner(final Context context)
        {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            initBroadcastReceiver();
            context.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            context.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        }

        private void initBroadcastReceiver()
        {
            broadcastReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    String action = intent.getAction();
                    debugLog("onReceive", "Handled Bluetooth Action: " + action);

                    switch (action)
                    {
                        case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                            handleDiscoveryFinished(intent);
                            break;

                        case BluetoothDevice.ACTION_FOUND:
                            handleDeviceFound(intent);
                            break;
                    }
                }
            };
        }

        private void handleDiscoveryFinished(final Intent intent)
        {
            switch (currentState)
            {
                case Discovery:
                    scan(scanFilter, scanCallback);
                    break;

                case StoppingDiscovery:
                    changeState(ScannerState.Idle);
                    break;
            }
        }

        private void handleDeviceFound(final Intent intent)
        {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            debugLog("handleDeviceFound", "Device discovered: " + device.getName() + ", " + device.getAddress() + ", BondState: " + device.getBondState() + ", State: " + currentState);

            if (filterDevice(device))
            {
                notifyDeviceFound(device);
            }
        }

        public void scan(final ScanFilter filter, final ScanCallback callback)
        {
            scanFilter = filter;
            scanCallback = callback;

            boolean discoveryStarted = false;

            try
            {
                discoveryStarted = bluetoothAdapter.startDiscovery();
                debugLog("scan", "bluetoothAdapter.startDiscovery() returned " + discoveryStarted);
            }
            catch (Exception ex)
            {
                logException("discover", ex);
                discoveryStarted = false;
            }
            finally
            {
                if (discoveryStarted)
                {
                    changeState(ScannerState.Discovery);
                }
                else
                {
                    changeState(ScannerState.Idle);
                }
            }
        }

        public void stopScan()
        {
            try
            {
                changeState(ScannerState.StoppingDiscovery);
                boolean result = bluetoothAdapter.cancelDiscovery();
                debugLog("stopScan", "bluetoothAdapter.cancelDiscovery() returned " + result);
            }
            catch (Exception ex)
            {
                logException("stopScan", ex);
            }
        }

        private void changeState(ScannerState state)
        {
            if (currentState != state)
            {
                currentState = state;
                debugLog("changeState", "New State: " + state);
            }
        }

        private boolean filterDevice(final BluetoothDevice device)
        {
            boolean includeDevice = true;

            try
            {
                if (scanFilter != null && device != null && currentState == ScannerState.Discovery)
                {
                    includeDevice = scanFilter.shouldScanDevice(device);
                }
            }
            catch (Exception ex)
            {
                logException("filterDevice", ex);
            }

            return includeDevice;
        }

        private void notifyDeviceFound(final BluetoothDevice device)
        {
            try
            {
                if (scanCallback != null && device != null && currentState == ScannerState.Discovery)
                {
                    scanCallback.deviceFound(device);
                }
            }
            catch (Exception ex)
            {
                logException("notifyDeviceFound", ex);
            }
        }
    }
}