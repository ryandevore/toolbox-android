package uu.toolbox.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

import uu.toolbox.core.UUData;
import uu.toolbox.core.UUString;

/**
 * Wrapper class around a BTLE scanning result.
 */
@SuppressWarnings("unused")
public class UUPeripheral
{
    private static final byte DATA_TYPE_INCOMPLETE_LIST_OF_16_BIT_SERVICE_CLASS_UUIDS = 0x02;
    private static final byte DATA_TYPE_COMPLETE_LIST_OF_128_BIT_SERVICE_CLASS_UUIDS = 0x07;
    private static final byte DATA_TYPE_COMPLETE_LOCAL_NAME = 0x09;
    private static final byte DATA_TYPE_MANUFACTURING_DATA = (byte)0xFF;

    private BluetoothDevice device;
    private byte[] scanRecord;
    private int rssi;
    private byte[] manufacturingData;
    private String localName;
    private final ArrayList<String> serviceUuids = new ArrayList<>();
    private long timestamp;

    public UUPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @NonNull byte[] scanRecord)
    {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        this.timestamp = System.currentTimeMillis();
        parseScanRecord();
    }

    public @NonNull byte[] getScanRecord()
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

    private void parseScanRecord()
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
}
