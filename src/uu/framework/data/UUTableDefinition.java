package uu.framework.data;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Interface for defining a SQL table definition
 */
public interface UUTableDefinition 
{
	//////////////////////////////////////////////////////////////////////////////////////////
	// Constants
	//////////////////////////////////////////////////////////////////////////////////////////

	public static final String TEXT_COLUMN_TYPE = "TEXT";
	public static final String INT8_COLUMN_TYPE = "INTEGER(1)";
	public static final String INT16_COLUMN_TYPE = "INTEGER(2)";
	public static final String INT32_COLUMN_TYPE = "INTEGER(4)";
	public static final String INT64_COLUMN_TYPE = "INTEGER(8)";
	public static final String INTEGER_COLUMN_TYPE = "INTEGER";
	public static final String REAL_COLUMN_TYPE = "REAL";
	public static final String BLOB_COLUMN_TYPE = "BLOB";
	public static final String INTEGER_PRIMARY_KEY_AUTO_INCREMENT_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// Interface
	//////////////////////////////////////////////////////////////////////////////////////////
	
	String getTableName();
	String[] getColumnNames();
	String[] getColumnDataTypes();
	String getPrimaryKeyColumnName();
	String getPrimaryKeyWhereClause();
	String[] getPrimaryKeyArgs();
	
	int getVersion();
	
	ContentValues getContentValues();
	void fillFromCursor(final Cursor cursor);
}