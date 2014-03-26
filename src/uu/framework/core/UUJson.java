package uu.framework.core;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

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
        try
        {
            return json.getString(key);
        }
        catch (JSONException ex)
        {
            Log.e(LOG_TAG, "Error getting JSON String", ex);
            return null;
        }
    }

    public static final long safeGetLong(final JSONObject json, final String key)
    {
        try
        {
            return json.getLong(key);
        }
        catch (JSONException ex)
        {
            Log.e(LOG_TAG, "Error getting JSON Long", ex);
            return 0;
        }
    }

    public static final int safeGetInt(final JSONObject json, final String key)
    {
        try
        {
            return json.getInt(key);
        }
        catch (JSONException ex)
        {
            Log.e(LOG_TAG, "Error getting JSON Int", ex);
            return 0;
        }
    }

    public static final boolean safeGetBool(final JSONObject json, final String key)
    {
        try
        {
            return json.getBoolean(key);
        }
        catch (JSONException ex)
        {
            Log.e(LOG_TAG, "Error getting JSON Bool", ex);
            return false;
        }
    }


    public static final JSONObject toJson(final HashMap<String, String> map)
    {
        try
        {
            JSONObject obj = new JSONObject();

            Set<String> keys = map.keySet();
            for (String key : keys)
            {
                String val = map.get(key);
                obj.put(key, val);
            }

            return obj;
        }
        catch (JSONException ex)
        {
            return null;
        }
    }
}
