package uu.toolbox.data;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public final class UUSql
{
    public static String buildCreateSql(final UUTableDefinition tableDef)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE ");
        sb.append(tableDef.getTableName());
        sb.append(" (");

        String[] columns = tableDef.getColumnNames();
        String[] dataTypes = tableDef.getColumnDataTypes();

        if (columns.length == dataTypes.length)
        {
            int count = columns.length;

            for (int i = 0; i < count; i++)
            {
                sb.append(columns[i]);
                sb.append(" ");
                sb.append(dataTypes[i]);

                if (i < (count - 1))
                {
                    sb.append(", ");
                }
            }

            String primaryKey = tableDef.getPrimaryKeyColumnName();
            if (primaryKey != null)
            {
                sb.append(", PRIMARY KEY(");
                sb.append(primaryKey);
                sb.append(")");
            }
        }
        else
        {
            throw new RuntimeException("Column Names and Datatypes do not match!");
        }

        sb.append(");");

        return sb.toString();
    }

    public static String buildCreateSql(final String tableName, final Map<String, String> columnDefs, final String primaryKey)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sb.append(" (");

        boolean first = true;
        for (String col : columnDefs.keySet())
        {
            sb.append(first ? "" : ",");
            String dataType = columnDefs.get(col);
            sb.append(col);
            sb.append(" ");
            sb.append(dataType);
            first = false;
        }

        if (primaryKey != null)
        {
            sb.append(", PRIMARY KEY(");
            sb.append(primaryKey);
            sb.append(")");
        }

        sb.append(");");

        return sb.toString();
    }

    public static void appendDowngradeTableSql(final ArrayList<String> list, final UUTableDefinition destTableDef)
    {
        String tableName = destTableDef.getTableName();
        String tmpTable = tableName + "_old";
        list.add(UUSql.buildRenameTableSql(tableName, tmpTable));
        list.add(UUSql.buildCreateSql(destTableDef));
        list.add(UUSql.buildSelectIntoTableCopy(tmpTable, tableName, destTableDef.getColumnNames()));
        list.add(UUSql.buildDropTableSql(tmpTable));
    }

    public static String buildSelectIntoTableCopy(final String srcTable, final String destTable, final String[] columns)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(destTable);
        sb.append(" SELECT ");

        boolean first = true;
        for (String col : columns)
        {
            sb.append(first ? "" : ",");
            sb.append(col);
            first = false;
        }

        sb.append(" FROM ");
        sb.append(srcTable);
        sb.append(";");

        return sb.toString();
    }

    public static String buildRenameTableSql(final String existingTable, final String newTable)
    {
        return String.format(Locale.getDefault(), "ALTER TABLE %s RENAME TO %s;", existingTable, newTable);
    }

    public static String buildDropTableSql(final String tableName)
    {
        return String.format(Locale.getDefault(), "DROP TABLE IF EXISTS %s;", tableName);
    }

    public static String buildAddColumnSql(final String table, final String column, final String type)
    {
        return String.format(Locale.getDefault(), "ALTER TABLE %s ADD COLUMN %s %s;", table, column, type);
    }

    public static String buildCreateIndexSql(final String table, final String indexName, final String column)
    {
        return String.format(Locale.getDefault(), "CREATE INDEX IF NOT EXISTS %s ON %s (%s);", indexName, table, column);
    }

    public static String buildDropIndexSql(final String indexName)
    {
        return String.format(Locale.getDefault(), "DROP INDEX IF EXISTS %s;", indexName);
    }
}