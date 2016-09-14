package uu.toolbox.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Vector;

import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

/**
 * UUBaseDatabase
 * 
 * Useful Utilities - A simple base class for Android SQL Lite databases.
 *  
 */
@SuppressWarnings("unused")
abstract class UUDatabase implements UUDatabaseDefinition
{
	///////////////////////////////////////////////////////////////////////////////////////////////
    // Member Variables 
	///////////////////////////////////////////////////////////////////////////////////////////////
	protected static Context theContext;
	protected static SQLiteOpenHelper theDatabaseHelper;
	protected static UUDatabase theSharedDatabase;
    protected SQLiteDatabase database;

	public static void init(final Context context, final Class<? extends UUDatabase> dbClass, final String databasePassword)
	{
        try
        {
            theContext = context;
            theSharedDatabase = dbClass.newInstance();
            theDatabaseHelper = new UUDatabaseHelper(context, theSharedDatabase);
            theSharedDatabase.database = theDatabaseHelper.getWritableDatabase();
        }
        catch (Exception ex)
        {
            UULog.error(UUDatabase.class, "init", ex);

            theContext = null;
            theSharedDatabase = null;
            theDatabaseHelper = null;
        }
	}

    public static void destroy(final Context context, final Class<? extends UUDatabase> dbClass)
    {
        safeCloseSharedDatabase();

        try
        {
            UUDatabase db = dbClass.newInstance();
            context.deleteDatabase(db.getDatabaseName());
        }
        catch (Exception ex)
        {
            UULog.error(UUDatabase.class, "destroy", ex);
        }
        finally
        {
            theContext = null;
            theSharedDatabase = null;
            theDatabaseHelper = null;
        }
    }

    private static void safeCloseSharedDatabase()
    {
        try
        {
            if (theSharedDatabase != null)
            {
                theSharedDatabase.closeDatabase(theSharedDatabase.database);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUDatabase.class, "safeCloseSharedDatabase", ex);
        }
    }
	
	///////////////////////////////////////////////////////////////////////////////////////////////
    // Construction 
	///////////////////////////////////////////////////////////////////////////////////////////////
	protected UUDatabase()
	{
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods 
	///////////////////////////////////////////////////////////////////////////////////////////////

	protected Context getContext()
	{
		return theContext;
	}
	
	/**
	 * Returns a read only copy of the database
	 * 
	 * @return a SQLiteDatabase instance
	 */
	public SQLiteDatabase getReadOnlyDatabase()
	{
		return database;
	}
	
	/**
	 * Returns a read write copy of the database
	 * 
	 * @return a SQLiteDatabase instance
	 */
	public SQLiteDatabase getReadWriteDatabase()
	{
		return database;
	}

    /**
     * Queries the database for a set of objects
     * 
     * @param type row object type
     * @param selection where clause
     * @param selectionArgs bound where arguments
     * @param orderBy order by clause
     * @param limit limit clause
     * @return a List of 
     */
    public synchronized <T extends UUTableDefinition> ArrayList<T> queryMultipleObjects(final Class<T> type, final String selection, final String[] selectionArgs, final String orderBy, final String limit)
    {
    	ArrayList<T> results = new ArrayList<>();
    	
    	SQLiteDatabase db;
		Cursor c = null;
		
    	try
    	{
		    db = getReadOnlyDatabase();
		   
		    T tableDef = type.newInstance();
		    
		    c = db.query(tableDef.getTableName(), tableDef.getColumnNames(), selection, selectionArgs, null, null, orderBy, limit);
		    
		    while (c.moveToNext())
		    {
		    	T obj = type.newInstance();
		    	obj.fillFromCursor(c);
		    	results.add(obj);
		    }
    	}
    	catch (Exception ex)
    	{
    		logException("queryMultipleObjects", ex);
    	}
    	finally
    	{
    		closeCursor(c);
    	}
    	
    	return results;
    }
    
    /**
     * Queries the database for a set of objects
     * 
     * @param type row object type
     * @param rawSqlQuery The sql query
     * @param selectionArgs bound where arguments
     * @return a List of objects of type T
     */
    public synchronized <T extends UUTableDefinition> ArrayList<T> rawQueryMultipleObjects(final Class<T> type, final String rawSqlQuery, final String[] selectionArgs)
    {
    	ArrayList<T> results = new ArrayList<>();
    	
    	SQLiteDatabase db;
		Cursor c = null;
		
    	try
    	{
		    db = getReadOnlyDatabase();
		    c = db.rawQuery(rawSqlQuery, selectionArgs);
		    
		    while (c.moveToNext())
		    {
		    	T obj = type.newInstance();
		    	obj.fillFromCursor(c);
		    	results.add(obj);
		    }
    	}
    	catch (Exception ex)
    	{
    		logException("rawQueryMultipleObjects", ex);
    	}
    	finally
    	{
    		closeCursor(c);
    	}
    	
    	return results;
    }
    
    /**
     * Queries the database for a single object
     * 
     * @param type row object type
     * @param selection where clause
     * @param selectionArgs bound where arguments
     * @param orderBy order by clause
     * @return a List of 
     */
    public synchronized <T extends UUTableDefinition> T querySingleObject(final Class<T> type, final String selection, final String[] selectionArgs, final String orderBy)
    {
    	ArrayList<T> list = queryMultipleObjects(type, selection, selectionArgs, orderBy, "1");
    	if (list != null && list.size() == 1)
    	{
    		return list.get(0);
    	}

    	return null;
    }
    
    /**
     * Runs a query expecting a single integer cell as the result
     *
     * @param sql the sql to run
     * @return a result
     */
    public synchronized String querySingleStringCell(final String sql, final String defaultValue)
    {
    	SQLiteDatabase db;
    	Cursor c = null;
    	String result = defaultValue;
                    
    	try
    	{
    		db = getReadOnlyDatabase();
    		
    		logSql(sql);
    		c = db.rawQuery(sql, null);
    		
    		if (c.moveToFirst())
    		{
    			result = c.getString(0);
    		}
    	}
	    catch (Exception ex)
	    {
	    	logException("querySingleStringCell", ex);
	    	result = defaultValue;
	    }
	    finally
	    {
	    	closeCursor(c);
	    }
	    
	    return result;
    }
    
    /**
     * Runs a query expecting a single integer cell as the result
     *
     * @param sql the sql to run
     * @return a result
     */
    public synchronized int querySingleIntCell(final String sql, final int defaultValue)
    {
    	SQLiteDatabase db;
    	Cursor c = null;
    	int result = defaultValue;
                    
    	try
    	{
    		db = getReadOnlyDatabase();
    		
    		logSql(sql);
    		c = db.rawQuery(sql, null);

    		if (c.moveToFirst())
    		{
    			result = c.getInt(0);
    		}
    	}
	    catch (Exception ex)
	    {
	    	logException("querySingleIntCell", ex);
	    	result = defaultValue;
	    }
	    finally
	    {
	    	closeCursor(c);
	    }
	    
	    return result;
    }

    /**
     * Runs a query expecting a single integer cell as the result
     *
     * @param sql the sql to run
     * @return a result
     */
    public synchronized long querySingleLongCell(final String sql, final long defaultValue)
    {
        SQLiteDatabase db;
        Cursor c = null;
        long result = defaultValue;

        try
        {
            db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, null);

            if (c.moveToFirst())
            {
                result = c.getLong(0);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleLongCell", ex);
            result = defaultValue;
        }
        finally
        {
            closeCursor(c);
        }

        return result;
    }
    
    /**
     * Runs a query expecting a single float cell as the result
     *
     * @param sql the sql to run
     * @return a result
     */
    public synchronized float querySingleFloatCell(final String sql, final float defaultValue)
    {
    	SQLiteDatabase db;
    	Cursor c = null;
    	float result = defaultValue;
                    
    	try
    	{
    		db = getReadOnlyDatabase();
    		
    		logSql(sql);
    		c = db.rawQuery(sql, null);
    		
    		if (c.moveToFirst())
    		{
    			result = c.getFloat(0);
    		}
    	}
	    catch (Exception ex)
	    {
	    	logException("querySingleFloatCell", ex);
	    	result = defaultValue;
	    }
	    finally
	    {
	    	closeCursor(c);
	    }
	    
	    return result;
    }

    /**
     * Runs a query expecting a single float cell as the result
     *
     * @param sql the sql to run
     * @return a result
     */
    public synchronized byte[] querySingleBlobCell(final String sql, final byte[] defaultValue)
    {
        SQLiteDatabase db;
        Cursor c = null;
        byte[] result = defaultValue;

        try
        {
            db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, null);

            if (c.moveToFirst())
            {
                result = c.getBlob(0);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleBlobCell", ex);
            result = defaultValue;
        }
        finally
        {
            closeCursor(c);
        }

        return result;
    }
    
    /**
     * Inserts an object
     * 
     * @param type row object type
     * @param object the object to update
     * @return an object of type T
     */
    public synchronized <T extends UUTableDefinition> T addObject(final Class<T> type, T object)
    {
    	long rowid = insertRow(object.getTableName(), object.getContentValues());
    	return querySingleObject(type, "ROWID = ?", new String[] { String.valueOf(rowid) }, null);
    }
    
    /**
     * Inserts or Updates an object
     * 
     * @param type row object type
     * @param object the object to update
     * @return an object of type T
     */
    public synchronized <T extends UUTableDefinition> T updateObject(final Class<T> type, T object)
    {
    	String whereClause = object.getPrimaryKeyWhereClause();
    	String[] whereArgs = object.getPrimaryKeyArgs();

        T lookup = querySingleObject(type, whereClause, whereArgs, null);
        if (lookup == null)
        {
            return addObject(type, object);
        }
        else
        {
            updateRow(object.getTableName(), object.getContentValues(), whereClause, whereArgs);
            return querySingleObject(type, whereClause, whereArgs, null);
        }
    }

    /**
     * Deletes an object
     *
     * @param type row object type
     * @param object the object to update
     *
     */
    public synchronized <T extends UUTableDefinition> void deleteObject(final Class<T> type, T object)
    {
        String whereClause = object.getPrimaryKeyWhereClause();
        String[] whereArgs = object.getPrimaryKeyArgs();
        delete(object.getTableName(), whereClause, whereArgs);
    }
    
    /**
     * WARNING - This method will delete all rows from all tables
     */
    public synchronized void resetDatabase()
    {
    	try
        {
    		Vector<UUTableDefinition> tableDefs = getTableDefinitions();
			int count = tableDefs.size();
			
			for (int i = 0; i < count; i++)
			{
				UUTableDefinition tableDef = tableDefs.elementAt(i);
				truncateTable(tableDef.getTableName());
			}
        }
        catch (Exception ex)
        {
        	logException("resetDatabase", ex);
        }
    }

    /**
     * WARNING - This method will delete all rows from the table
     *
     * @param tableName the table name
     */
    public synchronized void resetTable(final String tableName)
    {
        if (tableName != null)
        {
            truncateTable(tableName);
        }
    }

    public synchronized <T extends UUTableDefinition> void bulkInsert(final Class<T> type, ArrayList<T> list)
    {
        SQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();
            db.beginTransaction();

            for (T row : list)
            {
                db.insert(row.getTableName(), null, row.getContentValues());
            }

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("bulkInsert", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Protected Helper Methods 
	///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Safely closes a cursor object
     * 
     * @param cursor the cursor to close
     */
    protected void closeCursor(final Cursor cursor)
    {
        try
        {
            if (cursor != null && !cursor.isClosed())
            {
            	cursor.close();
            }
        }
        catch (Exception ex)
        {
        	logException("closeCursor", ex);
        }
    }
    
    /**
     * Safely closes a database object
     * 
     * @param database the database to close
     */
    protected void closeDatabase(final SQLiteDatabase database)
    {
        try
        {
            if (database != null)
            {
                database.close();
            }
        }
        catch (Exception ex)
        {
            logException("closeDatabase", ex);
        }
    }
    
    /**
     * Delete's all records from a table
     */
    protected void truncateTable(final String tableName)
    {
    	SQLiteDatabase db;
                    
    	try
    	{
    		db = getReadWriteDatabase();
    		db.delete(tableName, null, null);
    	}
	    catch (Exception ex)
	    {
	    	logException("truncateTable", ex);
	    }
    }
    
    /**
     * Count of all records in a table
     */
    protected int numberOfRecordsInTable(final String tableName)
    {
    	return countRecordsInTable(tableName, null);
    }
    
    /**
     * Count of all records in a table matching the specified where clause.  If
     * the where clause is null then the query will count all rows in the table
     */
    protected synchronized int countRecordsInTable(final String tableName, final String where)
    {
        return countRecordsInTable(tableName, where, null);
    }

    /**
     * Count of all records in a table matching the specified where clause.  If
     * the where clause is null then the query will count all rows in the table
     */
    protected synchronized int countRecordsInTable(final String tableName, final String where, final String[] whereArgs)
    {
        SQLiteDatabase db;
        Cursor c = null;
        int count = 0;

        try
        {
            db = getReadOnlyDatabase();

            String sql = "SELECT COUNT(*) FROM " + tableName;
            if (where != null && where.length() > 0)
            {
                sql += " WHERE " + where;
            }

            logSql(sql);
            c = db.rawQuery(sql, whereArgs);

            if (c.moveToFirst())
            {
                count = c.getInt(0);
            }
        }
        catch (Exception ex)
        {
            logException("countRecordsInTable", ex);
        }
        finally
        {
            closeCursor(c);
        }

        return count;
    }
    
    /**
     * Inserts a new table row
     * 
     * @param tableName name of the table
     * @param cv list of columns to update
     * 
     * @return the row id that was updated
     */
    protected synchronized long insertRow(final String tableName, final ContentValues cv)
    {
        SQLiteDatabase db;
        long rowid;
        
        try
        {
            db = getReadWriteDatabase();
            rowid = insertRow(db, tableName, cv);
        }
        catch (Exception ex)
        {
        	logException("insertRow", ex);
        	rowid = -1;
        }
        
        return rowid;
    }
    
    /**
     * Inserts a new table row
     * 
     * @param db the database to use
     * @param tableName name of the table
     * @param cv list of columns to update
     * 
     * @return the row id that was updated
     */
    protected synchronized long insertRow(final SQLiteDatabase db, final String tableName, final ContentValues cv)
    {
        long rowid;
        
        try
        {
            //Log.d(LOG_TAG, cv.toString());
            rowid = db.insert(tableName, null, cv);
            //Log.d(LOG_TAG, "insertRow added rowid=" + rowid);
            
            if (rowid == -1)
            {
            	UULog.warn(getClass(), "insertRow", "DB insert returned -1, this indicates an error!");
            }
        }
        catch (Exception ex)
        {
        	logException("insertRow", ex);
        	rowid = -1;
        }
        
        return rowid;
    }
    
    /**
     * Inserts or Updates a table row
     * 
     * @param tableName name of the table
     * @param cv list of columns to update
     * 
     * @return the row id that was updated
     */
    protected synchronized long insertOrReplaceRow(final String tableName, final ContentValues cv)
    {
        SQLiteDatabase db;
        long rowid;
        
        try
        {
            db = getReadWriteDatabase();
            rowid = db.replace(tableName, null, cv);

            if (rowid == -1)
            {
                UULog.warn(getClass(), "insertOrReplaceRow", "DB replace returned -1, this indicates an error!");
            }
        }
        catch (Exception ex)
        {
        	logException("insertOrReplaceRow", ex);
        	rowid = -1;
        }
        
        return rowid;
    }
    
    /**
     * Inserts or Updates a table row
     * 
     * @param tableName name of the table
     * @param cv list of columns to update
     * @param whereClause the where clause
     * @param whereArgs args 
     * 
     */
    protected synchronized void updateRow(
    		final String tableName, 
    		final ContentValues cv,
    		final String whereClause,
    		final String[] whereArgs)
    {
        SQLiteDatabase db;

        try
        {
            db = getReadWriteDatabase();
            db.update(tableName, cv, whereClause, whereArgs);
        }
        catch (Exception ex)
        {
        	logException("updateRow", ex);
        }
    }
    
    /**
     * Executes some raw SQL
     * 
     * @param sql the sql statement
     */
    protected void execSql(final String sql)
    {
    	SQLiteDatabase db;
        
        try
        {
            db = getReadWriteDatabase();
            db.execSQL(sql);
        }
        catch (Exception ex)
        {
        	logException("execSql", ex);
        }
    }
    
    /**
     * Deletes a table row
     * 
     * @param tableName name of the table
     * @param whereClause WHERE clause to use when deleting
     * @param whereArgs bound arguments applied to ?'s in the whereClause
     */
    protected synchronized void delete(final String tableName, final String whereClause, final String[] whereArgs)
    {
    	SQLiteDatabase db = null;
        
        try
        {
            db = getReadWriteDatabase();                
            db.beginTransaction();
            db.delete(tableName, whereClause, whereArgs);
            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
        	logException("delete", ex);
        }
        finally
        {
        	safeEndTransaction(db);
        }
    }
    
    /**
     * Safely ends a DB transaction
     * @param db the database to end a transaction on
     */
    protected void safeEndTransaction(final SQLiteDatabase db)
    {
    	try
        {
    		if (db != null)
    		{
    			db.endTransaction();
    		}
        }
        catch (Exception ex)
        {
        	logException("safeEndTransaction", ex);
        }
    }
    
    /**
     * Logs a line of SQL
     * 
     * @param sql the sql statement to log
     */
    protected void logSql(final String sql)
    {
    	UULog.debug(getClass(), "logSql", sql);
    }
    
    /**
     * Logs an exception
     * 
     * @param methodName an explanatory message to go along with the exception
     * @param exception the caught exception
     */
    protected void logException(final String methodName, final Exception exception)
    {
        UULog.error(getClass(), methodName, exception);
    }

    /**
     * Formats a SQLite limit clause from an offset and limit number
     *
     * @param offset the offset
     * @param limit the limit
     * @return a string that can be passed as the limit param to android db calls
     */
    protected String formatLimitClause(final long offset, final long limit)
    {
        StringBuilder sb = new StringBuilder();
        if (offset != -1)
        {
            sb.append(offset);
        }

        if (sb.length() > 0)
        {
            sb.append(",");
            sb.append(limit);
        }
        else if (limit > 0)
        {
            sb.append(limit);
        }

        return sb.toString();
    }

    protected <T extends UUTableDefinition> void logTable(final Class<T> type)
    {
        SQLiteDatabase db;
        Cursor c = null;

        try
        {
            db = getReadOnlyDatabase();

            UUTableDefinition tableDef = type.newInstance();
            c = db.query(tableDef.getTableName(), tableDef.getColumnNames(), null, null, null, null, null, null);

            boolean first = true;
            while (c.moveToNext())
            {
                int columnCount = c.getColumnCount();
                StringBuilder sb = new StringBuilder();

                if (first)
                {
                    for (int i = 0; i < columnCount; i++)
                    {
                        String colName = c.getColumnName(i);
                        sb.append(colName);
                        sb.append(", ");
                    }

                    UULog.debug(getClass(), "logTable", sb.toString());
                }

                first = false;

                sb = new StringBuilder();

                for (int i = 0; i < columnCount; i++)
                {
                    int colType = c.getType(i);
                    switch (colType)
                    {
                        case Cursor.FIELD_TYPE_INTEGER:
                        {
                            long val = c.getLong(i);
                            sb.append(val);
                            break;
                        }

                        case Cursor.FIELD_TYPE_FLOAT:
                        {
                            double val = c.getDouble(i);
                            sb.append(val);
                            break;
                        }

                        case Cursor.FIELD_TYPE_STRING:
                        {
                            String val = c.getString(i);
                            sb.append(val);
                            break;
                        }

                        case Cursor.FIELD_TYPE_NULL:
                        {
                            sb.append("<null>");
                            break;
                        }

                        case Cursor.FIELD_TYPE_BLOB:
                        {
                            byte[] val = c.getBlob(i);
                            sb.append(UUString.byteToHex(val));
                            break;
                        }

                        default:
                        {
                            sb.append("? (" + colType + ") ");
                            break;
                        }
                    }

                    sb.append(", ");
                }

                UULog.debug(getClass(), "logTable", sb.toString());

            }
        }
        catch (Exception ex)
        {
            logException("logTable", ex);
        }
        finally
        {
            closeCursor(c);
        }
    }
}