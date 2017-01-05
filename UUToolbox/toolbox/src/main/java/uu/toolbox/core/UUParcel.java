package uu.toolbox.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import uu.toolbox.logging.UULog;

/**
 * Some handy helper's for working with Parcel's
 */
@SuppressWarnings("unused")
public class UUParcel
{
    /**
     * Safely serializes a parcelable into a byte array
     *
     * @param parcelable the object to serialize
     *
     * @return an array of bytes on success or null if an error occurs
     */
    public static @Nullable byte[] serializeParcel(final @Nullable Parcelable parcelable)
    {
        byte[] result = null;

        try
        {
            if (parcelable != null)
            {
                Parcel p = Parcel.obtain();
                parcelable.writeToParcel(p, 0);
                result = p.marshall();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUParcel.class, "serializeParcel", ex);
            result = null;
        }

        return result;
    }

    /**
     * Safely deserializes an array of bytes into a Parcelable object
     *
     * @param parcelableCreator the object creator
     * @param bytes the raw bytes to deserialize
     * @param <T> type of object to be returned
     * @return the deserialized object or null if an error occurs.
     */
    public static @Nullable <T extends Parcelable> T deserializeParcelable(final Parcelable.Creator<T> parcelableCreator, final @Nullable byte[] bytes)
    {
        T result = null;

        try
        {
            if (bytes != null)
            {
                Parcel p = Parcel.obtain();
                p.unmarshall(bytes, 0, bytes.length);
                p.setDataPosition(0);

                result = parcelableCreator.createFromParcel(p);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUParcel.class, "deserializeParcelable", ex);
            result = null;
        }

        return result;
    }

}
