package uu.toolboxapp;

import android.app.Application;
import android.content.Context;

import uu.toolbox.bluetooth.UUBluetoothBroadcastReceiver;
import uu.toolbox.data.UUDataCache;
import uu.toolbox.network.UURemoteData;
import uu.toolbox.network.UURemoteImage;
import uu.toolboxapp.data.AppDatabase;

/**
 * Created by ryandevore on 9/14/16.
 */
public class ToolboxSampleApplication extends Application
{
    private UUBluetoothBroadcastReceiver receiver;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Context context = getApplicationContext();
        AppDatabase.init(context);
        UUDataCache.init(context);
        UURemoteData.init(context);
        UURemoteImage.init(context);

        receiver = new UUBluetoothBroadcastReceiver(context);
    }
}
