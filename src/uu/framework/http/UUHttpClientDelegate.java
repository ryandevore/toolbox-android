package uu.framework.http;

/**
 * Created by ryandevore on 3/26/14.
 */
public interface UUHttpClientDelegate
{
    void onCompleted(final UUHttpResponse response);
}
