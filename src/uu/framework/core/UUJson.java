package uu.framework.core;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * UUContext
 *
 * Useful Utilities - A set of extension methods for the JSONObject class.
 *
 */
public final class UUJson
{
    private static final String LOG_TAG = UUJson.class.getName();

    public static final String safeGetString(final JSONObject json, final String key)
    {
        return safeGetString(json, key, null);
    }

    public static final String safeGetString(final JSONObject json, final String key, final String defaultValue)
    {
        String  val = defaultValue;

        try
        {
            if (json.has(key) && !json.isNull(key))
            {
                val = json.getString(key);
            }
        }
        catch (JSONException ex)
        {
            Log.v(LOG_TAG, "Error getting JSON String", ex);
            val = defaultValue;
        }

        return val;
    }

    public static final long safeGetLong(final JSONObject json, final String key)
    {
        return safeGetLong(json, key, 0);
    }

    public static final long safeGetLong(final JSONObject json, final String key, final long defaultValue)
    {
        long val = defaultValue;

        try
        {
            if (json.has(key) && !json.isNull(key))
            {
                val = json.getLong(key);
            }
        }
        catch (JSONException ex)
        {
            Log.v(LOG_TAG, "Error getting JSON Long", ex);
            val = defaultValue;
        }

        return val;
    }

    public static final int safeGetInt(final JSONObject json, final String key)
    {
        return safeGetInt(json, key, 0);
    }

    public static final int safeGetInt(final JSONObject json, final String key, final int defaultValue)
    {
        int val = defaultValue;

        try
        {
            if (json.has(key) && !json.isNull(key))
            {
                val = json.getInt(key);
            }
        }
        catch (JSONException ex)
        {
            Log.v(LOG_TAG, "Error getting JSON Int", ex);
            val = defaultValue;
        }

        return val;
    }

    public static final boolean safeGetBool(final JSONObject json, final String key)
    {
        return safeGetBool(json, key, false);
    }

    public static final boolean safeGetBool(final JSONObject json, final String key, final boolean defaultValue)
    {
        boolean val = defaultValue;

        try
        {
            if (json.has(key) && !json.isNull(key))
            {
                val = json.getBoolean(key);
            }
        }
        catch (JSONException ex)
        {
            Log.v(LOG_TAG, "Error getting JSON Boolean", ex);
            val = defaultValue;
        }

        return val;
    }

    public static final double safeGetDouble(final JSONObject json, final String key)
    {
        return safeGetDouble(json, key, 0);
    }

    public static final double safeGetDouble(final JSONObject json, final String key, final double defaultValue)
    {
        double val = defaultValue;

        try
        {
            if (json.has(key) && !json.isNull(key))
            {
                val = json.getDouble(key);
            }
        }
        catch (JSONException ex)
        {
            Log.v(LOG_TAG, "Error getting JSON Double", ex);
            val = defaultValue;
        }

        return val;
    }

    public static Object safeGet(final JSONObject json, final String key)
    {
        return safeGet(json, key, null);
    }

    public static Object safeGet(final JSONObject json, final String key, final Object defaultValue)
    {
        Object val = defaultValue;

        try
        {
            if (json.has(key) && !json.isNull(key))
            {
                val = json.get(key);
            }
        }
        catch (JSONException ex)
        {
            Log.v(LOG_TAG, "Error getting JSON Field", ex);
            val = defaultValue;
        }

        return val;
    }


    public static final JSONObject toJson(final Map<String, Object> map)
    {
        try
        {
            JSONObject obj = new JSONObject(map);
            return obj;
        }
        catch (Exception ex)
        {
        	Log.v(LOG_TAG, "Error Creating JSON object", ex);
            return null;
        }
    }
}
