package uu.toolbox.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class UUComplexDataModel implements UUDataModel
{
    public static String TABLE_NAME = "uu_complex_test_model";

    private static String ID_COLUMN = "id";
    private static String STRING_COLUMN_NULLABLE_NO_DEFAULT = "string_nullable_nodefault";
    private static String STRING_COLUMN_NULLABLE_WITH_DEFAULT = "string_nullable_withdefault";
    private static String STRING_COLUMN_NON_NULL_NO_DEFAULT = "string_nonnull_nodefault";
    private static String STRING_COLUMN_NON_NULL_WITH_DEFAULT = "string_nonnull_withdefault";

    private static String INT_COLUMN_NULLABLE_NO_DEFAULT = "int_nullable_nodefault";
    private static String INT_COLUMN_NULLABLE_WITH_DEFAULT = "int_nullable_withdefault";
    private static String INT_COLUMN_NON_NULL_NO_DEFAULT = "int_nonnull_nodefault";
    private static String INT_COLUMN_NON_NULL_WITH_DEFAULT = "int_nonnull_withdefault";

    private static String REAL_COLUMN_NULLABLE_NO_DEFAULT = "real_nullable_nodefault";
    private static String REAL_COLUMN_NULLABLE_WITH_DEFAULT = "real_nullable_withdefault";
    private static String REAL_COLUMN_NON_NULL_NO_DEFAULT = "real_nonnull_nodefault";
    private static String REAL_COLUMN_NON_NULL_WITH_DEFAULT = "real_nonnull_withdefault";

    private static String BLOB_COLUMN_NULLABLE_NO_DEFAULT = "blob_nullable_nodefault";
    private static String BLOB_COLUMN_NULLABLE_WITH_DEFAULT = "blob_nullable_withdefault";
    private static String BLOB_COLUMN_NON_NULL_NO_DEFAULT = "blob_nonnull_nodefault";
    private static String BLOB_COLUMN_NON_NULL_WITH_DEFAULT = "real_nonnull_withdefault";


    private int id;

    private String stringNullableNoDefault;
    private String stringNullableWithDefault;
    private String stringNonNullNoDefault;
    private String stringNonNullWithDefault;

    private int intNullableNoDefault;
    private int intNullableWithDefault;
    private int intNonNullNoDefault;
    private int intNonNullWithDefault;

    private double realNullableNoDefault;
    private double realNullableWithDefault;
    private double realNonNullNoDefault;
    private double realNonNullWithDefault;

    private byte[] blobNullableNoDefault;
    private byte[] blobNullableWithDefault;
    private byte[] blobNonNullNoDefault;
    private byte[] blobNonNullWithDefault;

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

        map.put(ID_COLUMN, UUSql.INTEGER_PRIMARY_KEY_AUTO_INCREMENT_TYPE);

        map.put(STRING_COLUMN_NULLABLE_NO_DEFAULT, UUSql.TEXT_COLUMN_TYPE);
        map.put(STRING_COLUMN_NULLABLE_WITH_DEFAULT, UUSql.TEXT_COLUMN_TYPE + " DEFAULT Foo");
        map.put(STRING_COLUMN_NON_NULL_NO_DEFAULT, UUSql.TEXT_COLUMN_TYPE + " NOT NULL");
        map.put(STRING_COLUMN_NON_NULL_WITH_DEFAULT, UUSql.TEXT_COLUMN_TYPE + " NOT NULL DEFAULT Bar");

        map.put(INT_COLUMN_NULLABLE_NO_DEFAULT, UUSql.INTEGER_COLUMN_TYPE);
        map.put(INT_COLUMN_NULLABLE_WITH_DEFAULT, UUSql.INTEGER_COLUMN_TYPE + " DEFAULT 99");
        map.put(INT_COLUMN_NON_NULL_NO_DEFAULT, UUSql.INTEGER_COLUMN_TYPE + " NOT NULL");
        map.put(INT_COLUMN_NON_NULL_WITH_DEFAULT, UUSql.INTEGER_COLUMN_TYPE + " NOT NULL DEFAULT 4");

        map.put(REAL_COLUMN_NULLABLE_NO_DEFAULT, UUSql.REAL_COLUMN_TYPE);
        map.put(REAL_COLUMN_NULLABLE_WITH_DEFAULT, UUSql.REAL_COLUMN_TYPE + " DEFAULT 57.0");
        map.put(REAL_COLUMN_NON_NULL_NO_DEFAULT, UUSql.REAL_COLUMN_TYPE + " NOT NULL");
        map.put(REAL_COLUMN_NON_NULL_WITH_DEFAULT, UUSql.REAL_COLUMN_TYPE + " NOT NULL DEFAULT 32.0");

        map.put(BLOB_COLUMN_NULLABLE_NO_DEFAULT, UUSql.BLOB_COLUMN_TYPE);
        map.put(BLOB_COLUMN_NULLABLE_WITH_DEFAULT, UUSql.BLOB_COLUMN_TYPE + " DEFAULT 0xABCD");
        map.put(BLOB_COLUMN_NON_NULL_NO_DEFAULT, UUSql.BLOB_COLUMN_TYPE + " NOT NULL");
        map.put(BLOB_COLUMN_NON_NULL_WITH_DEFAULT, UUSql.BLOB_COLUMN_TYPE + " NOT NULL DEFAULT 0x1234");

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

        UUContentValues.putIfNotNull(cv, ID_COLUMN, id);

        return cv;
    }

    @Override
    public void fillFromCursor(@NonNull Cursor cursor)
    {
        id = UUCursor.safeGetInt(cursor, ID_COLUMN);

    }
}
