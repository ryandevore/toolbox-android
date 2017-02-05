package uu.toolbox.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uu.toolbox.core.UUData;
import uu.toolbox.core.UUJson;
import uu.toolbox.core.UUJsonConvertible;
import uu.toolbox.core.UUParcel;
import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

import static uu.toolbox.bluetooth.UUBluetooth.gattForPeripheral;

/**
 * Wrapper class around a BTLE scanning result.
 */
@SuppressWarnings("unused")
public class UUPeripheral implements UUJsonConvertible, Parcelable
{
    private static final byte DATA_TYPE_INCOMPLETE_LIST_OF_16_BIT_SERVICE_CLASS_UUIDS = 0x02;
    private static final byte DATA_TYPE_COMPLETE_LIST_OF_128_BIT_SERVICE_CLASS_UUIDS = 0x07;
    private static final byte DATA_TYPE_COMPLETE_LOCAL_NAME = 0x09;
    private static final byte DATA_TYPE_MANUFACTURING_DATA = (byte) 0xFF;

    public enum ConnectionState
    {
        Connecting,
        Connected,
        Disconnecting,
        Disconnected;

        public static @NonNull ConnectionState fromString(final @Nullable String string)
        {
            for (ConnectionState s : ConnectionState.values())
            {
                if (s.toString().equalsIgnoreCase(string))
                {
                    return s;
                }
            }

            return Disconnected;
        }

        static @NonNull ConnectionState fromProfileConnectionState(final int state)
        {
            switch (state)
            {
                case BluetoothProfile.STATE_CONNECTED:
                    return Connected;

                case BluetoothProfile.STATE_CONNECTING:
                    return Connecting;

                case BluetoothProfile.STATE_DISCONNECTING:
                    return Disconnecting;

                case BluetoothProfile.STATE_DISCONNECTED:
                    return Disconnected;
            }

            return Disconnected;
        }
    }

    private BluetoothDevice device;
    private byte[] scanRecord;
    private int rssi;
    private byte[] manufacturingData;
    private String localName;
    private final ArrayList<String> serviceUuids = new ArrayList<>();
    private long timestamp;

    private BluetoothGatt bluetoothGatt;

    public UUPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @Nullable byte[] scanRecord)
    {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        this.timestamp = System.currentTimeMillis();
        parseScanRecord();
    }

    public @Nullable byte[] getScanRecord()
    {
        return scanRecord;
    }

    public @Nullable byte[] getManufacturingData()
    {
        return manufacturingData;
    }

    public @NonNull BluetoothDevice getBluetoothDevice()
    {
        return device;
    }

    public @Nullable String getAddress()
    {
        return device.getAddress();
    }

    public @Nullable String getName()
    {
        if (UUString.isNotEmpty(localName))
        {
            return localName;
        }
        else
        {
            return device.getName();
        }
    }

    public @NonNull String[] getServiceUuids()
    {
        String[] list = new String[serviceUuids.size()];
        serviceUuids.toArray(list);
        return list;
    }

    public int getRssi()
    {
        return rssi;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public @NonNull ConnectionState getConnectionState(final @NonNull Context context)
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        int state = bluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
        if (state == BluetoothProfile.STATE_CONNECTED)
        {
            UUBluetoothGatt gatt = gattForPeripheral(this);
            if (gatt == null)
            {
                UULog.debug(getClass(), "getConnectionState", getAddress() + ", Profile is connected but UUBluetoothGatt is null! This should not happen!");
            }
            else
            {
                boolean isGattConnected = gatt.isGattConnected();
                UULog.debug(getClass(), "getConnectionState", getAddress() + ", Profile is connected, UUBluetoothGatt is non null, underlying Gatt is " + (isGattConnected ? "connected" : "NOT connected"));
                if (!isGattConnected)
                {
                    state = BluetoothProfile.STATE_DISCONNECTED;
                }
            }
        }
        return ConnectionState.fromProfileConnectionState(state);
    }

    void setBluetoothGatt(final @Nullable BluetoothGatt gatt)
    {
        bluetoothGatt = gatt;
    }

    public void discoverServices(
            final long timeout,
            final @NonNull UUPeripheralDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.discoverServices(timeout, delegate);
        }
    }

    public @NonNull List<BluetoothGattService> discoveredServices()
    {
        acquireExistingGatt();

        List<BluetoothGattService> list = new ArrayList<>();

        if (bluetoothGatt != null)
        {
            list.addAll(bluetoothGatt.getServices());
        }

        return list;
    }

    public void setNotifyState(
        final @NonNull BluetoothGattCharacteristic characteristic,
        final boolean notifyState,
        final long timeout,
        final @Nullable UUCharacteristicDelegate notifyDelegate,
        final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.setNotifyState(characteristic, notifyState, timeout, notifyDelegate, delegate);
        }
    }

    public void readCharacteristic(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.readCharacteristic(characteristic, timeout, delegate);
        }
    }

    public void readDescriptor(
            final @NonNull BluetoothGattDescriptor descriptor,
            final long timeout,
            final @NonNull UUDescriptorDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.readDescriptor(descriptor, timeout, delegate);
        }
    }

    public void writeCharacteristic(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.writeCharacteristic(characteristic, data, timeout, delegate);
        }
    }

    public void writeCharacteristicWithoutResponse(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.writeCharacteristicWithoutResponse(characteristic, data, timeout, delegate);
        }
    }

    public long getTimeSinceLastUpdate()
    {
        return System.currentTimeMillis() - timestamp;
    }

    private void parseScanRecord()
    {
        if (scanRecord != null)
        {
            int index = 0;

            while (index < scanRecord.length)
            {
                byte length = scanRecord[index];
                if (length == 0)
                    break;

                byte dataType = scanRecord[index + 1];

                byte[] data = new byte[length - 1];
                System.arraycopy(scanRecord, index + 2, data, 0, data.length);

                switch (dataType)
                {
                    case DATA_TYPE_MANUFACTURING_DATA:
                    {
                        manufacturingData = data;

                        if (UUData.isNotEmpty(manufacturingData))
                        {
                            parseManufacturingData(manufacturingData);
                        }

                        break;
                    }

                    case DATA_TYPE_COMPLETE_LOCAL_NAME:
                    {
                        localName = UUString.byteToUtf8String(data);
                        break;
                    }

                    case DATA_TYPE_INCOMPLETE_LIST_OF_16_BIT_SERVICE_CLASS_UUIDS:
                    {
                        parseServiceUuid(data, 2);
                        break;
                    }

                    case DATA_TYPE_COMPLETE_LIST_OF_128_BIT_SERVICE_CLASS_UUIDS:
                    {
                        parseServiceUuid(data, 16);
                        break;
                    }
                }

                index += (1 + length);
            }
        }
    }

    private void parseServiceUuid(final byte[] data, final int length)
    {
        int index = 0;

        while (index < data.length)
        {
            String hexChunk = UUString.byteToHex(data, index, length);
            if (UUString.isNotEmpty(hexChunk))
            {
                serviceUuids.add(hexChunk);
            }

            index += length;
        }
    }

    protected void parseManufacturingData(final @NonNull byte[] manufacturingData)
    {
        // Default does nothing
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // System.Object overrides
    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public String toString()
    {
        try
        {
            return String.format(Locale.getDefault(),
                    "%s, %s, %d, %s", getAddress(), getName(), this.rssi, UUString.byteToHex(manufacturingData));
        }
        catch (Exception ex)
        {
            return super.toString();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // JSON
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String JSON_DEVICE_KEY = "device";
    private static final String JSON_RSSI_KEY = "rssi";
    private static final String JSON_SCAN_RECORD_KEY = "scanRecord";
    private static final String JSON_TIMESTAMP_KEY = "timestamp";

    @Override
    public JSONObject toJsonObject()
    {
        JSONObject o = new JSONObject();

        UUJson.safePut(o, JSON_DEVICE_KEY, UUString.byteToHex(UUParcel.serializeParcel(device)));
        UUJson.safePut(o, JSON_SCAN_RECORD_KEY, UUString.byteToHex(scanRecord));
        UUJson.safePut(o, JSON_RSSI_KEY, rssi);
        UUJson.safePut(o, JSON_TIMESTAMP_KEY, timestamp);

        return o;
    }

    @Override
    public void fillFromJson(final Context context, final JSONObject json)
    {
        device = UUParcel.deserializeParcelable(BluetoothDevice.CREATOR, UUString.hexToByte(UUJson.safeGetString(json, JSON_DEVICE_KEY)));
        scanRecord = UUString.hexToByte(UUJson.safeGetString(json, JSON_SCAN_RECORD_KEY));
        rssi = UUJson.safeGetInt(json, JSON_RSSI_KEY);
        timestamp = UUJson.safeGetLong(json, JSON_TIMESTAMP_KEY);

        // Fill in derived data from scan record
        parseScanRecord();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Parcelable
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        try
        {
            dest.writeString(toJsonObject().toString());
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "writeToParcel", ex);
        }
    }

    public static final Parcelable.Creator<UUPeripheral> CREATOR
            = new Parcelable.Creator<UUPeripheral>()
    {
        public UUPeripheral createFromParcel(Parcel in)
        {
            return new UUPeripheral(in);
        }

        public UUPeripheral[] newArray(int size)
        {
            return new UUPeripheral[size];
        }
    };

    protected UUPeripheral(final Parcel in)
    {
        String jsonStr = in.readString();
        JSONObject json = UUJson.toJsonObject(jsonStr);
        fillFromJson(null, json);
    }

    private void acquireExistingGatt()
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            setBluetoothGatt(gatt.getBluetoothGatt());
        }
    }
}