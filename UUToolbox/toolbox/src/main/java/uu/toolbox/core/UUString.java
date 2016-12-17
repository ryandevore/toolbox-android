package uu.toolbox.core;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Useful set of methods for manipulating Strings
 *
 */
@SuppressWarnings("unused")
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

    /**
     * Joins a list of strings into a single string
     *
     * @param list the source list
     * @param separator the separator
     * @return a string
     */
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

    /**
     * Checks a string for null and length
     *
     * @param s the string to check
     * @return true if the string null or the length is zero
     */
    public static boolean isEmpty(final String s)
    {
        return (s == null || s.length() == 0);
    }

    /**
     * Checks a string for null and length
     *
     * @param s the string to check
     * @return true if the string is not null and has a length greater than zero
     */
    public static boolean isNotEmpty(final String s)
    {
        return (s != null && s.length() > 0);
    }

    /**
     * Capitalizes the first letter of a string
     *
     * @param s the string
     * @return A string with the first letter capitalized, or the string itself if the length is
     * less than zero or null.
     */
    public static String firstLetterCapital(final String s)
    {
        if (isNotEmpty(s) && s.length() > 1)
        {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        else
        {
            return s;
        }
    }

    public static boolean areEqual(final String lhs, final String rhs)
    {
        if (lhs == null && rhs == null)
        {
            return true;
        }
        else if (lhs == null || rhs == null)
        {
            return false;
        }
        else
        {
            return lhs.equals(rhs);
        }
    }
}
