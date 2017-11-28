package uu.toolbox.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Size;

import java.io.File;
import java.util.HashMap;

import uu.toolbox.data.UUDataCache;

public class UURemoteImage
{
    public static class MetaData extends UURemoteData.MetaData
    {
        public static final String ImageWidth = "ImageWidth";
        public static final String ImageHeight = "ImageHeight";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Singleton Interface
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static UURemoteImage theSharedInstance;

    public static void init(final Context context)
    {
        theSharedInstance = new UURemoteImage(context);
    }

    public static synchronized UURemoteImage sharedInstance()
    {
        return theSharedInstance;
    }

    public UURemoteImage(@NonNull final Context context)
    {
    }

    @Nullable
    public Bitmap getImage(@NonNull final String key, final boolean skipDownload)
    {
        if (UUDataCache.sharedInstance().doesDataExist(key))
        {
            File file = UUDataCache.sharedInstance().getDiskFileName(key);

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), opt);

            HashMap<String, Object> md = getMetaData(key);
            md.put(MetaData.ImageWidth, opt.outWidth);
            md.put(MetaData.ImageHeight, opt.outHeight);
            setMetaData(md, key);

            opt.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        else if (!skipDownload)
        {
            UURemoteData.sharedInstance().getData(key);
        }

        return null;
    }

    public boolean isDownloadPending(@NonNull final String key)
    {
        return UURemoteData.sharedInstance().isDownloadPending(key);
    }

    public HashMap<String, Object> getMetaData(@NonNull final String key)
    {
        return UURemoteData.sharedInstance().getMetaData(key);
    }

    public void setMetaData(@NonNull HashMap<String, Object> metaData, @NonNull final String key)
    {
        UURemoteData.sharedInstance().setMetaData(metaData, key);
    }

    @Nullable
    public Size imageSize(@NonNull final String key)
    {
        Size size = null;

        HashMap<String, Object> md = getMetaData(key);

        if (md.containsKey(MetaData.ImageWidth) &&
            md.containsKey(MetaData.ImageHeight))
        {
            Object wo = md.get(MetaData.ImageWidth);
            Object ho = md.get(MetaData.ImageHeight);
            if (wo instanceof Integer && ho instanceof Integer)
            {
                size = new Size((Integer)wo, (Integer)ho);
            }
        }

        return size;
    }
}