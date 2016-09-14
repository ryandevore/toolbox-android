package uu.toolbox.core;

import android.content.Context;

import org.json.JSONObject;

public interface UUJsonConvertible
{
    void fillFromJson(final Context context, final JSONObject json);

    JSONObject toJsonObject();
}