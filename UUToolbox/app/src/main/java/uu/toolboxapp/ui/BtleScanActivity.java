package uu.toolboxapp.ui;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import uu.toolbox.bluetooth.UUBluetoothScanner;
import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralFactory;
import uu.toolbox.bluetooth.UUPeripheralFilter;
import uu.toolbox.core.UUInteger;
import uu.toolbox.core.UUPermissions;
import uu.toolbox.core.UUString;
import uu.toolbox.core.UUThread;
import uu.toolbox.logging.UULog;
import uu.toolboxapp.R;
import uu.toolboxapp.bluetooth.CustomPeripheral;

public class BtleScanActivity extends AppCompatActivity
{
    private PeripheralAdapter tableAdapter;
    private Button scanButton;
    private final ArrayList<CustomPeripheral> tableData = new ArrayList<>();
    private final HashMap<String, CustomPeripheral> nearbyPeripherals = new HashMap<>();
    private long lastUiRefreshTime;

    private UUBluetoothScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btle_scan);

        scanner = new UUBluetoothScanner(getApplicationContext());

        scanButton = (Button)findViewById(R.id.scanButton);
        scanButton.setEnabled(false);

        ListView listView = (ListView)findViewById(R.id.list_view);
        tableAdapter = new PeripheralAdapter(this, tableData);
        listView.setAdapter(tableAdapter);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        UUPermissions.requestPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, 57, new UUPermissions.UUPermissionDelegate()
        {
            @Override
            public void onPermissionRequestComplete(String permission, boolean granted)
            {
                scanButton.setEnabled(granted);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        UUPermissions.handleRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void onScanClicked(View v)
    {
        if (scanner.isScanning())
        {
            scanner.stopScanning();
            scanButton.setText(R.string.scan);
        }
        else
        {
            startScanning();
            scanButton.setText(R.string.stop);
        }
    }

    private void startScanning()
    {
        ArrayList<UUPeripheralFilter> filters = new ArrayList<>();

        UUID[] uuidList = new UUID[]
        {
        };

        UUPeripheralFactory factory = new CustomPeripheralFactory();

        scanner.startScanning(factory, uuidList, filters, new UUBluetoothScanner.Listener()
        {
            @Override
            public void onPeripheralFound(@NonNull UUBluetoothScanner scanner, @NonNull UUPeripheral peripheral)
            {
                handlePeripheralFound(peripheral);
            }
        });
    }

    private <T extends UUPeripheral> void handlePeripheralFound(final @NonNull T peripheral)
    {
        UULog.debug(getClass(), "onPeripheralFound", "peripheralFound, class: " + peripheral.getClass());
        CustomPeripheral cp = (CustomPeripheral)peripheral;

        nearbyPeripherals.put(cp.getAddress(), cp);

        tableData.clear();
        tableData.addAll(nearbyPeripherals.values());

        Collections.sort(tableData, new Comparator<CustomPeripheral>()
        {
            @Override
            public int compare(CustomPeripheral o1, CustomPeripheral o2)
            {
                return UUInteger.compare(o2.getRssi(), o1.getRssi());
            }
        });

        final long timeSinceLastUpdate = System.currentTimeMillis() - lastUiRefreshTime;
        if (timeSinceLastUpdate > 1000)
        {
            UUThread.runOnMainThread(new Runnable()
            {
                @Override
                public void run()
                {
                    lastUiRefreshTime = System.currentTimeMillis();
                    tableAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    class RssiFilter implements UUPeripheralFilter
    {
        private int rssiLevel;

        RssiFilter(int rssiLevel)
        {
            this.rssiLevel = rssiLevel;
        }

        @Override
        public boolean shouldDiscoverPeripheral(@NonNull UUPeripheral peripheral)
        {
            return (peripheral.getRssi() > rssiLevel);
        }
    }

    class NameFilter implements UUPeripheralFilter
    {
        private String name;

        NameFilter(String name)
        {
            this.name = name;
        }

        @Override
        public boolean shouldDiscoverPeripheral(@NonNull UUPeripheral peripheral)
        {
            return UUString.areEqual(name, peripheral.getName());
        }
    }



    class CustomPeripheralFactory implements UUPeripheralFactory<CustomPeripheral>
    {
        @NonNull
        @Override
        public CustomPeripheral fromScanResult(@NonNull BluetoothDevice device, int rssi, @NonNull byte[] scanRecord)
        {
            return new CustomPeripheral(device, rssi, scanRecord);
        }


    }


    private void onRowClicked(final CustomPeripheral peripheral)
    {
        scanner.stopScanning();

        Intent intent = new Intent(this, PeripheralDetailActivity.class);
        intent.putExtra("peripheral", peripheral);
        startActivity(intent);

    }

    class PeripheralAdapter extends ArrayAdapter<CustomPeripheral>
    {
        public PeripheralAdapter(Context context, ArrayList<CustomPeripheral> data)
        {
            super(context, R.layout.peripheral_list_row, data);
        }

        @Override
        public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;
            if (view == null)
            {
                view = inflater.inflate(R.layout.peripheral_list_row, parent, false);
            }

            TextView nameLabel = (TextView)view.findViewById(R.id.name_label);
            TextView macLabel = (TextView)view.findViewById(R.id.mac_label);
            TextView rssiLabel = (TextView)view.findViewById(R.id.rssi_label);
            TextView timeLabel = (TextView)view.findViewById(R.id.time_label);
            TextView connectionLabel = (TextView)view.findViewById(R.id.connection_state_label);

            final CustomPeripheral rowData = getItem(position);

            nameLabel.setText(rowData.getName());
            macLabel.setText(rowData.getAddress());
            rssiLabel.setText(String.format(Locale.US, "%d", rowData.getRssi()));
            timeLabel.setText(String.format(Locale.US, "%.3f", rowData.getTimeSinceLastUpdate() / 1000.0f));
            connectionLabel.setText(rowData.getConnectionState(getApplicationContext()).toString());

            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onRowClicked(rowData);
                }
            });

            return view;
        }
    }
}
