package uu.toolbox.core;

import android.support.annotation.NonNull;

import org.json.JSONObject;

public interface UUJsonConvertible
{
    void fillFromJson(@NonNull final JSONObject json);

    @NonNull
    JSONObject toJsonObject();
}