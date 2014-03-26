package uu.framework.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ryandevore on 3/26/14.
 */
public class UUHttpUtils
{
    public static final URL safeCreateUrl(final String url)
    {
        try
        {
            return new URL(url);
        }
        catch (MalformedURLException ex)
        {
            return null;
        }
    }

    public static final byte[] safeSerializeJson(final JSONObject jsonObj, final String contentEncoding)
    {
        try
        {
            String str = jsonObj.toString();
            return str.getBytes(contentEncoding);
        }
        catch (Exception ex)
        {
            return null;
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
