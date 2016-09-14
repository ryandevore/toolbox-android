package uu.toolbox.data;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unused")
public final class UUSql
{
    public static final String TEXT_COLUMN_TYPE = "TEXT";
    public static final String INT8_COLUMN_TYPE = "INTEGER(1)";
    public static final String INT16_COLUMN_TYPE = "INTEGER(2)";
    public static final String INT32_COLUMN_TYPE = "INTEGER(4)";
    public static final String INT64_COLUMN_TYPE = "INTEGER(8)";
    public static final String INTEGER_COLUMN_TYPE = "INTEGER";
    public static final String REAL_COLUMN_TYPE = "REAL";
    public static final String BLOB_COLUMN_TYPE = "BLOB";
    public static final String INTEGER_PRIMARY_KEY_AUTO_INCREMENT_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";

    public static String buildCreateSql(final UUDataModel dataModel)
    {
        return buildCreateSql(dataModel.getTableName(), dataModel.getColumnMap(), dataModel.getPrimaryKeyColumnName());
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

    public static void appendDowngradeTableSql(final ArrayList<String> list, final UUDataModel dataModel)
    {
        String tableName = dataModel.getTableName();
        String tmpTable = tableName + "_old";
        list.add(UUSql.buildRenameTableSql(tableName, tmpTable));
        list.add(UUSql.buildCreateSql(dataModel));
        list.add(UUSql.buildSelectIntoTableCopy(tmpTable, tableName, getColumnNames(dataModel)));
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

    public static String[] getColumnNames(final UUDataModel dataModel)
    {
        return dataModel.getColumnMap().keySet().toArray(new String[dataModel.getColumnMap().size()]);
    }
}