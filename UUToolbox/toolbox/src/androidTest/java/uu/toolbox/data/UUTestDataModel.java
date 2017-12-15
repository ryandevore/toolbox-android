package uu.toolbox.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class UUTestDataModel implements UUDataModel
{
    public static String TABLE_NAME = "uu_test_model";

    private static String ID_COLUMN = "id";
    private static String NAME_COLUMN = "name";
    private static String NUMBER_COLUMN = "number";
    private static String TEAM_COLUMN = "team";

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
        return TABLE_NAME;
    }

    @NonNull
    @Override
    public HashMap<String, String> getColumnMap(int version)
    {
        HashMap<String, String> map = new HashMap<>();

        if (version >= VERSION_ONE)
        {
            map.put(ID_COLUMN, UUSql.INTEGER_PRIMARY_KEY_AUTO_INCREMENT_TYPE);
            map.put(NAME_COLUMN, UUSql.TEXT_COLUMN_TYPE);
            map.put(TEAM_COLUMN, UUSql.TEXT_COLUMN_TYPE);
        }

        if (version >= VERSION_TWO)
        {
            map.put(NUMBER_COLUMN, UUSql.INTEGER_COLUMN_TYPE);
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
        return UUSql.buildSingleColumnWhere(ID_COLUMN);
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
            UUContentValues.putIfNotNull(cv, ID_COLUMN, id);
            UUContentValues.putIfNotNull(cv, NAME_COLUMN, name);
            UUContentValues.putIfNotNull(cv, TEAM_COLUMN, team);
        }

        if (version >= VERSION_TWO)
        {
            UUContentValues.putIfNotNull(cv, NUMBER_COLUMN, number);
        }

        return cv;
    }

    @Override
    public void fillFromCursor(@NonNull Cursor cursor)
    {
        id = UUCursor.safeGetInt(cursor, ID_COLUMN);
        name = UUCursor.safeGetString(cursor, NAME_COLUMN);
        team = UUCursor.safeGetString(cursor, TEAM_COLUMN);
        number = UUCursor.safeGetInt(cursor, NUMBER_COLUMN);

    }
}
