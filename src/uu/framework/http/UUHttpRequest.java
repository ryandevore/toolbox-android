package uu.framework.http;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Wrapper for all request parameters used in a UUHttpClient request
 */
public class UUHttpRequest
{
    protected String _url;
    protected String _httpMethod;
    protected HashMap<String, String> _queryArguments;
    protected HashMap<String, String> _headerFields;
    protected int _timeout;
    protected String _contentType;
    protected byte[] _body;


    public String getURL()
    {
        return _url;
    }

    public void setURL(final String url)
    {
        _url = url;
    }

    public String getHttpMethod()
    {
        return _httpMethod;
    }

    public void setHttpMethod(final String httpMethod)
    {
        _httpMethod = httpMethod;
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

    public static final UUHttpRequest get(final String url, final HashMap<String, String> queryArguments)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpConstants.HttpMethod.GET);
        req.setQueryArguments(queryArguments);
        return req;
    }

    public static final UUHttpRequest delete(final String url, final HashMap<String, String> queryArguments)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpConstants.HttpMethod.DELETE);
        req.setQueryArguments(queryArguments);
        return req;
    }

    public static final UUHttpRequest post(final String url, final HashMap<String, String> queryArguments, final byte[] body, final String contentType)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpConstants.HttpMethod.POST);
        req.setQueryArguments(queryArguments);
        req.setBody(body);
        req.setContentType(contentType);
        return req;
    }

    public static final UUHttpRequest put(final String url, final HashMap<String, String> queryArguments, final byte[] body, final String contentType)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpConstants.HttpMethod.PUT);
        req.setQueryArguments(queryArguments);
        req.setBody(body);
        req.setContentType(contentType);
        return req;
    }

    public static final UUHttpRequest jsonPost(final String url, final HashMap<String, String> queryArguments, final JSONObject body)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpConstants.HttpMethod.POST);
        req.setQueryArguments(queryArguments);
        req.setBody(UUHttpUtils.safeSerializeJson(body, UUHttpConstants.ContentEncoding.UTF8));
        req.setContentType(UUHttpConstants.MimeType.APPLICATION_JSON);
        return req;
    }

    public static final UUHttpRequest jsonPut(final String url, final HashMap<String, String> queryArguments, final JSONObject body)
    {
        UUHttpRequest req = new UUHttpRequest();
        req.setURL(url);
        req.setHttpMethod(UUHttpConstants.HttpMethod.PUT);
        req.setQueryArguments(queryArguments);
        req.setBody(UUHttpUtils.safeSerializeJson(body, UUHttpConstants.ContentEncoding.UTF8));
        req.setContentType(UUHttpConstants.MimeType.APPLICATION_JSON);
        return req;
    }
}
