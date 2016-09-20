package uu.toolbox.core;

/**
 * Helpful set of extension methods for the Exception class
 */
public final class UUException
{
    private UUException() { }

    /**
     * Safely checks an exception to determine if it is a network timeout exception
     *
     * @param ex the exception to check
     * @return true if the exception is a SocketTimeoutException
     */
    public static boolean isNetworkTimeoutException(Exception ex)
    {
        return (ex != null && ex instanceof java.net.SocketTimeoutException);
    }

    /**
     * Safely checks an exception to determine if it is a network failure exception
     *
     * @param ex the exception to check
     * @return true if the exception is a SocketException or UnknownHostException
     */
    public static boolean isNetworkFailureException(Exception ex)
    {
        if (ex != null)
        {
            if (ex instanceof java.net.UnknownHostException ||
                    ex instanceof java.net.SocketException)
            {
                return true;
            }
        }

        return false;
    }
}
