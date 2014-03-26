package uu.framework.http;

/**
 * UUHttpClientDelegate
 *
 * Useful Utilities - Callback interface used to communicate from UUHttpClient async task back
 * to the calling application.
 *
 * In the future we may add other callbacks here, such as progress.
 *
 */
public interface UUHttpClientDelegate
{
    void onCompleted(final UUHttpResponse response);
}
