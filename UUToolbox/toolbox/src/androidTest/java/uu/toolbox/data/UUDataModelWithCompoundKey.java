package uu.toolbox.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class UUDataModelWithCompoundKey implements UUDataModel
{
    public static String TABLE_NAME = "uu_data_model_with_compound_key";

    public String c1;
    public String c2;
    public String data;

    @NonNull
    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @NonNull
    @Override
    public HashMap<Object, Object> getColumnMap(final int version)
    {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("c1", UUSqlColumnType.text);
        map.put("c2", UUSqlColumnType.text);
        map.put("data", UUSqlColumnType.text);
        return map;
    }

    @Nullable
    @Override
    public String getPrimaryKeyColumnName()
    {
        return "c1, c2";
    }

    @NonNull
    @Override
    public String getPrimaryKeyWhereClause()
    {
        return "c1 = ? AND c2 = ?";
    }

    @NonNull
    @Override
    public String[] getPrimaryKeyWhereArgs()
    {
        return new String[] { c1, c2 };
    }

    @NonNull
    @Override
    public ContentValues getContentValues(final int version)
    {
        ContentValues cv = new ContentValues();
        UUContentValues.putIfNotNull(cv, "c1", c1);
        UUContentValues.putIfNotNull(cv, "c2", c2);
        UUContentValues.putIfNotNull(cv, "data", data);
        return cv;
    }

    @Override
    public void fillFromCursor(@NonNull Cursor cursor)
    {
        c1 = UUCursor.safeGetString(cursor, "c1");
        c2 = UUCursor.safeGetString(cursor, "c2");
        data = UUCursor.safeGetString(cursor, "data");
    }
}
