package uu.framework.core;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Useful set of methods for manipulating Strings
 *
 */
public class UUString 
{
    /**
     * Converts a byte array into a Hex String
     *
     * @param data byte array to convert
     * @return Hex String representation of the data
     */
    public static String byteToHex(final byte[] data)
    {
        if (data == null)
            return "";

        return byteToHex(data, 0, data.length);
    }

    /**
     * Converts a byte array into a Hex String
     *
     * @param data byte array to convert
     * @return Hex String representation of the data
     */
    public static String byteToHex(final byte[] data, final int offset, final int count)
    {
        StringBuilder sb = new StringBuilder();
        String tmp;

        if (data != null)
        {
            for (int i = offset; i < (offset + count); i++)
            {
                tmp = Integer.toHexString((int) data[i] & 0xFF);
                if (tmp.length() < 2)
                    tmp = "0" + tmp;
                sb.append(tmp);
            }
        }

        // Convert to upper case
        tmp = sb.toString();
        if (tmp != null)
            tmp = tmp.toUpperCase();

        return tmp;
    }

    /**
     * Joins String array elements into a single string
     *
     * @param list the source list
     * @param separator the separator
     * @return a string
     */
    public static String componentsJoinedByString(final String[] list, final String separator)
    {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, list);
        return componentsJoinedByString(list, separator);
    }

    public static String componentsJoinedByString(final ArrayList<String> list, final String separator)
    {
        StringBuilder sb = new StringBuilder();

        if (list != null)
        {
            String delim = "";
            if (separator != null)
            {
                delim = separator;
            }

            for (String s : list)
            {
                sb.append(s);
                sb.append(delim);
            }
        }

        return sb.toString();
    }
}
