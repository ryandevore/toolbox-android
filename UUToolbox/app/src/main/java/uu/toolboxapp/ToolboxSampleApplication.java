package uu.toolboxapp;

import android.app.Application;
import android.content.Context;

import uu.toolbox.bluetooth.UUBluetoothBroadcastReceiver;
import uu.toolbox.data.UUDatabase;
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
        UUDatabase.init(context, AppDatabase.class);

        receiver = new UUBluetoothBroadcastReceiver(context);
    }
}
