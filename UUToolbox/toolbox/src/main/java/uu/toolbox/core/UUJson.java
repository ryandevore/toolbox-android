package uu.toolbox.core;

import android.util.Base64;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import uu.toolbox.logging.UULog;

/**
 * UUJson
 *
 * Useful Utilities - A set of extension methods for the JSONObject class.
 *
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class UUJson
{
    public static String safeGetString(final JSONObject json, final Object key)
    {
        return safeGetString(json, key, null);
    }

    public static boolean hasNonNullValueForKey(final JSONObject json, final Object key)
    {
        return (json != null && key != null && json.has(key.toString()) && !json.isNull(key.toString()));
    }

    public static String safeGetString(final JSONObject json, final Object key, final String defaultValue)
    {
        String  val = defaultValue;

        try
        {
            if (hasNonNullValueForKey(json, key))
            {
                val = json.getString(key.toString());
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetString", "key: " + key, ex);
            val = defaultValue;
        }

        return val;
    }

    public static long safeGetLong(final JSONObject json, final Object key)
    {
        return safeGetLong(json, key, 0);
    }

    //noinspection ConstantConditions
    public static long safeGetLong(final JSONObject json, final Object key, final long defaultValue)
    {
        long val = defaultValue;

        try
        {
            if (hasNonNullValueForKey(json, key))
            {
                val = json.getLong(key.toString());
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetLong", "key: " + key, ex);
            val = defaultValue;
        }

        return val;
    }

    public static int safeGetInt(final JSONObject json, final Object key)
    {
        return safeGetInt(json, key, 0);
    }

    public static int safeGetInt(final JSONObject json, final Object key, final int defaultValue)
    {
        int val = defaultValue;

        try
        {
            if (hasNonNullValueForKey(json, key))
            {
                val = json.getInt(key.toString());
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetInt", "key: " + key, ex);
            val = defaultValue;
        }

        return val;
    }

    public static boolean safeGetBool(final JSONObject json, final Object key)
    {
        return safeGetBool(json, key, false);
    }

    public static boolean safeGetBool(final JSONObject json, final Object key, final boolean defaultValue)
    {
        boolean val = defaultValue;

        try
        {
            if (hasNonNullValueForKey(json, key))
            {
                val = json.getBoolean(key.toString());
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetBool", "key: " + key, ex);
            val = defaultValue;
        }

        return val;
    }

    public static float safeGetFloat(final JSONObject json, final Object key)
    {
        return safeGetFloat(json, key, 0.0f);
    }

    public static float safeGetFloat(final JSONObject json, final Object key, final float defaultValue)
    {
        float val = defaultValue;

        try
        {
            if (hasNonNullValueForKey(json, key))
            {
                val = (float)json.getDouble(key.toString());
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetFloat", "key: " + key, ex);
            val = defaultValue;
        }

        return val;
    }

    public static double safeGetDouble(final JSONObject json, final Object key)
    {
        return safeGetDouble(json, key, 0);
    }

    public static double safeGetDouble(final JSONObject json, final Object key, final double defaultValue)
    {
        double val = defaultValue;

        try
        {
            if (hasNonNullValueForKey(json, key))
            {
                val = json.getDouble(key.toString());
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGetDouble", "key: " + key, ex);
            val = defaultValue;
        }

        return val;
    }

    public static byte[] safeGetDataFromBase64String(final JSONObject json, final Object key)
    {
        return safeGetDataFromBase64String(json, key, null);
    }

    public static byte[] safeGetDataFromBase64String(final JSONObject json, final Object key, final byte[] defaultValue)
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
            UULog.error(UUJson.class, "safeGetDataFromBase64String", "key: " + key, ex);
            val = defaultValue;
        }

        return val;
    }

    public static long safeGetDate(
            final JSONObject json,
            final Object key,
            final String[] formatters,
            final TimeZone timeZone)
    {
        return safeGetDate(json, key, 0, formatters, timeZone);
    }

    public static long safeGetDate(
            final JSONObject json,
            final Object key,
            final long defaultValue,
            final String[] formatters,
            final TimeZone timeZone)
    {
        long result = defaultValue;
        String str = safeGetString(json, key, null);

        if (str != null)
        {
            Date dt = UUDate.parseDate(str, timeZone, formatters);

            if (dt != null)
            {
                result = dt.getTime();
            }
        }

        return result;
    }

    public static Object safeGet(final JSONObject json, final Object key)
    {
        return safeGet(json, key, null);
    }

    public static Object safeGet(final JSONObject json, final Object key, final Object defaultValue)
    {
        Object val = defaultValue;

        try
        {
            if (hasNonNullValueForKey(json, key))
            {
                val = json.get(key.toString());
            }
        }
        catch (JSONException ex)
        {
            UULog.error(UUJson.class, "safeGet", "key: " + key, ex);
            val = defaultValue;
        }

        return val;
    }

    public static JSONObject safeGetJsonObject(final JSONObject json, final Object key)
    {
        return safeGetObject(JSONObject.class, json, key);
    }

    public static JSONArray safeGetJsonArray(final JSONObject json, final Object key)
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

    public static <T extends Object> T safeGetObject(final Class<T> type, final JSONObject json, final Object key)
    {
        return safeGetObject(type, json, key, null);
    }

    public static <T extends Object> T safeGetObject(final Class<T> type, final JSONObject json, final Object key, final T defaultValue)
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


    public static void safePut(final JSONObject json, final Object key, final Object val)
    {
        try
        {
            if (json != null && key != null)
            {
                if (val != null)
                {
                    if (val instanceof UUJsonConvertible)
                    {
                        json.put(key.toString(), ((UUJsonConvertible)val).toJsonObject());
                    }
                    else
                    {
                        json.put(key.toString(), val);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "safePut", "key: " + key, ex);
        }
    }

    public static void safePutArray(final JSONObject json, final Object key, final List val)
    {
        try
        {
            if (json != null && key != null)
            {
                if (val != null)
                {
                    JSONArray arr = new JSONArray();
                    for (Object o : val)
                    {
                        if (o instanceof UUJsonConvertible)
                        {
                            arr.put(((UUJsonConvertible)o).toJsonObject());
                        }
                    }

                    json.put(key.toString(), arr);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "safePutArray", "key: " + key, ex);
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
            return null;
        }
    }

    public static Object toJson(final String jsonString)
    {
        Object obj = toJsonObject(jsonString);
        if (obj == null)
        {
            obj = toJsonArray(jsonString);
        }

        return obj;
    }

    public static JSONObject toJsonObject(final String jsonString)
    {
        try
        {
            return new JSONObject(jsonString);
        }
        catch (Exception ex)
        {
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
            return null;
        }
    }

    public static JSONArray toJsonArray(final Iterable list)
    {
        try
        {
            JSONArray arr = new JSONArray();

            for (Object o : list)
            {
                arr.put(o);
            }

            return arr;
        }
        catch (Exception ex)
        {
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

    public static String toJsonString(final UUJsonConvertible jsonConvertible)
    {
        String jsonStr = null;

        try
        {
            if (jsonConvertible != null)
            {
                JSONObject jsonObject = jsonConvertible.toJsonObject();
                if (jsonObject != null)
                {
                    jsonStr = jsonObject.toString();
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "toJsonString", ex);
            jsonStr = null;
        }

        return jsonStr;
    }

    public static String toJsonString(final JSONArray jsonArray)
    {
        String jsonStr = null;

        try
        {
            if (jsonArray != null)
            {
                jsonStr = jsonArray.toString();
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

    public static <T extends UUJsonConvertible> ArrayList<T> parseJsonArray(final Class<T> type, final ArrayList<JSONObject> list)
    {
        ArrayList<T> parsedResults = new ArrayList<>();

        try
        {
            if (type != null && list != null)
            {
                for (JSONObject json : list)
                {
                    T obj = type.newInstance();
                    obj.fillFromJson(json);
                    parsedResults.add(obj);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "parseJsonArray", ex);
            parsedResults = null;
        }

        return parsedResults;
    }

    public static <T extends UUJsonConvertible> ArrayList<T> parseJsonArray(final Class<T> type, final JSONArray list)
    {
        ArrayList<T> parsedResults = new ArrayList<>();

        try
        {
            if (type != null && list != null)
            {
                for (int i = 0; i < list.length(); i++)
                {
                    JSONObject json = list.getJSONObject(i);
                    T obj = type.newInstance();
                    obj.fillFromJson(json);
                    parsedResults.add(obj);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "parseJsonArray", ex);
            parsedResults = null;
        }

        return parsedResults;
    }

    public static <T extends UUJsonConvertible> ArrayList<T> safeGetArrayOfObjects(
            final Class<T> type, final JSONObject json, final String key)
    {
        return parseJsonArray(type, safeGetJsonArray(json, key));
    }

    public static <T extends UUJsonConvertible> T parseJsonObject(final Class<T> type, final JSONObject jsonObj)
    {
        T parsedResult = null;

        try
        {
            if (type != null && jsonObj != null)
            {
                parsedResult = type.newInstance();
                parsedResult.fillFromJson(jsonObj);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUJson.class, "parseJsonObject", ex);
            parsedResult = null;
        }

        return parsedResult;
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

    public static byte[] safeSerializeJson(final UUJsonConvertible jsonObj, final String contentEncoding)
    {
        try
        {
            JSONObject obj = jsonObj.toJsonObject();
            return safeSerializeJson(obj, contentEncoding);
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
            if (obj instanceof UUJsonConvertible)
            {
                json = safeSerializeJson((UUJsonConvertible)obj, contentEncoding);
            }
            else if (obj instanceof JSONObject)
            {
                json = safeSerializeJson((JSONObject)obj, contentEncoding);
            }
            else if (obj instanceof ArrayList)
            {
                JSONArray jsonArray = new JSONArray();

                ArrayList list = (ArrayList)obj;
                for (Object listNode : list)
                {
                    JSONObject jsonObj = null;

                    if (listNode instanceof UUJsonConvertible)
                    {
                        UUJsonConvertible jsonConvertible = (UUJsonConvertible)listNode;
                        jsonObj = jsonConvertible.toJsonObject();
                    }
                    else if (listNode instanceof JSONObject)
                    {
                        jsonObj = (JSONObject)listNode;
                    }

                    if (jsonObj != null)
                    {
                        jsonArray.put(jsonObj);
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

    /*
    // Experimantal function to convert an arbitrary object to JSON via reflection.
    @Nullable
    public static JSONObject objectToJson(@Nullable final Object object)
    {
        JSONObject json = null;

        try
        {
            if (object != null)
            {
                Class objectClass = object.getClass();

                json = new JSONObject();
                Field[] fields = objectClass.getDeclaredFields();

                // compare values now
                for (Field f : fields)
                {

                    f.setAccessible(true);
                    Object val = f.get(object);
                    if (val == null)
                    {
                        val = JSONObject.NULL;
                    }

                    safePut(json, f.getName(), val);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUJson.class, "objectToJson", ex);
            json = null;
        }

        return json;
    }*/
}