package uu.toolbox.core;

import java.util.HashMap;

import uu.toolbox.logging.UULog;

/*
    Android equivalent of NSError.  Simply a container for an error code, domain and a dictionary
    of user information.

 */
public class UUError
{
    private int code;
    private String domain;
    private Exception exception;
    private HashMap<Object, Object> userInfo;

    public UUError()
    {
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public Exception getException()
    {
        return exception;
    }

    public void setException(Exception exception)
    {
        this.exception = exception;
    }

    public HashMap<Object, Object> getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo(HashMap<Object, Object> userInfo)
    {
        this.userInfo = userInfo;
    }

    public Object getUserInfoObject(Object key)
    {
        if (userInfo != null && key != null && userInfo.containsKey(key))
        {
            return userInfo.get(key);
        }
        else
        {
            return null;
        }
    }

    public <T extends Object> T getUserInfoObject(final Class<T> type, Object key)
    {
        T val = null;

        try
        {
            Object obj = getUserInfoObject(key);
            if (obj != null && type != null && obj.getClass().isAssignableFrom(type))
            {
                val = (T) obj;
            }
        }
        catch (Exception ex)
        {
            val = null;
        }

        return val;
    }

    public void addUserInfo(Object key, Object value)
    {
        if (userInfo == null)
        {
            userInfo = new HashMap<>();
        }

        userInfo.put(key, value);
    }

    public static void notifyDelegate(final UUErrorDelegate delegate, final UUError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onCompleted(error);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUError.class, "notifyDelegate", ex);
        }
    }
}
