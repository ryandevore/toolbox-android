package uu.toolbox.bluetooth.classic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import uu.toolbox.bluetooth.UUBluetoothScanner;
import uu.toolbox.core.UUWorkerThread;
import uu.toolbox.logging.UULog;

@SuppressWarnings("unused")
public class UUClassicBluetoothScanner
{
    private static boolean LOGGING_ENABLED = UULog.LOGGING_ENABLED;

    public interface Listener
    {
        void onClassicBluetoothDeviceFound(final @NonNull UUClassicBluetoothScanner scanner, final @NonNull BluetoothDevice device);
    }

    private enum ScannerState
    {
        Idle,
        Discovery,
        StoppingDiscovery,
    }

    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver broadcastReceiver;
    private UUWorkerThread scanThread;
    private boolean isScanning = false;
    private ArrayList<UUBluetoothDeviceFilter> scanFilters;
    private final HashMap<String, Boolean> ignoredDevices = new HashMap<>();
    private Listener listener;
    private ScannerState currentState;

    public UUClassicBluetoothScanner(final Context context)
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
                executeScan();
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

        if (isIgnored(device))
        {
            debugLog("handleDeviceFound", "Ignoring scan result from " + device.getAddress());
            return;
        }

        if (shouldDiscoverDevice(device))
        {
            notifyDeviceFound(device);
        }
    }

    private boolean shouldDiscoverDevice(@NonNull final BluetoothDevice device)
    {
        if (scanFilters != null)
        {
            for (UUBluetoothDeviceFilter filter : scanFilters)
            {
                UUBluetoothDeviceFilter.Result result = filter.shouldDiscoverDevice(device);
                if (result == UUBluetoothDeviceFilter.Result.IgnoreForever)
                {
                    ignoreDevice(device);
                    return false;
                }

                if (result == UUBluetoothDeviceFilter.Result.IgnoreOnce)
                {
                    return false;
                }
            }

            return true;
        }
        else
        {
            return true;
        }
    }

    private synchronized boolean isIgnored(@Nullable final BluetoothDevice device)
    {
        return (device == null || ignoredDevices.containsKey(device.getAddress()));
    }

    public synchronized void ignoreDevice(@NonNull final BluetoothDevice device)
    {
        ignoredDevices.put(device.getAddress(), Boolean.TRUE);
    }

    public synchronized void clearIgnoreList()
    {
        ignoredDevices.clear();
    }


    public void startScanning(
            final @Nullable ArrayList<UUBluetoothDeviceFilter> filters,
            final @NonNull Listener delegate)
    {
        scanFilters = filters;
        listener = delegate;
        executeScan();
    }

    private void executeScan()
    {
        boolean discoveryStarted = false;

        try
        {
            discoveryStarted = bluetoothAdapter.startDiscovery();
            debugLog("scan", "bluetoothAdapter.startDiscovery() returned " + discoveryStarted);
        }
        catch (Exception ex)
        {
            debugLog("discover", ex);
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
            debugLog("stopScan", ex);
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

    private void notifyDeviceFound(final BluetoothDevice device)
    {
        try
        {
            if (listener != null && device != null && currentState == ScannerState.Discovery)
            {
                listener.onClassicBluetoothDeviceFound(this, device);
            }
        }
        catch (Exception ex)
        {
            debugLog("notifyDeviceFound", ex);
        }
    }

    private static void debugLog(final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(UUBluetoothScanner.class, method, message);
        }
    }

    private synchronized static void debugLog(final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(UUBluetoothScanner.class, method, exception);
        }
    }
}