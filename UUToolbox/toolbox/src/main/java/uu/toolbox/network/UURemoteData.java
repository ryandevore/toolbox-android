package uu.toolbox.network;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import uu.toolbox.core.UUString;
import uu.toolbox.data.UUDataCache;
import uu.toolbox.logging.UULog;

public class UURemoteData implements UURemoteDataProtocol
{
    public static class MetaData extends UUDataCache.MetaData
    {
        public static final String MimeType = "MimeType";
        public static final String DownloadTimestamp = "DownloadTimestamp";
    }

    public class Notifications
    {
        public static final String DataDownloaded = "UUDataDownloadedNotification";
        public static final String DataDownloadFailed = "UUDataDownloadFailedNotification";
    }

    public class NotificationKeys
    {
        public static final String RemotePath = "UUDataRemotePathKey";
        public static final String Error = "UURemoteDataErrorKey";
    }

    private HashMap<String, UUHttpTask> pendingDownloads = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Singleton Interface
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static UURemoteData theSharedInstance;

    public static void init(final Context context)
    {
        theSharedInstance = new UURemoteData(context);

        if (UUDataCache.sharedInstance() == null)
        {
            UUDataCache.init(context);
        }
    }

    public static synchronized UURemoteData sharedInstance()
    {
        return theSharedInstance;
    }

    private LocalBroadcastManager broadcastManager;

    public UURemoteData(@NonNull final Context context)
    {
        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Nullable
    @Override
    public byte[] getData(@NonNull final String key)
    {
        try
        {
            if (!checkUrl(key))
            {
                return null;
            }

            byte[] data = UUDataCache.sharedInstance().getData(key);
            if (data != null)
            {
                return data;
            }

            UUHttpTask pendingDownload = pendingDownloads.get(key);
            if (pendingDownload != null)
            {
                // An active UUHttpTask means a request is currently fetching the resource, so
                // no need to re-fetch
                return null;
            }

            UUHttpRequest request = UUHttpRequest.get(key, null);
            request.setProcessMimeTypes(false);

            UUHttpTask task = UUHttp.execute(request, new UUHttpDelegate()
            {
                @Override
                public void onCompleted(UUHttpResponse response)
                {
                    handleDownloadResponse(response, key);
                }
            });

            pendingDownloads.put(key, task);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "getData", ex);
            return null;
        }

        return null;
    }

    @Override
    public boolean isDownloadPending(@NonNull final String key)
    {
        return pendingDownloads.containsKey(key);
    }

    @NonNull
    @Override
    public HashMap<String, Object> getMetaData(@NonNull final String key)
    {
        return UUDataCache.sharedInstance().getMetaData(key);
    }

    @Override
    public void setMetaData(@NonNull HashMap<String, Object> metaData, @NonNull final String key)
    {
        UUDataCache.sharedInstance().setMetaData(metaData, key);
    }

    private boolean checkUrl(@NonNull final String key)
    {
        try
        {
            if (UUString.isEmpty(key))
            {
                // Fast fail if the key is empty
                return false;
            }

            // A bad URL with throw here
            new URL(key);
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "checkUrl", ex);
            return false;
        }

        return true;
    }

    private void handleDownloadResponse(@NonNull final UUHttpResponse response, @NonNull final String key)
    {
        try
        {
            Exception ex = response.getException();
            byte[] rawResponse = response.getRawResponse();

            if (ex == null && rawResponse != null)
            {
                UUDataCache.sharedInstance().setData(rawResponse, key);
                updateMetaDataFromResponse(response, key);

                Intent intent = new Intent(Notifications.DataDownloaded);
                intent.putExtra(NotificationKeys.RemotePath, key);

                broadcastManager.sendBroadcast(intent);
            }
            else
            {
                UULog.debug(getClass(),
                        "handleDownloadResponse",
                        String.format(Locale.US, "Remote download failed!\n\nPath: %s\nStatus Code: %d\nError: %s",
                                key, response.getHttpResponseCode(), UUString.safeToString(ex)));

                Intent intent = new Intent(Notifications.DataDownloadFailed);
                intent.putExtra(NotificationKeys.RemotePath, key);

                // TODO: Put error info into intent

                broadcastManager.sendBroadcast(intent);
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), " handleDownloadResponse", ex);
        }
    }

    private void updateMetaDataFromResponse(@NonNull final UUHttpResponse response, @NonNull final String key)
    {
        HashMap<String, Object> md = UUDataCache.sharedInstance().getMetaData(key);
        md.put(MetaData.MimeType, response.getContentType());
        md.put(MetaData.DownloadTimestamp, System.currentTimeMillis());
        UUDataCache.sharedInstance().setMetaData(md, key);
    }


    public void saveData(@NonNull final byte[] data, @NonNull final String key)
    {
        try
        {
            UUDataCache.sharedInstance().setData(data, key);

            HashMap<String, Object> md = UUDataCache.sharedInstance().getMetaData(key);
            md.put(MetaData.MimeType, "raw"); // Is this needed?
            md.put(MetaData.DownloadTimestamp, System.currentTimeMillis());
            UUDataCache.sharedInstance().setMetaData(md, key);

            Intent intent = new Intent(Notifications.DataDownloaded);
            intent.putExtra(NotificationKeys.RemotePath, key);

            broadcastManager.sendBroadcast(intent);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), " saveData", ex);
        }
    }
}
