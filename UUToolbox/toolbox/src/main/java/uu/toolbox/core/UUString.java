package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.util.ArrayList;
import java.util.Collections;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Strings
 *
 */
@SuppressWarnings("unused")
public class UUString 
{
    /**
     * "UTF-8" as a constant string
     */
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";

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

        return sb.toString().toUpperCase();
    }

    /**
     * Converts a hex string to a byte array
     *
     * @param data the string to convert
     *
     * @return a byte array or null if the string has invalid hex characters
     */
    public static byte[] hexToByte(final String data)
    {
        byte[] buffer = null;

        try
        {
            if (data != null)
            {
                String workingData = data;
                if ((data.length() % 2 != 0))
                {
                    workingData = "0" + data;
                }

                int bufferLength = workingData.length() / 2;
                buffer = new byte[bufferLength];
                for (int i = 0; i < bufferLength; i++)
                {
                    buffer[i] = (byte) (Integer.parseInt(workingData.substring((i * 2), (i * 2) + 2), 16));
                }
            }
        }
        catch (NumberFormatException ex)
        {
            buffer = null;
        }

        return buffer;
    }

    /**
     * Safely creates a string from a byte array
     *
     * @param data the byte array
     * @param encoding the encoding to use
     * @return a string or "" if an exception is caught
     */
    public static String byteToString(final byte[] data, final String encoding)
    {
        try
        {
            return new String(data, encoding);
        }
        catch (Exception ex)
        {
            UULog.debug(UUString.class, "byteToString", ex);
            return "";
        }
    }

    /**
     * Safely creates a UTF8 string
     *
     * @param data the byte array
     * @return a string or "" if an exception is caught
     */
    public static String byteToUtf8String(final byte[] data)
    {
        return byteToString(data, CHARSET_UTF8);
    }

    /**
     * Safely creates an ASCII (Charset ISO-8859-10 string
     *
     * @param data the byte array
     * @return a string or "" if an exception is caught
     */
    public static String byteToAsciiString(final byte[] data)
    {
        return byteToString(data, CHARSET_ISO_8859_1);
    }

    /**
     * Safely creates a byte[] from a string
     *
     * @param input the string
     * @param encoding the character encoding to use
     * @return a byte[] or null if an error occurs
     */
    public static byte[] stringToByte(final String input, final String encoding)
    {
        byte[] encoded = null;

        try
        {
            if (input != null && encoding != null)
            {
                encoded = input.getBytes(encoding);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUString.class, "stringToByte", ex);
            encoded = null;
        }

        return encoded;
    }

    /**
     * Safely creates a UTF8 byte array
     *
     * @param input the input instring
     * @return a byte[] or null if an error occurs
     */
    public static byte[] stringToUtf8Bytes(final String input)
    {
        return stringToByte(input, CHARSET_UTF8);
    }

    /**
     * Safely creates an ASCII (Charset ISO-8859-1) byte array
     *
     * @param input the input string
     * @return a string or "" if an exception is caught
     */
    public static byte[] stringToAsciiBytes(final String input)
    {
        return stringToByte(input, CHARSET_ISO_8859_1);
    }

    @NonNull
    public static String byteToBase64String(@Nullable final byte[] data)
    {
        if (data == null)
        {
            return "";
        }

        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    /**
     * Joins String array elements into a single string
     *
     * @param list the source list
     * @param separator the separator
     * @return a string
     */
    public static String componentsJoinedByString(final Object[] list, final String separator)
    {
        ArrayList<Object> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, list);
        return componentsJoinedByString(arrayList, separator);
    }

    /**
     * Joins a list of strings into a single string
     *
     * @param list the source list
     * @param separator the separator
     * @return a string
     */
    public static String componentsJoinedByString(final ArrayList<Object> list, final String separator)
    {
        StringBuilder sb = new StringBuilder();

        if (list != null)
        {
            String delim = "";
            if (separator != null)
            {
                delim = separator;
            }

            for (Object s : list)
            {
                if (sb.length() > 0)
                {
                    sb.append(delim);
                }

                sb.append(s.toString());
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

    /**
     * Safely compares two strings
     *
     * @param lhs left side
     * @param rhs right side
     * @return boolean
     */
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

    /**
     * Wrapper to return an Object's toString() result or null.
     *
     * @param object the object
     *
     * @return a non null string
     */
    public static @NonNull String safeToString(final @Nullable Object object)
    {
        String result = null;

        if (object != null)
        {
            result = object.toString();
        }

        if (result == null)
        {
            result = "";
        }

        return result;
    }
}
