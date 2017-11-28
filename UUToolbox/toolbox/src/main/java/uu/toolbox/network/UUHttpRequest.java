package uu.toolbox.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.Proxy;
import java.util.HashMap;

import javax.net.ssl.SSLSocketFactory;

import uu.toolbox.core.UUJson;

/**
 * UUHttpRequest
 *
 * Useful Utilities - Wrapper to encapsulate all request params needed for a UUHttpClient request.
 *
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class UUHttpRequest
{
    protected String _url;
    protected UUHttpMethod httpMethod;
    protected HashMap<String, String> _queryArguments;
    protected HashMap<String, String> _headerFields;
    protected int _timeout;
    protected String _contentType;
    protected byte[] _body;
    protected Proxy _proxy;
    protected SSLSocketFactory _sslSocketFactory;
    protected  boolean _processMimeTypes;


    public String getURL()
    {
        return _url;
    }

    public void setURL(final String url)
    {
        _url = url;
    }

    public UUHttpMethod getHttpMethod()
    {
        return httpMethod;
    }

    public void setHttpMethod(final UUHttpMethod httpMethod)
    {
        this.httpMethod = httpMethod;
    }

    public HashMap<String, String> getQueryArguments()
    {
        return _queryArguments;
    }

    public void setQueryArguments(final HashMap<String, String> queryArguments)
    {
        _queryArguments = queryArguments;
    }

    public HashMap<String, String> getHeaderFields()
    {
        return _headerFields;
    }

    public void setHeaderFields(final HashMap<String, String> headerFields)
    {
        _headerFields = headerFields;
    }

    public int getTimeout()
    {
        return _timeout;
    }

    public void setTimeout(final int timeout)
    {
        _timeout = timeout;
    }

    public String getContentType()
    {
        return _contentType;
    }

    public void setContentType(final String contentType)
    {
        _contentType = contentType;
    }

    public byte[] getBody()
    {
        return _body;
    }

    public void setBody(final byte[] body)
    {
        _body = body;
    }

    public Proxy getProxy() { return _proxy; }

    public void setProxy(final Proxy proxy) { _proxy = proxy; }

    public SSLSocketFactory getSocketFactory()
    {
        return _sslSocketFactory;
    }

    public void setSocketFactory(final SSLSocketFactory socketFactory)
    {
        _sslSocketFactory = socketFactory;
    }

    public boolean getProcessMimeTypes()
    {
        return _processMimeTypes;
    }

    public void setProcessMimeTypes(final boolean processMimeTypes)
    {
        _processMimeTypes = processMimeTypes;
    }

    public static UUHttpRequest get(final String url, final HashMap<String, String> queryArguments)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpMethod.GET);
        req.setQueryArguments(queryArguments);
        return req;
    }

    public static UUHttpRequest delete(final String url, final HashMap<String, String> queryArguments)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpMethod.DELETE);
        req.setQueryArguments(queryArguments);
        return req;
    }

    public static UUHttpRequest post(final String url, final HashMap<String, String> queryArguments, final byte[] body, final String contentType)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpMethod.POST);
        req.setQueryArguments(queryArguments);
        req.setBody(body);
        req.setContentType(contentType);
        return req;
    }

    public static UUHttpRequest put(final String url, final HashMap<String, String> queryArguments, final byte[] body, final String contentType)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpMethod.PUT);
        req.setQueryArguments(queryArguments);
        req.setBody(body);
        req.setContentType(contentType);
        return req;
    }

    public static UUHttpRequest jsonPost(final String url, final HashMap<String, String> queryArguments, final JSONObject body)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpMethod.POST);
        req.setQueryArguments(queryArguments);
        req.setBody(UUJson.safeSerializeJson(body, UUContentEncoding.UTF8.toString()));
        req.setContentType(UUMimeType.ApplicationJson.stringVal());
        return req;
    }

    public static UUHttpRequest jsonPut(final String url, final HashMap<String, String> queryArguments, final JSONObject body)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpMethod.PUT);
        req.setQueryArguments(queryArguments);
        req.setBody(UUJson.safeSerializeJson(body, UUContentEncoding.UTF8.toString()));
        req.setContentType(UUMimeType.ApplicationJson.stringVal());
        return req;
    }

    public static UUHttpRequest jsonPost(final String url, final HashMap<String, String> queryArguments, final JSONArray body)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpMethod.POST);
        req.setQueryArguments(queryArguments);
        req.setBody(UUJson.safeSerializeJson(body, UUContentEncoding.UTF8.toString()));
        req.setContentType(UUMimeType.ApplicationJson.stringVal());
        return req;
    }
}