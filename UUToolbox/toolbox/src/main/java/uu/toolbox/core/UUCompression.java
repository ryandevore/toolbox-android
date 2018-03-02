package uu.toolbox.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import uu.toolbox.logging.UULog;

/**
 * Set of helper methods for compressing and decompressing data.
 */
public class UUCompression
{
    public static byte[] gzip(final byte[] data)
    {
        ByteArrayOutputStream bos = null;
        GZIPOutputStream zos = null;
        byte[] compressed = null;

        try
        {
            bos = new ByteArrayOutputStream();
            zos = new GZIPOutputStream(new BufferedOutputStream(bos));
            zos.write(data);
            UUCloseable.safeClose(zos);
            compressed = bos.toByteArray();
        }
        catch (Exception ex)
        {
            UULog.debug(UUCompression.class, "gzip", ex);
        }
        finally
        {
            UUCloseable.safeClose(zos);
            UUCloseable.safeClose(bos);
        }

        return compressed;
    }

    public static byte[] gunzip(final byte[] data)
    {
        ByteArrayInputStream bis = null;
        GZIPInputStream zis = null;
        byte[] decompressed = null;

        ByteArrayOutputStream bos = null;

        try
        {
            bos = new ByteArrayOutputStream();
            bis = new ByteArrayInputStream(data);
            zis = new GZIPInputStream(new BufferedInputStream(bis));

            int bytesRead;

            byte[] buffer = new byte[1024];

            do
            {
                bytesRead = zis.read(buffer, 0, buffer.length);
                if (bytesRead > 0)
                {
                    bos.write(buffer, 0, bytesRead);
                }

            } while (bytesRead != -1);

            decompressed = bos.toByteArray();
        }
        catch (Exception ex)
        {
            UULog.debug(UUCompression.class, "gunzip", ex);
        }
        finally
        {
            UUCloseable.safeClose(zis);
            UUCloseable.safeClose(bis);
            UUCloseable.safeClose(bos);
        }

        return decompressed;
    }
}
