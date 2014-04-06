package uu.framework.http;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by ryandevore on 4/6/14.
 */
public class UUHttpClientTask extends AsyncTask<UUHttpRequest, Void, UUHttpResponse>
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
