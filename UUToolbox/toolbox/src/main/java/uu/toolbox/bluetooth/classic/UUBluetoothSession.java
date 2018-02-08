package uu.toolbox.bluetooth.classic;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import uu.toolbox.bluetooth.UUBluetooth;
import uu.toolbox.bluetooth.UUBluetoothConstants;
import uu.toolbox.bluetooth.UUBluetoothError;
import uu.toolbox.bluetooth.UUBluetoothSessionErrorDelegate;
import uu.toolbox.core.UUString;
import uu.toolbox.core.UUThread;
import uu.toolbox.core.UUTimer;
import uu.toolbox.logging.UULog;

public class UUBluetoothSession
{
    private static boolean LOGGING_ENABLED = UULog.LOGGING_ENABLED;

    private static final String CONNECT_WATCHDOG_BUCKET = "UUBluetoothSession_ConnectWatchdogBucket";
    private static final String PAIR_WATCHDOG_BUCKET = "UUBluetoothSession_PairWatchdogBucket";
    private static final String UNPAIR_WATCHDOG_BUCKET = "UUBluetoothSession_UnPairWatchdogBucket";
    private static final String SERVICE_DISCOVERY_WATCHDOG_BUCKET = "UUBluetoothSession_ServiceDiscoveryWatchdogBucket";
    private static final String CONNECT_SPP_WATCHDOG_BUCKET = "UUBluetoothSession_ConnectSppWatchdogBucket";
    private static final String DISCONNECT_SPP_WATCHDOG_BUCKET = "UUBluetoothSession_DisconnectSppWatchdogBucket";
//    private static final String CHARACTERISTIC_NOTIFY_STATE_WATCHDOG_BUCKET = "UUBluetoothSession_CharacteristicNotifyStateWatchdogBucket";
//    private static final String READ_CHARACTERISTIC_WATCHDOG_BUCKET = "UUBluetoothSession_ReadCharacteristicValueWatchdogBucket";
//    private static final String WRITE_CHARACTERISTIC_WATCHDOG_BUCKET = "UUBluetoothSession_WriteCharacteristicValueWatchdogBucket";
//    private static final String READ_DESCRIPTOR_WATCHDOG_BUCKET = "UUBluetoothSession_ReadDescriptorValueWatchdogBucket";
//    private static final String WRITE_DESCRIPTOR_WATCHDOG_BUCKET = "UUBluetoothSession_WriteDescriptorValueWatchdogBucket";
//    private static final String READ_RSSI_WATCHDOG_BUCKET = "UUBluetoothSession_ReadRssiWatchdogBucket";
//    private static final String POLL_RSSI_BUCKET = "UUBluetoothSession_PollRssiBucket";
    private static final String DISCONNECT_WATCHDOG_BUCKET = "UUBluetoothSession_DisconnectWatchdogBucket";

//    private static final int TIMEOUT_DISABLED = -1;


    //private static final String LOG_TAG = "UUBluetoothSession";

//    enum UUBluetoothSessionState
//    {
//        Idle,
//        //WaitingForPairing,
//        //WaitingForConnect,
//        //WaitingForServiceDiscovery,
//        //Connected,
//        //WaitingForDisconnect,
//        //WaitingForUnpairing,
//    }

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

        Timeout,
        Disconnected
    }

//    public interface ConnectionDelegate
//    {
//        /**
//         * Invoked when a device is successfully connected.
//         *
//         * @param session the device that was connected
//         */
//        void onConnected(@NonNull final UUBluetoothSession session);
//
//        /**
//         * Invoked when a device was disconnected
//         *
//         * @param session the session that was disconnected
//         * @param error the error (if any) that caused the disconnect to occur
//         */
//        void onDisconnected(@NonNull final UUBluetoothSession session, @Nullable final UUBluetoothSessionError error);
//    }


    private Context context;

    private BluetoothDevice device;
    private String deviceAddress;
    private BroadcastReceiver broadcastReceiver;
    //private UUBluetoothSessionState currentState;
    private BluetoothSocket bluetoothSocket;
    //private ErrorRunnable connectRunnable;
    //private DisconnectRunnable disconnectRunnable;


    //private ConnectionDelegate connectionDelegate;
    private long connectTimeout;
    private long disconnectTimeout;
    private UUBluetoothError disconnectError;

    private UUBluetoothSessionErrorDelegate startSessionSppDelegate;

    private UUBluetoothSessionErrorDelegate pairDelegate;
    private UUBluetoothSessionErrorDelegate unpairDelegate;

    private UUBluetoothSessionErrorDelegate serviceDiscoveryDelegate;

    private UUBluetoothSessionErrorDelegate connectSppDelegate;
    private UUBluetoothSessionErrorDelegate disconnectSppDelegate;


    public UUBluetoothSession(Context context, BluetoothDevice device)
    {
        this.context = context;
        this.device = device;
        if (device != null)
        {
            this.deviceAddress = device.getAddress().toLowerCase();
        }

        //currentState = UUBluetoothSessionState.Idle;

        initBroadcastReceiver();

        registerReceivers();
    }

    public BluetoothDevice getDevice()
    {
        return device;
    }


    private void registerReceivers()
    {
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
        //registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));

//        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
//        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
//        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
//        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));
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
                debugLog("onReceive", intent);

                String action = intent.getAction();

                if (shouldHandleIntent(intent))
                {
                    debugLog("onReceive", "Handled Bluetooth Action: " + action + ", device: " + getIntentDeviceAddress(intent));

                    switch (action)
                    {
                        case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        {
                            handleBondStateChanged(intent);
                            break;
                        }

                        case BluetoothDevice.ACTION_UUID:
                        {
                            handleActionUuid(intent);
                            break;
                        }

/*
                        case BluetoothDevice.ACTION_PAIRING_REQUEST:
                        {
                            debugLog("handlePairingRequest", "Setting pairingConfirmation");
                            boolean result = device.setPairingConfirmation(true);
                            debugLog("handlePairingRequest", "Setting pairingConfirmation returned: " + result);

                            break;
                        }*/

//                        case BluetoothDevice.ACTION_ACL_DISCONNECTED:
//                        {
//                            handleDisconnected();
//                            break;
//                        }
                    }
                }
                else
                {
                    debugLog("onReceive", "Ignoring Bluetooth Action: " + action + ", device: " + getIntentDeviceAddress(intent));
                }
            }
        };
    }

    private void handleBondStateChanged(final Intent intent)
    {
        int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
        int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        debugLog("handleBondStateChanged", "Bond State for device " + device.getAddress() + " changed from " + UUBluetooth.bondStateToString(previousBondState) + " to " + UUBluetooth.bondStateToString(bondState));

        if ((previousBondState == BluetoothDevice.BOND_BONDING) &&
            (bondState == BluetoothDevice.BOND_BONDED || bondState == BluetoothDevice.BOND_NONE)/* &&
                currentState == UUBluetoothSessionState.WaitingForPairing*/ &&
                pairDelegate != null)
        {
            //connectRunnable.safeNotify("Pairing finished");
            UUBluetoothSessionError err = null;

            if (bondState == BluetoothDevice.BOND_NONE)
            {
                err = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.PairingFailed);
            }

            notifyPairingComplete(err);
        }
        else if ((previousBondState == BluetoothDevice.BOND_BONDING || previousBondState == BluetoothDevice.BOND_BONDED) &&
                (bondState == BluetoothDevice.BOND_NONE) &&
                /*currentState == UUBluetoothSessionState.WaitingForUnpairing*/
                unpairDelegate != null)
        {
            //disconnectRunnable.safeNotify("Unpairing finished");

            UUBluetoothSessionError err = null;

//            if (bondState != BluetoothDevice.BOND_NONE)
//            {
//                err = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.UnpairingFailed);
//            }

            notifyUnpairingComplete(null);
        }
    }

//    private void handleDisconnected()
//    {
//        notifyDisconnectedComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.Disconnected));
//    }

    private void handleActionUuid(final Intent intent)
    {
        //Parcelable[] list = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
        //debugLog("handleActionUUid", "UUID: " + UUString.safeToString(list));





        Parcelable extraUuids[] = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
        if (extraUuids != null)
        {

            boolean foundSpp = false;

            for (Parcelable p : extraUuids)
            {
                ParcelUuid parcelUuid = (ParcelUuid)p;
                UUID uuid = parcelUuid.getUuid();
                debugLog("handleActionUuid", "uuid: " + uuid.toString());

                if (uuid.compareTo(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID) == 0)
                {
                    foundSpp = true;
                    debugLog("handleActionUuid", "Found SPP profile on device: " + deviceAddress);
                }
            }

            if (foundSpp && serviceDiscoveryDelegate != null)
            {
                notifyServiceDiscoveryComplete(null);
            }

//            if (foundSpp && currentState == UUBluetoothSessionState.WaitingForServiceDiscovery)
//            {
//                debugLog("handleActionUuid", "Discovered Serial Port profile, kicking wait thread");
//                connectRunnable.safeNotify("SPP found uuid");
//            }
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

    /*
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
    }*/

    /*public void startSession(final ErrorCallback callback)
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
    }*/

    /*
    private UUBluetoothSessionError pair(final SessionRunnable runnable)
    {
        UUBluetoothSessionError error = null;

        int bondState = -1;

        try
        {
            if (device != null)
            {
                bondState = device.getBondState();
                debugLog("pair", "BondState is " + UUBluetooth.bondStateToString(bondState));
                if (bondState != BluetoothDevice.BOND_BONDED)
                {
                    changeState(UUBluetoothSessionState.WaitingForPairing);

                    debugLog("pair", "Attempting to pair with device " + deviceAddress);
                    boolean returnValue = device.createBond();
                    debugLog("pair", "createBond returned: " + returnValue);

                    runnable.safeWait("pair");

                    bondState = device.getBondState();
                    debugLog("pair", "BondState after pairing is " + UUBluetooth.bondStateToString(bondState));
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
    }*/

    public boolean isPaired()
    {
        return (device.getBondState() == BluetoothDevice.BOND_BONDED);
    }

    public void pair(final long timeout, @NonNull final UUBluetoothSessionErrorDelegate delegate)
    {
        final String timerId = pairWatchdogTimerId();

        UUBluetoothSessionErrorDelegate tmpDelegate = new UUBluetoothSessionErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSession.UUBluetoothSessionError error)
            {
                debugLog("pair", "Pairing complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        pairDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("pair", "Pair timeout: " + device);
                // TODO: Is this really how to handle timeout on pairing?
                //disconnect(UUBluetoothError.timeoutError());
                notifyPairingComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.Timeout));
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("pair", "device is null!");
                    return;
                }

                int bondState = device.getBondState();
                if (bondState == BluetoothDevice.BOND_BONDED)
                {
                    debugLog("pair", "device is already bonded.");
                    notifyPairingComplete(null);
                    return;
                }

//                if (bondState == BluetoothDevice.BOND_BONDING)
//                {
//                    debugLog("pair", "device is in the process of bonding.");
//                    return;
//                }

                debugLog("pair", "Attempting to pair with device " + deviceAddress);
                boolean success = device.createBond();
                debugLog("pair", "createBond returned: " + success);

                if (!success)
                {
                    notifyPairingComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.PairingFailed));
                }
            }
        });
    }

    public void unpair(final long timeout, @NonNull final UUBluetoothSessionErrorDelegate delegate)
    {
        final String timerId = unpairWatchdogTimerId();

        UUBluetoothSessionErrorDelegate tmpDelegate = new UUBluetoothSessionErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSession.UUBluetoothSessionError error)
            {
                debugLog("unpair", "Unpairing complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        unpairDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("unpair", "Unpair timeout: " + device);
                // TODO: Is this really how to handle timeout on pairing?
                //disconnect(UUBluetoothError.timeoutError());
                notifyUnpairingComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.Timeout));
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("unpair", "device is null!");
                    return;
                }

                int bondState = device.getBondState();
                if (bondState != BluetoothDevice.BOND_BONDED)
                {
                    debugLog("unpair", "device is already bonded.");
                    return;
                }

                debugLog("unpair", "Attempting to unpair with device " + deviceAddress);
                boolean success = callRemoveBond(device);
                debugLog("unpair", "removeBond returned: " + success);

                if (!success)
                {
                    notifyUnpairingComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.UnpairingFailed));
                }


                /*
                if (bondState == BluetoothDevice.BOND_BONDED)
                {
                    changeState(UUBluetoothSessionState.WaitingForUnpairing);

                    debugLog("unpair", "Removing bond with device: " + deviceAddress);
                    Method m =  device.getClass().getMethod("removeBond", (Class[])null);
                    Boolean returnValue = (Boolean)m.invoke(device, (Object[])null);
                    debugLog("unpair", "removeBond returned: " + returnValue.toString());

                    runnable.safeWait("unpair");

                    bondState = device.getBondState();
                    debugLog("unpair", "BondState after unpairing is " + UUBluetooth.bondStateToString(bondState));
                    if (bondState != BluetoothDevice.BOND_NONE)
                    {
                        error = new UUBluetoothSessionError(UUBluetoothSessionErrorCode.UnpairingFailed);
                    }
                }*/
            }
        });
    }

    private boolean callRemoveBond(@NonNull final BluetoothDevice device)
    {
        try
        {
            Method m =  device.getClass().getMethod("removeBond", (Class[])null);
            if (m != null)
            {
                Boolean result = (Boolean) m.invoke(device, (Object[]) null);
                if (result != null)
                {
                    return result;
                }
            }
        }
        catch (Exception ex)
        {
            return false;
        }

        return false;
    }

    public boolean hasService(@NonNull final UUID uuid)
    {
        List<UUID> services = discoveredServiceUuids();
        return services.contains(uuid);
    }

    public boolean hasSppService()
    {
        return hasService(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID);
    }

    public void discoverServices(final long timeout, @NonNull final UUBluetoothSessionErrorDelegate delegate)
    {
        final String timerId = serviceDiscoveryWatchdogTimerId();

        UUBluetoothSessionErrorDelegate tmpDelegate = new UUBluetoothSessionErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSession.UUBluetoothSessionError error)
            {
                debugLog("discoverServices", "Service Discovery complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        serviceDiscoveryDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("discoverServices", "Service Discovery timeout: " + device);
                // TODO: Is this really how to handle timeout on service discovery?
                //disconnect(UUBluetoothError.timeoutError());
                notifyServiceDiscoveryComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.Timeout));
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("discoverServices", "device is null!");
                    return;
                }

                // TODO: Does device have to be bonded here?
                /*
                int bondState = device.getBondState();
                if (bondState == BluetoothDevice.BOND_BONDED)
                {
                    debugLog("pair", "device is already bonded.");
                    return;
                }*/


                debugLog("serviceDiscovery", "Calling fetchUuidsWithSdp");
                boolean success = device.fetchUuidsWithSdp();
                debugLog("serviceDiscovery", "device.fetchUuidsWithSdp returned " + success);

                if (!success)
                {
                    notifyServiceDiscoveryComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.ServiceDiscoveryFailed));
                }
            }
        });
    }

    @NonNull
    public List<UUID> discoveredServiceUuids()
    {
        ArrayList<UUID> list = new ArrayList<>();

        if (device != null)
        {
            ParcelUuid[] cached = device.getUuids();
            if (cached != null)
            {
                for (ParcelUuid parcelUuid : cached)
                {
                    list.add(parcelUuid.getUuid());
                }
            }
        }

        return list;
    }

    public boolean isSppConnected()
    {
        return (bluetoothSocket != null && bluetoothSocket.isConnected());
    }

    public void connectSpp(final long timeout, final boolean secureConnection, @NonNull final UUBluetoothSessionErrorDelegate delegate)
    {
        final String timerId = connectSppWatchdogTimerId();

        UUBluetoothSessionErrorDelegate tmpDelegate = new UUBluetoothSessionErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSession.UUBluetoothSessionError error)
            {
                debugLog("connectSpp", "Connect SPP complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        connectSppDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("connectSpp", "Connect SPP timeout: " + device);
                // TODO: Is this really how to handle timeout on connect SPP?
                //disconnect(UUBluetoothError.timeoutError());
                notifyConnectSppComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.Timeout));
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("connectSpp", "device is null!");
                    return;
                }

                // TODO: Does device have to be bonded here?
                /*
                int bondState = device.getBondState();
                if (bondState == BluetoothDevice.BOND_BONDED)
                {
                    debugLog("pair", "device is already bonded.");
                    return;
                }*/


                try
                {
                    if (secureConnection)
                    {
                        debugLog("connectSpp", "Creating socket with createRfcommSocketToServiceRecord");
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID);
                        debugLog("connectSpp", "createRfcommSocketToServiceRecord returned " + ((bluetoothSocket != null) ? bluetoothSocket.toString() : "null"));
                    }
                    else
                    {
                        debugLog("connectSpp", "Creating socket with createInsecureRfcommSocketToServiceRecord");
                        bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID);
                        debugLog("connectSpp", "createInsecureRfcommSocketToServiceRecord returned " + ((bluetoothSocket != null) ? bluetoothSocket.toString() : "null"));
                    }

                    if (bluetoothSocket == null)
                    {
                        // TODO: better error code
                        notifyConnectSppComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.ConnectFailedInvalidState));
                        return;
                    }

                    // TODO: Does this block the main thread?
                    debugLog("connectSpp", "Opening Bluetooth Socket");
                    bluetoothSocket.connect();
                    debugLog("connectSpp", "Bluetooth Socket Open");

                    notifyConnectSppComplete(null);
                }
                catch (Exception ex)
                {
                    logException("connectSpp", ex);
                    notifyConnectSppComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.ConnectFailedFromException, ex));
                }
            }
        });
    }

    public void disconnectSpp(final long timeout, @NonNull final UUBluetoothSessionErrorDelegate delegate)
    {
        final String timerId = disconnectSppWatchdogTimerId();

        UUBluetoothSessionErrorDelegate tmpDelegate = new UUBluetoothSessionErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSession.UUBluetoothSessionError error)
            {
                debugLog("disconnectSpp", "Disconnect SPP complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        disconnectSppDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("disconnectSpp", "Connect SPP timeout: " + device);
                // TODO: Is this really how to handle timeout on connect SPP?
                //disconnect(UUBluetoothError.timeoutError());
                notifyDisconnectSppComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.Timeout));
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("disconnectSpp", "device is null!");
                    return;
                }

                closeSocket();
                notifyDisconnectSppComplete(null);
            }
        });
    }




/*
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
    }*/

/*
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
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID);
                    debugLog("connect", "createInsecureRfcommSocketToServiceRecord returned " + ((bluetoothSocket != null) ? bluetoothSocket.toString() : "null"));
                }
                else
                {
                    debugLog("connect", "Creating socket with createRfcommSocketToServiceRecord");
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID);
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
    }*/

    /*
    private UUBluetoothSessionError unpair(final SessionRunnable runnable)
    {
        UUBluetoothSessionError error = null;

        try
        {
            if (device != null)
            {
                int bondState = device.getBondState();
                debugLog("unpair", "BondState is " + UUBluetooth.bondStateToString(bondState));
                if (bondState == BluetoothDevice.BOND_BONDED)
                {
                    changeState(UUBluetoothSessionState.WaitingForUnpairing);

                    debugLog("unpair", "Removing bond with device: " + deviceAddress);
                    Method m =  device.getClass().getMethod("removeBond", (Class[])null);
                    Boolean returnValue = (Boolean)m.invoke(device, (Object[])null);
                    debugLog("unpair", "removeBond returned: " + returnValue.toString());

                    runnable.safeWait("unpair");

                    bondState = device.getBondState();
                    debugLog("unpair", "BondState after unpairing is " + UUBluetooth.bondStateToString(bondState));
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
    }*/

    /*
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
    }*/

//    private static void logException(String method, Exception ex)
//    {
//        Log.e(LOG_TAG, method, ex);
//    }
//
//    protected static void debugLog(String method, String msg)
//    {
//        Log.d(LOG_TAG, method + ": " + msg);
//    }

    private void debugLog(final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(getClass(), method, message);
        }
    }

    protected void debugLog(final String method, final Intent intent)
    {
        if (LOGGING_ENABLED)
        {
            UULog.logIntent(getClass(), method, "", intent);
        }
    }

    private void logException(final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            UULog.error(getClass(), method, exception);
        }
    }

    /*
    private void changeState(UUBluetoothSessionState state)
    {
        if (currentState != state)
        {
            currentState = state;
            debugLog("changeState", "New State: " + state);
        }
    }*/

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

    /*
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
    }*/

    /*
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
    }*/

    /*
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
    }*/

    /*
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
    }*/

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

    /*
    public interface ScanFilter
    {
        boolean shouldScanDevice(BluetoothDevice device);
    }

    interface ScanCallback
    {
        void deviceFound(BluetoothDevice device);
    }*/

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

    /*
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
    }*/

    private static void notifyStartSession(final Context context, final BluetoothDevice device, final SessionCallback callback)
    {
        /*try
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
        }*/
    }

    /*private static void notifySessionCallback(final SessionCallback callback, final UUBluetoothSessionError error, final UUBluetoothSession session)
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
    }*/

    private void notifyErrorCallback(final UUBluetoothSessionError error, final UUBluetoothSessionErrorDelegate callback)
    {
        try
        {
            if (callback != null)
            {
                callback.onComplete(this, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyErrorCallback", ex);
        }
    }

    private void notifyStartSppSessionComplete(final UUBluetoothSessionError error)
    {
        UUBluetoothSessionErrorDelegate delegate = startSessionSppDelegate;
        startSessionSppDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyPairingComplete(final UUBluetoothSessionError error)
    {
        UUBluetoothSessionErrorDelegate delegate = pairDelegate;
        pairDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyServiceDiscoveryComplete(final UUBluetoothSessionError error)
    {
        UUBluetoothSessionErrorDelegate delegate = serviceDiscoveryDelegate;
        serviceDiscoveryDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyUnpairingComplete(final UUBluetoothSessionError error)
    {
        UUBluetoothSessionErrorDelegate delegate = unpairDelegate;
        unpairDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyConnectSppComplete(final UUBluetoothSessionError error)
    {
        UUBluetoothSessionErrorDelegate delegate = connectSppDelegate;
        connectSppDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyDisconnectSppComplete(final UUBluetoothSessionError error)
    {
        UUBluetoothSessionErrorDelegate delegate = disconnectSppDelegate;
        disconnectSppDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    /*
    private void notifyConnectComplete(final UUBluetoothSessionError error)
    {
        ConnectionDelegate delegate = connectionDelegate;

        try
        {
            if (delegate != null)
            {
                delegate.onConnected(this);
            }
        }
        catch (Exception ex)
        {
            logException("notifyConnectComplete", ex);
        }
    }

    private void notifyDisconnectedComplete(final UUBluetoothSessionError error)
    {
        ConnectionDelegate delegate = connectionDelegate;
        connectionDelegate = null;

        try
        {
            if (delegate != null)
            {
                delegate.onDisconnected(this, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyDisconnectedComplete", ex);
        }
    }*/




























    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Timers
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private @NonNull String timerPrefix()
    {
        return String.format(Locale.US, "Classic__%s", device.getAddress());
    }

    private @NonNull String formatDeviceTimerId(final @NonNull String bucket)
    {
        return String.format(Locale.US, "%s__%s", timerPrefix(), bucket);
    }

    private @NonNull String connectWatchdogTimerId()
    {
        return formatDeviceTimerId(CONNECT_WATCHDOG_BUCKET);
    }

    private @NonNull String pairWatchdogTimerId()
    {
        return formatDeviceTimerId(PAIR_WATCHDOG_BUCKET);
    }

    private @NonNull String unpairWatchdogTimerId()
    {
        return formatDeviceTimerId(UNPAIR_WATCHDOG_BUCKET);
    }

    private @NonNull String serviceDiscoveryWatchdogTimerId()
    {
        return formatDeviceTimerId(SERVICE_DISCOVERY_WATCHDOG_BUCKET);
    }

    private @NonNull String connectSppWatchdogTimerId()
    {
        return formatDeviceTimerId(CONNECT_SPP_WATCHDOG_BUCKET);
    }

    private @NonNull String disconnectSppWatchdogTimerId()
    {
        return formatDeviceTimerId(DISCONNECT_SPP_WATCHDOG_BUCKET);
    }

    private @NonNull String disconnectWatchdogTimerId()
    {
        return formatDeviceTimerId(DISCONNECT_WATCHDOG_BUCKET);
    }

    public void cancelAllTimers()
    {
        try
        {
            if (device != null)
            {
                ArrayList<UUTimer> list = UUTimer.listActiveTimers();

                String prefix = timerPrefix();
                for (UUTimer t : list)
                {
                    if (t.getTimerId().startsWith(prefix))
                    {
                        debugLog("cancelAllTimers", "Cancelling device timer: " + t.getTimerId());
                        t.cancel();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logException("cancelAllTimers", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Connection Management
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void startSppSession(
            //final @NonNull Context context,
            //final @NonNull BluetoothDevice device,
            final long timeout,
            //final long disconnectTimeout,
            final boolean secureSppSocket,
            final @NonNull UUBluetoothSessionErrorDelegate delegate)
    {
        final String timerId = connectWatchdogTimerId();

        UUBluetoothSessionErrorDelegate tmpDelegate = new UUBluetoothSessionErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSessionError error)
            {
                debugLog("startSppSession", "Start SPP Session complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        startSessionSppDelegate = tmpDelegate;


        /*
        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("startSppSession", "Start SPP timeout: " + device);

                disconnect(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.Timeout));
            }
        });*/

        //this.disconnectTimeout = disconnectTimeout;
        /*UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                debugLog("connect", "Connecting to: " + peripheral);

                disconnectError = null;
                //connectGatt(context, peripheral.getBluetoothDevice(), connectGattAutoFlag);
            }
        });*/

        pair(timeout, new UUBluetoothSessionErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSession.UUBluetoothSessionError pairingError)
            {
                UULog.debug(getClass(), "pair.onComplete", "error: " + UUString.safeToString(pairingError));
                if (pairingError != null)
                {
                    //disconnect(pairingError);
                    notifyStartSppSessionComplete(pairingError);
                    return;
                }

                session.discoverServices(timeout, new UUBluetoothSessionErrorDelegate()
                {
                    @Override
                    public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSession.UUBluetoothSessionError serviceDiscoveryError)
                    {
                        UULog.debug(getClass(), "discoverServices.onComplete", "error: " + UUString.safeToString(serviceDiscoveryError));

                        if (serviceDiscoveryError != null)
                        {
                            notifyStartSppSessionComplete(serviceDiscoveryError);
                            return;
                        }

                        if (!session.hasSppService())
                        {
                            // TODO: real error code here
                            notifyStartSppSessionComplete(new UUBluetoothSessionError(UUBluetoothSessionErrorCode.ServiceDiscoveryFailed));
                            return;
                        }

                        session.connectSpp(timeout, secureSppSocket, new UUBluetoothSessionErrorDelegate()
                        {
                            @Override
                            public void onComplete(@NonNull final UUBluetoothSession session, @Nullable UUBluetoothSession.UUBluetoothSessionError connectSppError)
                            {
                                UULog.debug(getClass(), "connectSpp.onComplete", "error: " + UUString.safeToString(connectSppError));
                                notifyStartSppSessionComplete(connectSppError);
                            }
                        });
                    }
                });
            }
        });
    }

    public void disconnect(@Nullable final UUBluetoothSessionError error)
    {
        closeSocket();
        unregisterReceivers();

        unpair(disconnectTimeout, new UUBluetoothSessionErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSessionError error)
            {
                //notifyDisconnectedComplete(error);
            }
        });

//        UUBluetoothGatt gatt = gattForPeripheral(peripheral);
//        if (gatt != null)
//        {
//            gatt.disconnect(null);
//        }
    }
}