package uu.toolbox.core;

import uu.toolbox.data.UUTableDefinition;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

/**
 * UUSQLiteStatement
 * 
 * Useful Utilities - A static wrapper around the SQLiteStatement class
 *  
 */
public class UUSQLiteStatement 
{
	public static final String buildInsertSql(final UUTableDefinition tableDef)
	{
    	String[] columns = tableDef.getColumnNames();
    	String[] params = columns.clone();
    	for (int i = 0; i < params.length; i++)
    		params[i] = "?";
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append(String.format("INSERT INTO %s (%s) VALUES (%s)", 
    			tableDef.getTableName(),
    			TextUtils.join(",", columns),
    			TextUtils.join(",", params)));
    	
    	return sb.toString();
	}
	
	public static final void bindInt(final SQLiteStatement sqlStatement, final int columnIndex, final Integer value)
	{
		if (sqlStatement != null)
		{
			if (value != null)
			{
				sqlStatement.bindLong(columnIndex, value.intValue());
			}
			else
			{
				sqlStatement.bindNull(columnIndex);
			}
		}
	}
	
	public static final void bindLong(final SQLiteStatement sqlStatement, final int columnIndex, final Long value)
	{
		if (sqlStatement != null)
		{
			if (value != null)
			{
				sqlStatement.bindLong(columnIndex, value.longValue());
			}
			else
			{
				sqlStatement.bindNull(columnIndex);
			}
		}
	}
	
	public static final void bindString(final SQLiteStatement sqlStatement, final int columnIndex, final String value)
	{
		if (sqlStatement != null)
		{
			if (value != null)
			{
				sqlStatement.bindString(columnIndex, value);
			}
			else
			{
				sqlStatement.bindNull(columnIndex);
			}
		}
	}
	
	public static final void bindData(final SQLiteStatement sqlStatement, final int columnIndex, final byte[] value)
	{
		if (sqlStatement != null)
		{
			if (value != null)
			{
				sqlStatement.bindBlob(columnIndex, value);
			}
			else
			{
				sqlStatement.bindNull(columnIndex);
			}
		}
	}
}
