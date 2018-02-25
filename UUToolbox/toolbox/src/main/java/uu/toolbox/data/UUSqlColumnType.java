package uu.toolbox.data;

import android.support.annotation.NonNull;

public enum UUSqlColumnType
{
    text("TEXT"),
    int8("INTEGER(1)"),
    int16("INTEGER(2)"),
    int32("INTEGER(4)"),
    int64("INTEGER(8)"),
    integer("INTEGER"),
    real("REAL"),
    blob("BLOB"),
    integerPrimaryKeyAutoIncrement("INTEGER PRIMARY KEY AUTOINCREMENT");

    private String value;
    private boolean notNull;
    private Object defaultValue;

    UUSqlColumnType(String val)
    {
        value = val;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(value);

        if (notNull)
        {
            sb.append(" NOT NULL");
        }

        if (defaultValue != null)
        {
            sb.append(" DEFAULT ");
            sb.append(defaultValue.toString());
        }

        return sb.toString();
    }

    @NonNull
    public UUSqlColumnType notNull()
    {
        notNull = true;
        return this;
    }

    @NonNull
    public UUSqlColumnType withDefault(@NonNull final Object value)
    {
        defaultValue = value;
        return this;
    }
}
