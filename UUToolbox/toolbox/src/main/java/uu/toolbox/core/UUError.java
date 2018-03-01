package uu.toolbox.core;

import java.util.HashMap;

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
}
