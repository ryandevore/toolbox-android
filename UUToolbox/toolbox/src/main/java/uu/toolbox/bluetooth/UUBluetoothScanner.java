package uu.toolbox.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import uu.toolbox.core.UUWorkerThread;
import uu.toolbox.logging.UULog;

@SuppressWarnings("unused")
public class UUBluetoothScanner
{
    public interface Listener
    {
        void onPeripheralFound(final @NonNull UUBluetoothScanner scanner, final @NonNull UUPeripheral peripheral);
    }

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback legacyScanCallback;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private UUWorkerThread scanThread;
    private boolean isScanning = false;
    private ArrayList<UUPeripheralFilter> scanFilters;
    private UUPeripheralFactory peripheralFactory;

    public UUBluetoothScanner(final Context context)
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanThread = new UUWorkerThread();
    }

    public <T extends UUPeripheral> void startScanning(
            final @Nullable UUPeripheralFactory factory,
            final @Nullable UUID[] serviceUuidList,
            final @Nullable ArrayList<UUPeripheralFilter> filters,
            final @NonNull Listener delegate)
    {
        if (isMainThread())
        {
            scanFilters = filters;
            peripheralFactory = factory;

            if (peripheralFactory == null)
            {
                peripheralFactory = new DefaultPeripheralFactory();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                startScan(serviceUuidList, delegate);
            }
            else
            {
                startLegacyScan(serviceUuidList, delegate);
            }
        }
        else
        {
            runOnMainThread(new Runnable()
            {
                @Override
                public void run()
                {
                    startScanning(factory, serviceUuidList, filters, delegate);
                }
            });
        }
    }

    public boolean isScanning()
    {
        return isScanning;
    }

    public void stopScanning()
    {
        if (isMainThread())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                stopScan();
            }
            else
            {
                stopLegacyScan();
            }
        }
        else
        {
            runOnMainThread(new Runnable()
            {
                @Override
                public void run()
                {
                    stopScanning();
                }
            });
        }
    }

    @SuppressWarnings("deprecation")
    private void startLegacyScan(final UUID[] serviceUuidList, final Listener delegate)
    {
        try
        {
            isScanning = true;

            legacyScanCallback = new BluetoothAdapter.LeScanCallback()
            {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
                {
                    handleLegacyScanResult(device, rssi, scanRecord, delegate);
                }
            };

            bluetoothAdapter.startLeScan(serviceUuidList, legacyScanCallback);
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "startLegacyScan", ex);
        }
    }

    @TargetApi(21)
    private void startScan(final UUID[] serviceUuidList, final Listener delegate)
    {
        try
        {
            ArrayList<ScanFilter> filters = new ArrayList<>();

            for (UUID uuid : serviceUuidList)
            {
                ScanFilter.Builder fb = new ScanFilter.Builder();
                fb.setServiceUuid(new ParcelUuid(uuid));
                filters.add(fb.build());
            }

            ScanSettings.Builder builder = new ScanSettings.Builder();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
                builder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
                builder.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
            }

            builder.setReportDelay(0);
            builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

            ScanSettings settings = builder.build();

            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            isScanning = true;

            scanCallback = new ScanCallback()
            {
                @Override
                public void onScanResult(int callbackType, ScanResult result)
                {
                    UULog.debug(getClass(), "startScan.onScanResult", "callbackType: " + callbackType + ", result: " + result.toString());
                    handleScanResult(result, delegate);
                }

                /**
                 * Callback when batch results are delivered.
                 *
                 * @param results List of scan results that are previously scanned.
                 */
                public void onBatchScanResults(List<ScanResult> results)
                {
                    UULog.debug(getClass(), "startScan.onBatchScanResults", "There are " + results.size() + " batched results");

                    for (ScanResult sr : results)
                    {
                        UULog.debug(getClass(), "startScan.onBatchScanResults", results.toString());
                        handleScanResult(sr, delegate);
                    }
                }

                /**
                 * Callback when scan could not be started.
                 *
                 * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
                 */
                public void onScanFailed(int errorCode)
                {
                    UULog.debug(getClass(), "startScan.onScanFailed", "errorCode: " + errorCode);
                }
            };

            bluetoothLeScanner.startScan(filters, settings, scanCallback);
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "startScan", ex);
        }
    }

    private void handleLegacyScanResult(final BluetoothDevice device, final int rssi, final byte[] scanRecord, final Listener delegate)
    {
        scanThread.post(new Runnable()
        {
            @Override
            public void run()
            {
                UUPeripheral peripheral = peripheralFactory.fromScanResult(device, rssi, scanRecord);
                if (shouldDiscoverPeripheral(peripheral))
                {
                    handlePeripheralFound(peripheral, delegate);
                }
            }
        });
    }

    @TargetApi(21)
    private void handleScanResult(final ScanResult scanResult, final Listener delegate)
    {
        scanThread.post(new Runnable()
        {
            @Override
            public void run()
            {
                UUPeripheral peripheral = peripheralFactory.fromScanResult(scanResult.getDevice(), scanResult.getRssi(), safeGetScanRecord(scanResult));
                if (shouldDiscoverPeripheral(peripheral))
                {
                    handlePeripheralFound(peripheral, delegate);
                }
            }
        });
    }

    @TargetApi(21)
    private byte[] safeGetScanRecord(final ScanResult result)
    {
        if (result != null)
        {
            ScanRecord sr = result.getScanRecord();
            if (sr != null)
            {
                return sr.getBytes();
            }
        }

        return null;
    }

    private void handlePeripheralFound(final UUPeripheral peripheral, final Listener delegate)
    {
        UULog.debug(getClass(), "handlerPeripheralFound", "Peripheral Found: " + peripheral);
        notifyPeripheralFound(peripheral, delegate);
    }

    private void notifyPeripheralFound(final UUPeripheral peripheral, final Listener delegate)
    {
        try
        {
            if (delegate != null && peripheral != null)
            {
                delegate.onPeripheralFound(this, peripheral);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "notifyPeripheralFound", ex);
        }
    }

    @SuppressWarnings("deprecation")
    private void stopLegacyScan()
    {
        try
        {
            bluetoothAdapter.stopLeScan(legacyScanCallback);
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "stopLegacyScan", ex);
        }
        finally
        {
            isScanning = false;
            legacyScanCallback = null;
        }
    }

    @TargetApi(21)
    private void stopScan()
    {
        try
        {
            if (bluetoothLeScanner != null)
            {
                bluetoothLeScanner.stopScan(scanCallback);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "stopScan", ex);
        }
        finally
        {
            isScanning = false;
            scanCallback = null;
        }
    }

    private boolean shouldDiscoverPeripheral(final @NonNull UUPeripheral peripheral)
    {
        if (scanFilters != null)
        {
            for (UUPeripheralFilter filter : scanFilters)
            {
                if (!filter.shouldDiscoverPeripheral(peripheral))
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


    private boolean isMainThread()
    {
        return (Looper.myLooper() == Looper.getMainLooper());
    }

    private void runOnMainThread(final @Nullable Runnable r)
    {
        try
        {
            if (r != null)
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            r.run();
                        }
                        catch (Exception ex)
                        {
                            UULog.debug(getClass(), "runOnMainThread.run", ex);
                        }
                    }
                });
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "runOnMainThread", ex);
        }
    }

    private class DefaultPeripheralFactory implements UUPeripheralFactory<UUPeripheral>
    {
        @NonNull
        @Override
        public UUPeripheral fromScanResult(@NonNull BluetoothDevice device, int rssi, @NonNull byte[] scanRecord)
        {
            return new UUPeripheral(device, rssi, scanRecord);
        }
    }
}