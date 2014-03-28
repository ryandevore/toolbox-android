package uu.framework.http;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * UUHttpClient is a simple wrapper for the java.net.HttpURLConnection class in the Android SDK.
 *
 * The main purpose of this class is to provide an easy to use wrapper for JSON web services.
 */

/**
 * UUHttpClientDelegate
 *
 * Useful Utilities - UUHttpClient is a simple wrapper for the java.net.HttpURLConnection class in the Android SDK.
 *
 * The main purpose of this class is to provide an easy to use wrapper for JSON web services.
 *
 */
public final class UUHttpClient
{
    protected static final String LOG_TAG = UUHttpClient.class.getName();

    public static void get(
            final String url,
            final HashMap<String, String> queryArguments,
            final UUHttpClientDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.get(url, queryArguments);
        UUHttpClientTask task = new UUHttpClientTask(delegate);
        task.execute(request);
    }

    public static void delete(
            final String url,
            final HashMap<String, String> queryArguments,
            final UUHttpClientDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.delete(url, queryArguments);
        UUHttpClientTask task = new UUHttpClientTask(delegate);
        task.execute(request);
    }

    public static void post(
            final String url,
            final HashMap<String, String> queryArguments,
            final byte[] body,
            final String contentType,
            final UUHttpClientDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.post(url, queryArguments, body, contentType);
        UUHttpClientTask task = new UUHttpClientTask(delegate);
        task.execute(request);
    }

    public static void put(
            final String url,
            final HashMap<String, String> queryArguments,
            final byte[] body,
            final String contentType,
            final UUHttpClientDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.put(url, queryArguments, body, contentType);
        UUHttpClientTask task = new UUHttpClientTask(delegate);
        task.execute(request);
    }

    public static void jsonPost(
            final String url,
            final HashMap<String, String> queryArguments,
            final JSONObject body,
            final UUHttpClientDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.jsonPost(url, queryArguments, body);
        UUHttpClientTask task = new UUHttpClientTask(delegate);
        task.execute(request);
    }

    public static void jsonPut(
            final String url,
            final HashMap<String, String> queryArguments,
            final JSONObject body,
            final UUHttpClientDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.jsonPut(url, queryArguments, body);
        UUHttpClientTask task = new UUHttpClientTask(delegate);
        task.execute(request);
    }


    protected static UUHttpResponse executeRequest(UUHttpRequest request)
    {
        UUHttpResponse response = new UUHttpResponse();
        response.setRequest(request);

        HttpURLConnection urlConnection = null;

        try
        {
            URL url = UUHttpUtils.safeCreateUrl(buildFullUrl(request));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod(request.getHttpMethod());

            byte[] body = request.getBody();
            if (body != null && body.length > 0)
            {
                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(request.getBody().length);
                urlConnection.setRequestProperty("Content-Type", request.getContentType());
                urlConnection.setRequestProperty("Content-Length", Integer.toString(body.length));
                writeRequest(urlConnection, body);
            }

            response.setHttpResponseCode(urlConnection.getResponseCode());
            response.setContentType(urlConnection.getContentType());
            response.setContentEncoding(urlConnection.getContentEncoding());

            if (response.getHttpResponseCode() == HttpURLConnection.HTTP_OK)
            {
                byte[] responseBuffer = readResponse(urlConnection);
                response.setParsedResponse(parseResponse(response.getContentType(), response.getContentEncoding(), responseBuffer));
            }

            Log.d(LOG_TAG, "HTTP Response Code: " + response.getHttpResponseCode());
            Log.d(LOG_TAG, "Response Content-Type:" + response.getContentType());
            Log.d(LOG_TAG, "Response Content-Encoding: " + response.getContentEncoding());

        }
        catch (Exception ex)
        {
            Log.e(LOG_TAG, "Caught an exception", ex);
            response.setException(ex);
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }

        return response;
    }

    protected  static String buildFullUrl(final UUHttpRequest request)
    {
        String url = request.getURL().toString();

        HashMap<String, String> args = request.getQueryArguments();

        if (args != null && !args.isEmpty())
        {
            Uri.Builder uri = new Uri.Builder();

            Set<String> keys = args.keySet();
            for (String key : keys)
            {
                String val = args.get(key);
                uri.appendQueryParameter(key, val);
            }

            url += uri.build().toString();
        }

        return url;
    }

    protected static void writeRequest(final HttpURLConnection connection, final byte[] body)
    {
        OutputStream os = null;

        try
        {
            os = new BufferedOutputStream(connection.getOutputStream());
            os.write(body);
            os.flush();
        }
        catch (IOException ex)
        {
            Log.d(LOG_TAG, "Error writing body", ex);
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException ex)
                {

                }
            }
        }
    }

    protected static byte[] readResponse(final HttpURLConnection connection) throws IOException
    {
        InputStream in = new BufferedInputStream(connection.getInputStream());

        int bytesRead = 0;

        byte[] buffer = new byte[10240];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        while (true)
        {
            bytesRead = in.read(buffer, 0, buffer.length);
            if (bytesRead == -1)
                break;

            Log.d(LOG_TAG, "Read " + bytesRead + " bytes");
            bos.write(buffer, 0, bytesRead);
        }

        byte[] responseBuffer = bos.toByteArray();
        return responseBuffer;
    }

    protected static Object parseResponse(final String contentType, final String contentEncoding, final byte[] rawResponse)
    {
        try
        {
            if (contentType.startsWith(UUHttpConstants.MimeType.APPLICATION_JSON) ||
                contentType.startsWith(UUHttpConstants.MimeType.TEXT_JSON))
            {
                String str = new String(rawResponse);
                Log.d(LOG_TAG, "Raw JSON: " + str);
                JSONObject obj = new JSONObject(str);
                return obj;
            }
        }
        catch (Exception ex)
        {
            Log.e(LOG_TAG, "Unable to parse response", ex);
        }

        return null;
    }

}

class UUHttpClientTask extends AsyncTask<UUHttpRequest, Void, UUHttpResponse>
{
    private UUHttpClientDelegate _delegate;

    public UUHttpClientTask(final UUHttpClientDelegate delegate)
    {
        _delegate = delegate;
    }

    @Override
    protected UUHttpResponse doInBackground(UUHttpRequest... uuHttpClientRequests)
    {
        if (uuHttpClientRequests != null && uuHttpClientRequests.length == 1)
        {
            UUHttpRequest request = uuHttpClientRequests[0];
            return UUHttpClient.executeRequest(request);
        }
        else
        {
            return null;
        }
    }

    @Override
    protected void onPostExecute(UUHttpResponse uuHttpClientResponse)
    {
        super.onPostExecute(uuHttpClientResponse);

        try
        {
            if (_delegate != null)
            {
                _delegate.onCompleted(uuHttpClientResponse);
            }
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), "Error notifying callback", ex);
        }
    }
}
