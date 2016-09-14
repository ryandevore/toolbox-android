package uu.toolbox.http;

/**
 * Created by ryandevore on 9/14/16.
 */
public enum UUMimeType
{
    ApplicationJson     ("application/json"),
    TextJson            ("text/json");

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String stringVal;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    UUMimeType(final String val)
    {
        stringVal = val;
    }

    public final String stringVal()
    {
        return stringVal;
    }

    protected static UUMimeType fromString(final String val)
    {
        UUMimeType[] list = UUMimeType.values();
        for (UUMimeType e : list)
        {
            if (e.stringVal().equalsIgnoreCase(val))
            {
                return e;
            }
        }

        return null;
    }
}
