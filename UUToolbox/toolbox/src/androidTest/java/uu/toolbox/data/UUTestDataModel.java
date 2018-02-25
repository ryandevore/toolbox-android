package uu.toolbox.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class UUTestDataModel implements UUDataModel
{
    public static String TableName = "uu_test_model";

    private enum Columns
    {
        id,
        name,
        number,
        team
    }

    private static int VERSION_ONE = 1;
    private static int VERSION_TWO = 2;
    private static int CURRENT_VERSION = VERSION_TWO;

    private int id;
    private String name;
    private int number;
    private String team;

    @NonNull
    @Override
    public String getTableName()
    {
        return TableName;
    }

    @NonNull
    @Override
    public HashMap<Object, Object> getColumnMap(int version)
    {
        HashMap<Object, Object> map = new HashMap<>();

        if (version >= VERSION_ONE)
        {
            map.put(Columns.id, UUSqlColumnType.integerPrimaryKeyAutoIncrement);
            map.put(Columns.name, UUSqlColumnType.text);
            map.put(Columns.team, UUSqlColumnType.text);
        }

        if (version >= VERSION_TWO)
        {
            map.put(Columns.number, UUSqlColumnType.integer);
        }

        return map;
    }

    @Nullable
    @Override
    public String getPrimaryKeyColumnName()
    {
        // Return NULL because create uses auto increment column
        return null;
    }

    @NonNull
    @Override
    public String getPrimaryKeyWhereClause()
    {
        return UUSql.buildSingleColumnWhere(Columns.id);
    }

    @NonNull
    @Override
    public String[] getPrimaryKeyWhereArgs()
    {
        return new String[] { String.valueOf(id) };
    }

    @NonNull
    @Override
    public ContentValues getContentValues(int version)
    {
        ContentValues cv = new ContentValues();

        if (version >= VERSION_ONE)
        {
            UUContentValues.putIfNotNull(cv, Columns.id, id);
            UUContentValues.putIfNotNull(cv, Columns.name, name);
            UUContentValues.putIfNotNull(cv, Columns.team, team);
        }

        if (version >= VERSION_TWO)
        {
            UUContentValues.putIfNotNull(cv, Columns.number, number);
        }

        return cv;
    }

    @Override
    public void fillFromCursor(@NonNull Cursor cursor)
    {
        id = UUCursor.safeGetInt(cursor, Columns.id);
        name = UUCursor.safeGetString(cursor, Columns.name);
        team = UUCursor.safeGetString(cursor, Columns.number);
        number = UUCursor.safeGetInt(cursor, Columns.team);

    }
}
