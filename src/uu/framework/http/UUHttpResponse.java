package uu.framework.http;

/**
 * UUHttpResponse
 *
 * Useful Utilities - Encapsulates response data from a UUHttpClient request call
 *
 */
public class UUHttpResponse
{
    protected UUHttpRequest _request;
    protected Exception _exception;
    protected Object _parsedResponse;
    protected int _httpResponseCode;
    protected String _httpResponseMessage;
    protected String _contentType;
    protected String _contentEncoding;

    public UUHttpRequest getRequest()
    {
        return _request;
    }

    public void setRequest(final UUHttpRequest request)
    {
        _request = request;
    }

    public Exception getException()
    {
        return _exception;
    }

    public void setException(final Exception exception)
    {
        _exception = exception;
    }

    public Object getParsedResponse()
    {
        return _parsedResponse;
    }

    public void setParsedResponse(final Object parsedResponse)
    {
        _parsedResponse = parsedResponse;
    }

    public int getHttpResponseCode()
    {
        return _httpResponseCode;
    }

    public void setHttpResponseCode(final int httpResponseCode)
    {
        _httpResponseCode = httpResponseCode;
    }

    public String getHttpResponseMessage()
    {
        return _httpResponseMessage;
    }

    public void setHttpResponseMessage(final String httpResponseMessage)
    {
        _httpResponseMessage = httpResponseMessage;
    }

    public String getContentType()
    {
        return _contentType;
    }

    public void setContentType(final String contentType)
    {
        _contentType = contentType;
    }

    public String getContentEncoding()
    {
        return _contentEncoding;
    }

    public void setContentEncoding(final String contentEncoding)
    {
        _contentEncoding = contentEncoding;
    }
}
