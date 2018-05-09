package uu.toolbox.core;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import uu.toolbox.logging.UULog;

public interface UUJsonConvertible
{
    void fillFromJson(@NonNull final JSONObject json);

    @NonNull
    JSONObject toJsonObject();


    default void writeJsonToParcel(Parcel dest, int flags)
    {
        dest.writeString(toJsonObject().toString());
    }

    default void fillJsonFromParcel(final Parcel in)
    {
        try
        {
            String jsonString = in.readString();
            JSONObject json = new JSONObject(jsonString);
            fillFromJson(json);
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "fillFromParcel", ex);
        }
    }
}