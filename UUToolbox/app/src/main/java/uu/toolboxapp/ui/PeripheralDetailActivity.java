package uu.toolboxapp.ui;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import uu.toolbox.bluetooth.UUBluetooth;
import uu.toolbox.bluetooth.UUBluetoothError;
import uu.toolbox.bluetooth.UUConnectionDelegate;
import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralDelegate;
import uu.toolbox.core.UUThread;
import uu.toolbox.logging.UULog;
import uu.toolbox.ui.UUActivity;
import uu.toolboxapp.R;

import static uu.toolbox.bluetooth.UUPeripheral.ConnectionState.Disconnected;

/**
 * Created by ryandevore on 12/29/16.
 */

public class PeripheralDetailActivity extends AppCompatActivity
{
    private TextView nameLabel;
    private TextView macLabel;
    private TextView rssiLabel;
    private TextView connectionStateLabel;

    private Button connectButton;
    private Button discoverServicesButton;

    private UUPeripheral peripheral;
    private final ArrayList<BluetoothGattService> tableData = new ArrayList<>();
    private PeripheralDetailActivity.BtleServiceAdapter tableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral_detail);

        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.hasExtra("peripheral"))
            {
                peripheral = intent.getParcelableExtra("peripheral");
            }
        }

        if (peripheral == null)
        {
            UUActivity.showToast(this, getClass().getName() + " requires a peripheral be passed with launch intent", Toast.LENGTH_SHORT);
            finish();
        }

        setTitle(peripheral.getName());

        nameLabel = UUActivity.findTextViewById(this, R.id.name);
        macLabel = UUActivity.findTextViewById(this, R.id.address);
        rssiLabel = UUActivity.findTextViewById(this, R.id.rssi);
        connectionStateLabel = UUActivity.findTextViewById(this, R.id.state);

        connectButton = UUActivity.findButtonById(this, R.id.connect_button);
        discoverServicesButton = UUActivity.findButtonById(this, R.id.discover_services_button);

        tableAdapter = new PeripheralDetailActivity.BtleServiceAdapter(this, tableData);
        ListView listView = UUActivity.findListViewById (this, R.id.services_list_view);
        if (listView != null)
        {
            listView.setAdapter(tableAdapter);
        }

        updateHeader();
        updateButtons();
    }

    private void updateHeader()
    {
        nameLabel.setText(peripheral.getName());
        macLabel.setText(peripheral.getAddress());
        rssiLabel.setText(String.format(Locale.US, "%d", peripheral.getRssi()));
        connectionStateLabel.setText(peripheral.getConnectionState(getApplicationContext()).toString());
    }

    private void updateButtons()
    {
        UUPeripheral.ConnectionState state = peripheral.getConnectionState(getApplicationContext());

        switch (state)
        {
            case Connecting:
            case Connected:
            {
                connectButton.setText(R.string.disconnect);
                break;
            }

            default:
            case Disconnected:
            case Disconnecting:
            {
                connectButton.setText(R.string.connect);
                break;
            }
        }

        // Peripheral should never get 'stuck' in intermediate state
        connectButton.setEnabled((state == UUPeripheral.ConnectionState.Connected || state == Disconnected));
    }

    private void updateTable()
    {
        tableData.clear();
        tableData.addAll(peripheral.discoveredServices());
        tableAdapter.notifyDataSetChanged();
    }

    private void updateUi()
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                updateHeader();
                updateButtons();
                updateTable();
            }
        });
    }

    public void onConnectClicked(View view)
    {
        UUPeripheral.ConnectionState connectionState = peripheral.getConnectionState(getApplicationContext());

        switch (connectionState)
        {
            case Connected:
            {
                disconnect();
                break;
            }

            case Disconnected:
            {
                connect();
                break;
            }

            default:
            {
                UULog.debug(getClass(), "onConnectClicked", "Peripheral is in between states: " + connectionState);
                break;
            }
        }
    }

    public void onReadRssiClicked(View view)
    {
        peripheral.readRssi(300000, new UUPeripheralDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                updateUi();
            }
        });
    }

    public void onPollRssiClicked(View view)
    {
        /*
        final UUTimer t = new UUTimer("RssiPoller", 500, true, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                UULog.debug(getClass(), "RssiPoller.onTimer", "tick, userInfo: " + userInfo + ", isMainThread: " + UUThread.isMainThread());
            }
        });

        t.start();

        UUTimer.startTimer("CancelPoller", 0, null, new UUTimer.WatchdogTimerDelegate()
        {
            @Override
            public void onTimer(@Nullable Object userInfo)
            {
                UULog.debug(getClass(), "CancelPoller.onTimer", "Cancelling repeat timer");
                t.cancel();
            }
        });*/

    }

    public void onDiscoverServicesClicked(View view)
    {
        peripheral.discoverServices(10000, new UUPeripheralDelegate()
        {
            @Override
            public void onComplete(final @NonNull UUPeripheral peripheral, final @Nullable UUBluetoothError error)
            {
                UULog.debug(getClass(), "onDiscoverServices.onComplete", "Peripheral: " + peripheral + ", Error: " + error);

                UUThread.runOnMainThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tableData.clear();
                        tableData.addAll(peripheral.discoveredServices());
                        tableAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void connect()
    {
        UULog.debug(getClass(), "connect", "Connecting to : " + peripheral);
        UUBluetooth.connectPeripheral(this, peripheral, false, 10000, new UUConnectionDelegate()
        {
            @Override
            public void onConnected(@NonNull UUPeripheral peripheral)
            {
                UULog.debug(getClass(), "connect.onConnected", "Peripheral connected, " + peripheral);
                updateUi();
            }

            @Override
            public void onDisconnected(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                UULog.debug(getClass(), "connect.onConnected", "Peripheral disconnected, " + peripheral);
                updateUi();
            }
        });
    }

    private void disconnect()
    {
        UULog.debug(getClass(), "disconnect", "Disconnecting from : " + peripheral);
        UUBluetooth.disconnectPeripheral(peripheral);
    }

    private void onServiceClicked(final @NonNull BluetoothGattService service)
    {
        Intent intent = new Intent(this, ServiceDetailActivity.class);
        intent.putExtra("peripheral", peripheral);
        intent.putExtra("serviceUuid", service.getUuid().toString());
        startActivity(intent);
    }

    class BtleServiceAdapter extends ArrayAdapter<BluetoothGattService>
    {
        public BtleServiceAdapter(Context context, ArrayList<BluetoothGattService> data)
        {
            super(context, R.layout.service_list_row, data);
        }

        @Override
        public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;
            if (view == null)
            {
                view = inflater.inflate(R.layout.service_list_row, parent, false);
            }

            final BluetoothGattService rowData = getItem(position);
            if (rowData != null)
            {
                ServiceListRow row = new ServiceListRow(view);
                row.update(rowData);

                view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onServiceClicked(rowData);
                    }
                });
            }

            return view;
        }
    }
}