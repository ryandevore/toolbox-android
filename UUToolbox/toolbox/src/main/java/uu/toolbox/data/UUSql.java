package uu.toolbox.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import uu.toolbox.core.UUString;

@SuppressWarnings("unused")
public final class UUSql
{
    public static String buildCreateSql(final UUDataModel dataModel, final int version)
    {
        return buildCreateSql(dataModel.getTableName(), dataModel.getColumnMap(version), dataModel.getPrimaryKeyColumnName());
    }

    public static String buildCreateSql(final String tableName, final Map<Object, Object> columnDefs, final String primaryKey)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sb.append(" (");

        boolean first = true;
        for (Object col : columnDefs.keySet())
        {
            sb.append(first ? "" : ", ");
            Object dataType = columnDefs.get(col);
            sb.append(col.toString());
            sb.append(" ");
            sb.append(dataType.toString());
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

    public static void appendUpgradeTableSql(@NonNull final ArrayList<String> list,
                                             @NonNull final UUDataModel dataModel,
                                             final int fromVersion,
                                             final int toVersion)
    {
        String tableName = dataModel.getTableName();

        HashMap<Object, Object> fromColumns = dataModel.getColumnMap(fromVersion);
        HashMap<Object, Object> toColumns = dataModel.getColumnMap(toVersion);

        Set<Object> fromColumnNames = fromColumns.keySet();
        Set<Object> toColumnNames = toColumns.keySet();

        ArrayList<Object> removedColumns = new ArrayList<>();
        ArrayList<Object> addedColumns = new ArrayList<>();

        for (Object fromColumn : fromColumnNames)
        {
            if (!toColumnNames.contains(fromColumn))
            {
                removedColumns.add(fromColumn);
            }
        }

        for (Object toColumn : toColumnNames)
        {
            if (!fromColumnNames.contains(toColumn))
            {
                addedColumns.add(toColumn);
            }
        }

        if (removedColumns.size() > 0)
        {
            // SQLite does not support dropping columns, so if any columns are removed in the upgrade,
            // We have to create a temporary table with the new structure, then insert data into it,
            // and then drop the old table.

            String tmpTable = tableName + "_old_" + fromVersion;
            list.add(UUSql.buildRenameTableSql(tableName, tmpTable));
            list.add(UUSql.buildCreateSql(dataModel, toVersion));
            list.add(UUSql.buildSelectIntoTableCopy(tmpTable, tableName, getColumnNames(dataModel, toVersion)));
            list.add(UUSql.buildDropTableSql(tmpTable));
        }
        else
        {
            for (Object column : addedColumns)
            {
                list.add(buildAddColumnSql(tableName, column, toColumns.get(column)));
            }
        }
    }

    public static String buildSelectIntoTableCopy(final String srcTable, final String destTable, final Object[] columns)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(destTable);
        sb.append(" SELECT ");
        sb.append(UUString.componentsJoinedByString(columns, ", "));
        sb.append(" FROM ");
        sb.append(srcTable);
        sb.append(";");
        return sb.toString();
    }

    public static String buildRenameTableSql(final String existingTable, final String newTable)
    {
        return String.format(Locale.US, "ALTER TABLE %s RENAME TO %s;", existingTable, newTable);
    }

    public static String buildDropTableSql(final String tableName)
    {
        return String.format(Locale.US, "DROP TABLE IF EXISTS %s;", tableName);
    }

    public static String buildAddColumnSql(final String table, final Object column, final Object type)
    {
        return String.format(Locale.US, "ALTER TABLE %s ADD COLUMN %s %s;", table, column, type);
    }

    public static String buildCreateIndexSql(final String table, final String indexName, final Object column)
    {
        return String.format(Locale.US, "CREATE INDEX IF NOT EXISTS %s ON %s (%s);", indexName, table, column);
    }

    public static String buildDropIndexSql(final String indexName)
    {
        return String.format(Locale.US, "DROP INDEX IF EXISTS %s;", indexName);
    }

    public static String[] getColumnNames(final UUDataModel dataModel, final int version)
    {
        HashMap<Object, Object> columnMap = dataModel.getColumnMap(version);

        Object[] columnObjects = columnMap.keySet().toArray(new Object[columnMap.size()]);
        String[] list = new String[columnObjects.length];
        for (int i = 0; i < list.length; i++)
        {
            list[i] = columnObjects[i].toString();
        }

        return list;
    }

    public static String buildSingleColumnWhere(@NonNull final Object columnName)
    {
        return String.format(Locale.US, "%s = ?", columnName);
    }

    public static void appendCreateLines(@NonNull final ArrayList<String> list, @NonNull UUDatabaseDefinition databaseDefinition, final int version)
    {
        ArrayList<UUDataModel> tableDefs = databaseDefinition.getDataModels(version);
        if (tableDefs != null)
        {
            for (UUDataModel dataModel : tableDefs)
            {
                list.add(UUSql.buildCreateSql(dataModel, version));
            }
        }
    }

    private static boolean containsModelClass(@NonNull final ArrayList<UUDataModel> list, @NonNull final Class modelClass)
    {

        for (UUDataModel dataModel : list)
        {
            if (dataModel.getClass() == modelClass)
            {
                return true;
            }
        }

        return false;
    }

    public static void analyzeModelSets(
            @NonNull ArrayList<UUDataModel> fromModels,
            @NonNull ArrayList<UUDataModel> toModels,
            @NonNull ArrayList<UUDataModel> addedModels,
            @NonNull ArrayList<UUDataModel> deletedModels,
            @NonNull ArrayList<UUDataModel> migratedModels)
    {
        for (UUDataModel fromModel : fromModels)
        {
            if (containsModelClass(toModels, fromModel.getClass()))
            {
                migratedModels.add(fromModel);
            }
            else
            {
                deletedModels.add(fromModel);
            }
        }

        Iterator<UUDataModel> toListIterator = toModels.iterator();
        while (toListIterator.hasNext())
        {
            UUDataModel model = toListIterator.next();
            if (containsModelClass(migratedModels, model.getClass()))
            {
                toListIterator.remove();
            }
        }

        // Migrated models have been removed, so all that is left are new models
        addedModels.addAll(toModels);
    }

    public static void appendUpgradeLines(
            @NonNull final ArrayList<String> list,
            @NonNull UUDatabaseDefinition databaseDefinition,
            final int fromVersion,
            final int toVersion)
    {
        ArrayList<UUDataModel> fromModels = databaseDefinition.getDataModels(fromVersion);
        ArrayList<UUDataModel> toModels = databaseDefinition.getDataModels(toVersion);

        ArrayList<UUDataModel> addedModels = new ArrayList<>();
        ArrayList<UUDataModel> migratedModels = new ArrayList<>();
        ArrayList<UUDataModel> deletedModels = new ArrayList<>();

        analyzeModelSets(fromModels, toModels, addedModels, deletedModels, migratedModels);

        for (UUDataModel deletedModel : deletedModels)
        {
            list.add(UUSql.buildDropTableSql(deletedModel.getTableName()));
        }

        for (UUDataModel addedModel : addedModels)
        {
            list.add(UUSql.buildCreateSql(addedModel, toVersion));
        }

        for (UUDataModel migratedModel : migratedModels)
        {
            UUSql.appendUpgradeTableSql(list, migratedModel, fromVersion, toVersion);
        }
    }
}