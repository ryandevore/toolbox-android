package uu.toolbox.data;

import android.database.sqlite.SQLiteStatement;

/**
 * UUSQLiteStatement
 * 
 * Useful Utilities - A static wrapper around the SQLiteStatement class
 *  
 */
@SuppressWarnings("unused")
public class UUSQLiteStatement 
{
	public static void bindInt(final SQLiteStatement sqlStatement, final int columnIndex, final Integer value)
	{
		if (sqlStatement != null)
		{
			if (value != null)
			{
				sqlStatement.bindLong(columnIndex, value);
			}
			else
			{
				sqlStatement.bindNull(columnIndex);
			}
		}
	}
	
	public static void bindLong(final SQLiteStatement sqlStatement, final int columnIndex, final Long value)
	{
		if (sqlStatement != null)
		{
			if (value != null)
			{
				sqlStatement.bindLong(columnIndex, value);
			}
			else
			{
				sqlStatement.bindNull(columnIndex);
			}
		}
	}
	
	public static void bindString(final SQLiteStatement sqlStatement, final int columnIndex, final String value)
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
	
	public static void bindData(final SQLiteStatement sqlStatement, final int columnIndex, final byte[] value)
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
