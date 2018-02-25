package uu.toolbox.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class UUComplexDataModel implements UUDataModel
{
    public static String TableName = "uu_complex_test_model";

    private enum Columns
    {
        id,
        string_nullable_nodefault,
        string_nullable_withdefault,
        string_nonnull_nodefault,
        string_nonnull_withdefault,

        int_nullable_nodefault,
        int_nullable_withdefault,
        int_nonnull_nodefault,
        int_nonnull_withdefault,

        real_nullable_nodefault,
        real_nullable_withdefault,
        real_nonnull_nodefault,
        real_nonnull_withdefault,

        blob_nullable_nodefault,
        blob_nullable_withdefault,
        blob_nonnull_nodefault,
        blob_nonnull_withdefault
    }


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
        return TableName;
    }

    @NonNull
    @Override
    public HashMap<Object, Object> getColumnMap(int version)
    {
        HashMap<Object, Object> map = new HashMap<>();

        map.put(Columns.id, UUSqlColumnType.integerPrimaryKeyAutoIncrement);

        map.put(Columns.string_nullable_nodefault, UUSqlColumnType.text);
        map.put(Columns.string_nullable_withdefault, UUSqlColumnType.text.withDefault("Foo"));
        map.put(Columns.string_nonnull_nodefault, UUSqlColumnType.text.notNull());
        map.put(Columns.string_nonnull_withdefault, UUSqlColumnType.text.notNull().withDefault("Bar"));

        map.put(Columns.int_nullable_nodefault, UUSqlColumnType.integer);
        map.put(Columns.int_nullable_withdefault, UUSqlColumnType.integer.withDefault("99"));
        map.put(Columns.int_nonnull_nodefault, UUSqlColumnType.integer.notNull());
        map.put(Columns.int_nonnull_withdefault, UUSqlColumnType.integer.notNull().withDefault("4"));

        map.put(Columns.real_nullable_nodefault, UUSqlColumnType.real);
        map.put(Columns.real_nullable_withdefault, UUSqlColumnType.real.withDefault("57.0"));
        map.put(Columns.real_nonnull_nodefault, UUSqlColumnType.real.notNull());
        map.put(Columns.real_nonnull_withdefault, UUSqlColumnType.real.notNull().withDefault("32.0"));

        map.put(Columns.blob_nullable_nodefault, UUSqlColumnType.blob);
        map.put(Columns.blob_nullable_withdefault, UUSqlColumnType.blob.withDefault("0xABCD"));
        map.put(Columns.blob_nonnull_nodefault, UUSqlColumnType.blob.notNull());
        map.put(Columns.blob_nonnull_withdefault, UUSqlColumnType.blob.notNull().withDefault("0x1234"));

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

        UUContentValues.putIfNotNull(cv, Columns.id, id);

        return cv;
    }

    @Override
    public void fillFromCursor(@NonNull Cursor cursor)
    {
        id = UUCursor.safeGetInt(cursor, Columns.id);

    }
}
