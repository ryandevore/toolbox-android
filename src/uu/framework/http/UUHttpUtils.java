package uu.framework.http;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * UUHttpUtils
 *
 * Useful Utilities - A bag of helper methods for the UUHttpClient class
 *
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
}
