package uu.toolbox.core;

import android.content.Context;
import android.util.Base64;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import uu.toolbox.logging.UULog;

/**
 * UUJson
 *
 * Useful Utilities - A set of extension methods for the JSONObject class.
 *
 */
public final class UUJson
{
    public static String safeGetString(final JSONObject json, final String key)
    {
        return safeGetString(json, key, null);
    }

    public static String safeGetString(final JSONObject json, final String key, final String defaultValue)
    {
        String  val = defaultValue;

        try
        {
            if (json != null && json.has(key) && !json.isNull(key))
            {
                val = json.getString(key);
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetString", ex);
            val = defaultValue;
        }

        return val;
    }

    public static long safeGetLong(final JSONObject json, final String key)
    {
        return safeGetLong(json, key, 0);
    }

    public static long safeGetLong(final JSONObject json, final String key, final long defaultValue)
    {
        long val = defaultValue;

        try
        {
            if (json != null && json.has(key) && !json.isNull(key))
            {
                val = json.getLong(key);
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetLong", ex);
            val = defaultValue;
        }

        return val;
    }

    public static int safeGetInt(final JSONObject json, final String key)
    {
        return safeGetInt(json, key, 0);
    }

    public static int safeGetInt(final JSONObject json, final String key, final int defaultValue)
    {
        int val = defaultValue;

        try
        {
            if (json != null && json.has(key) && !json.isNull(key))
            {
                val = json.getInt(key);
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetInt", ex);
            val = defaultValue;
        }

        return val;
    }

    public static boolean safeGetBool(final JSONObject json, final String key)
    {
        return safeGetBool(json, key, false);
    }

    public static boolean safeGetBool(final JSONObject json, final String key, final boolean defaultValue)
    {
        boolean val = defaultValue;

        try
        {
            if (json != null && json.has(key) && !json.isNull(key))
            {
                val = json.getBoolean(key);
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetBool", ex);
            val = defaultValue;
        }

        return val;
    }

    public static float safeGetFloat(final JSONObject json, final String key)
    {
        return safeGetFloat(json, key, 0.0f);
    }

    public static float safeGetFloat(final JSONObject json, final String key, final float defaultValue)
    {
        float val = defaultValue;

        try
        {
            if (json != null && json.has(key) && !json.isNull(key))
            {
                val = (float)json.getDouble(key);
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetFloat", ex);
            val = defaultValue;
        }

        return val;
    }

    public static double safeGetDouble(final JSONObject json, final String key)
    {
        return safeGetDouble(json, key, 0);
    }

    public static double safeGetDouble(final JSONObject json, final String key, final double defaultValue)
    {
        double val = defaultValue;

        try
        {
            if (json != null && json.has(key) && !json.isNull(key))
            {
                val = json.getDouble(key);
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetDouble", ex);
            val = defaultValue;
        }

        return val;
    }

    public static byte[] safeGetDataFromBase64String(final JSONObject json, final String key)
    {
        return safeGetDataFromBase64String(json, key, null);
    }

    public static byte[] safeGetDataFromBase64String(final JSONObject json, final String key, final byte[] defaultValue)
    {
        byte[] val = defaultValue;

        try
        {
            String base64String = safeGetString(json, key);
            if (base64String != null)
            {
                val = Base64.decode(base64String, Base64.NO_WRAP);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "safeGetDataFromBase64String", ex);
            val = defaultValue;
        }

        return val;
    }

    public static long safeGetDate(final JSONObject json, final String key, final String[] formatters)
    {
        return safeGetDate(json, key, 0, formatters);
    }

    public static long safeGetDate(final JSONObject json, final String key, final long defaultValue, final String[] formatters)
    {
        long result = defaultValue;
        String str = safeGetString(json, key, null);

        if (str != null)
        {
            Date dt = UUDate.parseDate(str, formatters);
            if (dt != null)
            {
                result = dt.getTime();
            }
        }

        return result;
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
            if (json != null && json.has(key) && !json.isNull(key))
            {
                val = json.get(key);
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGet", ex);
            val = defaultValue;
        }

        return val;
    }

    public static JSONObject safeGetJsonObject(final JSONObject json, final String key)
    {
        return safeGetObject(JSONObject.class, json, key);
    }

    public static JSONArray safeGetJsonArray(final JSONObject json, final String key)
    {
        return safeGetObject(JSONArray.class, json, key);
    }

    public static Object safeGetJsonObject(final JSONArray json, final int index)
    {
        Object obj = null;

        try
        {
            if (json != null && json.length() > index)
            {
                obj = json.get(index);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "safeGetJsonObject", ex);
            obj = null;
        }

        return obj;
    }

    public static <T extends Object> T safeGetObject(final Class<T> type, final JSONObject json, final String key)
    {
        return safeGetObject(type, json, key, null);
    }

    public static <T extends Object> T safeGetObject(final Class<T> type, final JSONObject json, final String key, final T defaultValue)
    {
        T val = defaultValue;

        try
        {
            Object obj = safeGet(json, key);
            if (obj != null && type != null && obj.getClass().isAssignableFrom(type))
            {
                val = (T) obj;
            }
        }
        catch (Exception ex)
        {
            val = defaultValue;
        }

        return val;
    }


    public static void safePut(final JSONObject json, final String key, final Object val)
    {
        try
        {
            if (json != null && key != null)
            {
                if (val != null)
                {
                    json.put(key, val);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "safePut", ex);
        }
    }

    public static JSONObject toJson(final Map<String, Object> map)
    {
        try
        {
            return new JSONObject(map);
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "toJson", ex);
            return null;
        }
    }

    public static JSONObject toJsonFromStringMap(final Map<String, String> map)
    {
        try
        {
            return new JSONObject(map);
        }
        catch (Exception ex)
        {
            //AppLog.error(UUJson.class, "toJson", ex);
            return null;
        }
    }

    public static JSONObject toJson(final String jsonString)
    {
        try
        {
            return new JSONObject(jsonString);
        }
        catch (Exception ex)
        {
            //AppLog.error(UUJson.class, "toJson", ex);
            return null;
        }
    }

    public static JSONArray toJsonArray(final String jsonString)
    {
        try
        {
            return new JSONArray(jsonString);
        }
        catch (Exception ex)
        {
            //AppLog.error(UUJson.class, "toJson", ex);
            return null;
        }
    }

    public static String toJsonString(final Map<String, Object> map)
    {
        String jsonStr = null;

        try
        {
            JSONObject obj = toJson(map);
            if (obj != null)
            {
                jsonStr = obj.toString();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "toJsonString", ex);
            jsonStr = null;
        }

        return jsonStr;
    }

    public static String toJsonStringFromStringMap(final Map<String, String> map)
    {
        String jsonStr = "";

        try
        {
            JSONObject obj = toJsonFromStringMap(map);
            if (obj != null)
            {
                jsonStr = obj.toString();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "toJsonString", ex);
            jsonStr = "";
        }

        return jsonStr;
    }

    public static ArrayList<JSONObject> flattenJsonArray(final JSONArray json)
    {
        ArrayList<JSONObject> list = new ArrayList<>();

        try
        {
            if (json != null)
            {
                int count = json.length();
                for (int i = 0; i < count; i++)
                {
                    Object obj = json.get(i);
                    if (obj != null && obj instanceof JSONObject)
                    {
                        list.add((JSONObject)obj);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            list = new ArrayList<>();
        }

        return list;
    }

    public static ArrayList<String> safeGetJsonStrings(final JSONArray json)
    {
        ArrayList<String> list = new ArrayList<>();

        try
        {
            if (json != null)
            {
                int count = json.length();
                for (int i = 0; i < count; i++)
                {
                    String obj = json.getString(i);
                    list.add(obj);
                }
            }
        }
        catch (Exception ex)
        {
            list = new ArrayList<>();
        }

        return list;
    }

    public static <T extends UUJsonConvertible> ArrayList<T> parseJson(final Context context, final Class<T> type, final ArrayList<JSONObject> list)
    {
        ArrayList<T> parsedResults = new ArrayList<>();

        try
        {
            if (type != null && list != null)
            {
                for (JSONObject json : list)
                {
                    T obj = type.newInstance();
                    obj.fillFromJson(context, json);
                    parsedResults.add(obj);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "parseJson", ex);
            parsedResults = null;
        }

        return parsedResults;
    }

    public static byte[] safeSerializeJson(final JSONObject jsonObj, final String contentEncoding)
    {
        try
        {
            String str = jsonObj.toString();
            return str.getBytes(contentEncoding);
        }
        catch (Exception ex)
        {
            UULog.debug(UUJson.class, "safeSerializeJson", ex);
            return null;
        }
    }

    public static byte[] safeSerializeJson(final JSONArray jsonArray, final String contentEncoding)
    {
        try
        {
            String str = jsonArray.toString();
            return str.getBytes(contentEncoding);
        }
        catch (Exception ex)
        {
            UULog.debug(UUJson.class, "safeSerializeJson", ex);
            return null;
        }
    }

    public static byte[] objectToJson(final Object obj, final String contentEncoding)
    {
        byte[] json = null;

        if (obj != null)
        {
            if (obj instanceof JSONObject)
            {
                json = safeSerializeJson((JSONObject)obj, contentEncoding);
            }
            else if (obj instanceof ArrayList)
            {
                JSONArray jsonArray = new JSONArray();

                ArrayList list = (ArrayList)obj;
                for (Object listNode : list)
                {
                    if (listNode instanceof UUJsonConvertible)
                    {
                        UUJsonConvertible jsonConvertible = (UUJsonConvertible)listNode;
                        JSONObject jsonObj = jsonConvertible.toJsonObject();
                        if (jsonObj != null)
                        {
                            jsonArray.put(jsonObj);
                        }
                    }
                }

                json = safeSerializeJson(jsonArray, contentEncoding);
            }
        }

        return json;
    }



    public static void closeJsonReader(final JsonReader reader)
    {
        try
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUJson.class, "closeJsonReader", ex);
        }
    }
}