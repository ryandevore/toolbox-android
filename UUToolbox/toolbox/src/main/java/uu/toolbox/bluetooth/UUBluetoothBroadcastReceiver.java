package uu.toolbox.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;

import uu.toolbox.logging.UULog;

public class UUBluetoothBroadcastReceiver extends BroadcastReceiver
{
    private Context context;

    public UUBluetoothBroadcastReceiver(@NonNull final Context context)
    {
        this.context = context;

        registerForEvent(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerForEvent(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerForEvent(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerForEvent(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        registerForEvent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        registerForEvent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        registerForEvent(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerForEvent(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerForEvent(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerForEvent(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        registerForEvent(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerForEvent(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerForEvent(BluetoothDevice.ACTION_CLASS_CHANGED);
        registerForEvent(BluetoothDevice.ACTION_FOUND);
        registerForEvent(BluetoothDevice.ACTION_NAME_CHANGED);

        registerForEvent(BluetoothDevice.ACTION_UUID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            registerForEvent(BluetoothDevice.ACTION_PAIRING_REQUEST);
        }
    }

    public void unregisterAll()
    {
        try
        {
            context.unregisterReceiver(this);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "unregisterAll", ex);
        }
    }

    private void registerForEvent(@NonNull final String event)
    {
        try
        {
            context.registerReceiver(this, new IntentFilter(event));
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "registerForEvent", ex);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        UULog.logIntent(getClass(), "onReceive", "", intent);
    }
}
