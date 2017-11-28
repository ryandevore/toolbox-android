package uu.toolbox.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import uu.toolbox.core.UUFile;
import uu.toolbox.core.UUJson;
import uu.toolbox.logging.UULog;

public class UUDataCache implements UUDataCacheProtocol
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final long DEFAULT_CONTENT_EXPIRATION_SECONDS = (long)(60L * 60L * 24L * 30L * 1000L); // 30 days

    public static class MetaData
    {
        public static final String Timestamp = "Timestamp";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Singleton Interface
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static UUDataCache theSharedInstance;

    public static void init(final Context context)
    {
        theSharedInstance = new UUDataCache(context, defaultCacheFolder(context), DEFAULT_CONTENT_EXPIRATION_SECONDS);
    }

    public static synchronized UUDataCache sharedInstance()
    {
        return theSharedInstance;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance Variables
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private long contentExpirationLength = DEFAULT_CONTENT_EXPIRATION_SECONDS;
    private File cacheFolder = null;
    private UUDataCacheDb metaDataDb;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance Variables
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public UUDataCache(@NonNull final Context context,
                       @NonNull final File cacheLocation,
                       final long contentExpiration)
    {
        cacheFolder = cacheLocation;
        contentExpirationLength = contentExpiration;
        UUFile.createFoldersIfNeeded(cacheFolder);
        metaDataDb = new UUDataCacheDb(context);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDataCacheProtocol interface
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public byte[] getData(@NonNull String key)
    {
        removeIfExpired(key);

        byte[] cached = loadFromCache(key);
        if (cached != null)
        {
            return cached;
        }

        return loadFromDisk(key);
    }

    @Override
    public void setData(@NonNull byte[] data, @NonNull String key)
    {
        saveToDisk(data, key);
        saveToCache(data, key);

        HashMap<String, Object> md = getMetaData(key);
        md.put(MetaData.Timestamp, System.currentTimeMillis());
        setMetaData(md, key);
    }

    @NonNull
    @Override
    public HashMap<String, Object> getMetaData(@NonNull String key)
    {
        return metaDataDb.getMetaData(key);
    }

    @Override
    public void setMetaData(@NonNull HashMap<String, Object> metaData, @NonNull String key)
    {
        metaDataDb.setMetaData(key, metaData);
    }

    @Override
    public boolean doesDataExist(@NonNull String key)
    {
        try
        {
            File file = diskCacheURL(key);
            return file.exists();
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "doesDataExist", ex);
            return false;
        }
    }

    @Override
    public boolean isDataExpired(@NonNull String key)
    {
        HashMap<String, Object> md = getMetaData(key);
        Object timestamp = md.get(MetaData.Timestamp);
        if (timestamp != null && timestamp instanceof Long)
        {
            long actualTimestamp = (Long)timestamp;
            long elapsed = System.currentTimeMillis() - actualTimestamp;
            return (elapsed > contentExpirationLength);
        }

        return false;
    }

    @Override
    public void removeData(@NonNull String key)
    {
        removeAllMetaData(key);
        removeFileFromCache(key);
        removeFileFromDisk(key);
    }

    @Override
    public void clearCache()
    {
        UUFile.deleteFile(cacheFolder);
        UUFile.createFoldersIfNeeded(cacheFolder);

        metaDataDb.clearAllMetaData();
    }

    @Override
    public void purgeExpiredData()
    {
        ArrayList<String> keys = listKeys();

        for (String key : keys)
        {
            removeIfExpired(key);
        }
    }

    @Override
    public long getDataExpirationInterval()
    {
        return contentExpirationLength;
    }

    @Override
    public void setDataExpirationInterval(long interval)
    {
        contentExpirationLength = interval;
    }

    @NonNull
    @Override
    public ArrayList<String> listKeys()
    {
        ArrayList<String> results = new ArrayList<>();

        try
        {
            if (cacheFolder != null)
            {
                File[] list = cacheFolder.listFiles();
                for (File f : list)
                {
                    results.add(f.getName());
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "listKeys", ex);
            results = new ArrayList<>();
        }

        return results;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Implementation
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static @NonNull File defaultCacheFolder(@NonNull final Context context)
    {
        return new File(context.getCacheDir(), "UUDataCache");
    }

    @NonNull
    private String sanitizeKey(@NonNull final String key)
    {
        return key.replaceAll("[^A-Za-z0-9]", "-");
    }

    @NonNull
    private File diskCacheURL(@NonNull final String key)
    {
        return new File(cacheFolder, sanitizeKey(key));
    }

    private void removeIfExpired(@NonNull final String key)
    {
        if (isDataExpired(key))
        {
            removeData(key);
        }
    }

    @Nullable
    private byte[] loadFromDisk(@NonNull final String key)
    {
        byte[] data = null;

        try
        {
            File pathUrl = diskCacheURL(key);
            data = UUFile.readFile(pathUrl);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "loadFromDisk", ex);
        }

        return data;
    }

    @Nullable
    private byte[] loadFromCache(@NonNull final String key)
    {
        //return dataCache.object(forKey: key as NSString) as Data?

        // TODO: Implement in memory cache
        return null;
    }

    private void removeFileFromDisk(@NonNull String key)
    {
        try
        {
            File pathUrl = diskCacheURL(key);
            UUFile.deleteFile(pathUrl);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "removeFileFromDisk", ex);
        }
    }

    private void removeFileFromCache(@NonNull String key)
    {
        try
        {
            // TODO: Implement remove from cache
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "removeFileFromCache", ex);
        }
    }

    private void removeAllMetaData(@NonNull String key)
    {
        try
        {
            //public static void destroy(final Context context, final Class<? extends UUDatabase> dbClass)
            // TODO: Implement remove all meta data
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "removeAllMetaData", ex);
        }
    }

    private void saveToDisk(@NonNull byte[] data, @NonNull String key)
    {
        try
        {
            File pathUrl = diskCacheURL(key);
            UUFile.writeFile(pathUrl, data);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "saveToDisk", ex);
        }
    }

    private void saveToCache(@NonNull byte[] data, @NonNull String key)
    {
        //dataCache.setObject(data as NSData, forKey: key as NSString)

        // TODO: Implement memory cache
    }

    private void clearDiskCache()
    {
        try
        {
            UUFile.deleteFile(cacheFolder);
            UUFile.createFoldersIfNeeded(cacheFolder);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "clearDiskCache", ex);
        }
    }

    private void clearMemoryCache()
    {
        try
        {
            UUFile.deleteFile(cacheFolder);
            UUFile.createFoldersIfNeeded(cacheFolder);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "clearDiskCache", ex);
        }
    }


    public static class UUDataCacheMetaData implements UUDataModel
    {
        private static final String TABLE_NAME = "uu_data_cache_meta_data";

        private static final String NAME_COLUMN = "name";
        private static final String TIMESTAMP_COLUMN = "timestamp";
        private static final String META_DATA_COLUMN = "meta_data";

        private String name;
        private long timestamp;
        private String metaData;

        @NonNull
        @Override
        public String getTableName()
        {
            return TABLE_NAME;
        }

        @NonNull
        @Override
        public HashMap<String, String> getColumnMap()
        {
            HashMap<String, String> map = new HashMap<>();

            map.put(NAME_COLUMN, UUSql.TEXT_COLUMN_TYPE);
            map.put(TIMESTAMP_COLUMN, UUSql.INT64_COLUMN_TYPE);
            map.put(META_DATA_COLUMN, UUSql.TEXT_COLUMN_TYPE);

            return map;
        }

        @Nullable
        @Override
        public String getPrimaryKeyColumnName()
        {
            return NAME_COLUMN;
        }

        @NonNull
        @Override
        public String getPrimaryKeyWhereClause()
        {
            return String.format(Locale.US, "%s = ?", NAME_COLUMN);
        }

        @NonNull
        @Override
        public String[] getPrimaryKeyWhereArgs()
        {
            return new String[] { name };
        }

        @NonNull
        @Override
        public ContentValues getContentValues()
        {
            ContentValues cv = new ContentValues();

            UUContentValues.putIfNotNull(cv, NAME_COLUMN, name);
            UUContentValues.putIfNotNull(cv, TIMESTAMP_COLUMN, timestamp);
            UUContentValues.putIfNotNull(cv, META_DATA_COLUMN, metaData);

            return cv;
        }

        @Override
        public void fillFromCursor(@NonNull Cursor cursor)
        {
            name = UUCursor.safeGetString(cursor, NAME_COLUMN);
            timestamp = UUCursor.safeGetLong(cursor, TIMESTAMP_COLUMN);
            metaData = UUCursor.safeGetString(cursor, META_DATA_COLUMN);
        }

        @NonNull
        private HashMap<String, Object> explodeMetaData()
        {
            HashMap<String, Object> m = new HashMap<>();

            try
            {
                JSONObject json = new JSONObject(metaData);

                Iterator<String> keys = json.keys();
                while (keys.hasNext())
                {
                    String key = keys.next();
                    Object val = UUJson.safeGet(json, key);
                    m.put(key, val);
                };
            }
            catch (Exception ex)
            {
                UULog.error(getClass(), "explodeMetaData", ex);
                m = new HashMap<>();
            }

            return m;
        }

        private void setMetaDataFromHashMap(@NonNull final HashMap<String, Object> dictionary)
        {
            try
            {
                JSONObject obj = UUJson.toJson(dictionary);
                if (obj != null)
                {
                    metaData = obj.toString();
                }
            }
            catch (Exception ex)
            {
                UULog.error(getClass(), "setMetaDataFromHashMap", ex);
            }
        }
    }

    private static class UUDataCacheDb extends UUDatabase
    {
        private static final String DB_NAME = "UUDataCacheDb";
        private static final int DB_VERSION_ONE = 1;

        private static final int DB_VERSION = DB_VERSION_ONE;

        @NonNull
        private HashMap<String, Object> getMetaData(@NonNull final String key)
        {
            String where = String.format(Locale.US, "%s = ?", UUDataCacheMetaData.NAME_COLUMN);
            String[] whereArgs = new String[] { key };

            HashMap<String, Object> result = new HashMap<>();

            UUDataCacheMetaData row = querySingleObject(UUDataCacheMetaData.class, where, whereArgs, null);
            if (row != null)
            {
                result = row.explodeMetaData();
            }

            return result;
        }

        private void setMetaData(@NonNull final String key, @NonNull HashMap<String, Object> metaData)
        {
            UUDataCacheMetaData md = new UUDataCacheMetaData();
            md.name = key;
            md.timestamp = System.currentTimeMillis();
            md.setMetaDataFromHashMap(metaData);

            updateObject(UUDataCacheMetaData.class, md);
        }

        private void clearAllMetaData()
        {
            for (UUDataModel dm : getDataModels(getVersion()))
            {
                truncateTable(dm.getTableName());
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // UUDatabaseDefinition
        ////////////////////////////////////////////////////////////////////////////////////////////////

        private UUDataCacheDb(@NonNull final Context context)
        {
            super(context);
        }

        @SuppressWarnings("unused")
        public String getDatabaseName()
        {
            return DB_NAME;
        }

        public int getVersion()
        {
            return DB_VERSION;
        }

        public ArrayList<UUDataModel> getDataModels(int version)
        {
            switch (version)
            {
                case DB_VERSION_ONE:
                {
                    return getV1DataModels();
                }

                default:
                {
                    return null;
                }
            }
        }

        @Override
        public ArrayList<String> getSqlCreateLines(int version)
        {
            return null;
        }

        public void handlePostOpen(SQLiteDatabase db)
        {

        }

        public void handlePostCreate(SQLiteDatabase db)
        {

        }

        public void handleUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

        }

        private ArrayList<UUDataModel> getV1DataModels()
        {
            ArrayList<UUDataModel> list = new ArrayList<>();
            list.add(new UUDataCacheMetaData());
            return list;
        }
    }
}