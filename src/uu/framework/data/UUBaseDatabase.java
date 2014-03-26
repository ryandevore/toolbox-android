package uu.framework.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;

import uu.framework.core.UUTools;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

/**
 * UUBaseDatabase
 * 
 * Useful Utilities - A simple base class for Android SQL Lite databases.
 *  
 */
public abstract class UUBaseDatabase implements UUDatabaseDefinition
{
	///////////////////////////////////////////////////////////////////////////////////////////////
    // Member Variables 
	///////////////////////////////////////////////////////////////////////////////////////////////
	protected static Context theContext;
	protected static SQLiteOpenHelper theDatabaseHelper;
	protected final String LOG_TAG;
	
	public static final void init(final Context context)
	{
		theContext = context;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
    // Construction 
	///////////////////////////////////////////////////////////////////////////////////////////////
	protected UUBaseDatabase()
	{
		LOG_TAG = getClass().getName();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods 
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	protected SQLiteOpenHelper sharedSqlLiteHelper()
	{
		if (theDatabaseHelper == null)
		{
			theDatabaseHelper = new UUDatabaseHelper(theContext, this);
		}
		
		return theDatabaseHelper;
	}
	
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
		return sharedSqlLiteHelper().getReadableDatabase();
	}
	
	/**
	 * Returns a read write copy of the database
	 * 
	 * @return a SQLiteDatabase instance
	 */
	public SQLiteDatabase getReadWriteDatabase()
	{
		return sharedSqlLiteHelper().getWritableDatabase();
	}
	
	/**
	 * Copies this database file to another location
	 * 
	 * @param destinationPath the destination path
	 * 
	 * NOTE: This is a developer only debug feature.  Normal app usage should not really need it :-)
	 * 
	 */
	public void exportDatabase()
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try
		{
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			{
				String destinationPath = Environment.getExternalStorageDirectory() + "/" + getDatabaseName() + ".sqlite";
				
				fis = new FileInputStream(getReadOnlyDatabase().getPath());
				fos = new FileOutputStream(destinationPath);
				
				int chunkSize = 10240;
				byte[] buffer = new byte[chunkSize];
				
				int read = 0;
				
				while (true)
				{
					read = fis.read(buffer, 0, buffer.length);
					if (read == -1)
					{
						break;
					}
					
					fos.write(buffer, 0, read);
				}
				
				fos.flush();
			}
			else
			{
				Log.d(LOG_TAG, "External media is not mounted, cannot export db");
			}
		}
		catch (Exception ex)
		{
			Log.d(LOG_TAG, "Error exporting database: ", ex);
		}
		finally
		{
			UUTools.closeStream(fis);
			UUTools.closeStream(fos);
		}
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
    	ArrayList<T> results = new ArrayList<T>();
    	
    	SQLiteDatabase db = null;
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
     * @param selection where clause
     * @param selectionArgs bound where arguments
     * @param orderBy order by clause
     * @param limit limit clause
     * @return a List of 
     */
    public synchronized <T extends UUTableDefinition> ArrayList<T> rawQueryMultipleObjects(final Class<T> type, final String rawSqlQuery, final String[] selectionArgs)
    {
    	ArrayList<T> results = new ArrayList<T>();
    	
    	SQLiteDatabase db = null;
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
     * @param limit limit clause
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
     * @param tableName the table to search
     * @param sql the sql to run
     * @return a result
     */
    public synchronized String querySingleStringCell(final String sql, final String defaultValue)
    {
    	SQLiteDatabase db = null;
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
     * @param tableName the table to search
     * @param sql the sql to run
     * @return a result
     */
    public synchronized int querySingleIntCell(final String sql, final int defaultValue)
    {
    	SQLiteDatabase db = null;
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
     * Runs a query expecting a single float cell as the result
     * 
     * @param tableName the table to search
     * @param sql the sql to run
     * @return a result
     */
    public synchronized float querySingleFloatCell(final String sql, final float defaultValue)
    {
    	SQLiteDatabase db = null;
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
     * Inserts an object
     * 
     * @param type row object type
     * @param object the object to update
     * @return
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
     * @return
     */
    public synchronized <T extends UUTableDefinition> T updateObject(final Class<T> type, T object)
    {
    	String whereClause = object.getPrimaryKeyWhereClause();
    	String[] whereArgs = object.getPrimaryKeyArgs();
    	updateRow(object.getTableName(), object.getContentValues(), whereClause, whereArgs);
    	return querySingleObject(type, whereClause, whereArgs, null);
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
    	SQLiteDatabase db = null;
                    
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
    	SQLiteDatabase db = null;
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
    		c = db.rawQuery(sql, null);
    		
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
        SQLiteDatabase db = null;
        long rowid = -1;
        
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
        long rowid = -1;
        
        try
        {
            //Log.d(LOG_TAG, cv.toString());
            rowid = db.insert(tableName, null, cv);
            //Log.d(LOG_TAG, "insertRow added rowid=" + rowid);
            
            if (rowid == -1)
            {
            	Log.w(LOG_TAG, "DB insert returned -1, this indicates an error!");
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
        SQLiteDatabase db = null;
        Cursor c = null;
        long rowid = -1;
        
        try
        {
            db = getReadWriteDatabase();
            //Log.d(LOG_TAG, cv.toString());
            rowid = db.replace(tableName, null, cv);
            //Log.d(LOG_TAG, "insertOrReplaceRow updated rowid=" + rowid);
            
            if (rowid == -1)
            {
            	Log.w(LOG_TAG, "DB replace returned -1, this indicates an error!");
            }
        }
        catch (Exception ex)
        {
        	logException("insertOrReplaceRow", ex);
        	rowid = -1;
        }
        finally
        {
            closeCursor(c);
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
        SQLiteDatabase db = null;
        Cursor c = null;
        
        try
        {
            db = getReadWriteDatabase();
            db.update(tableName, cv, whereClause, whereArgs);
        }
        catch (Exception ex)
        {
        	logException("updateRow", ex);
        }
        finally
        {
            closeCursor(c);
        }
    }
    
    /**
     * Executes some raw SQL
     * 
     * @param sql the sql statement
     */
    protected void execSql(final String sql)
    {
    	SQLiteDatabase db = null;
        
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
     * @param db
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
    	//Log.d(LOG_TAG, sql);
    }
    
    /**
     * Logs an exception
     * 
     * @param message an explanatory message to go along with the exception
     * @param throwable the caught exception
     */
    protected void logException(final String message, final Throwable throwable)
    {
    	Log.e(LOG_TAG, message, throwable);
    }
}

class UUDatabaseHelper extends SQLiteOpenHelper 
{
	private UUDatabaseDefinition databaseDefinition;
	
	public UUDatabaseHelper(final Context context, final UUDatabaseDefinition definition) 
	{
	    super(context, definition.getDatabaseName(), null, definition.getVersion());
	    databaseDefinition = definition;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// SQLiteOpenHelper Methods
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		try
		{
			Vector<UUTableDefinition> tableDefs = databaseDefinition.getTableDefinitions();
			int count = tableDefs.size();
			
			for (int i = 0; i < count; i++)
			{
				UUTableDefinition tableDef = tableDefs.elementAt(i);
				String sql = buildCreateSql(tableDef);
				logSql(sql);
				db.execSQL(sql);
			}
		}
		catch (Exception ex)
		{
			logException("onCreate", ex);
		}
	}
	
	@Override
	public void onOpen(SQLiteDatabase db)
	{
	    super.onOpen(db);
	}
	    
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		Log.w(getClass().getName(), "Database upgrade not supported");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Private Methods
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private void logSql(final String sql)
    {
		Log.d(getClass().getName(), sql);
    }
	
	private void logException(final String message, final Throwable throwable)
    {
    	Log.e(getClass().getName(), message, throwable);
    }
	
	private String buildCreateSql(final UUTableDefinition tableDef)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("CREATE TABLE " + tableDef.getTableName() + " (");
		
		String[] columns = tableDef.getColumnNames();
		String[] dataTypes = tableDef.getColumnDataTypes();
		
		if (columns.length == dataTypes.length)
		{
			int count = columns.length;
			
			for (int i = 0; i < count; i++)
			{
				sb.append(columns[i] + " " + dataTypes[i]);
				
				if (i < (count - 1))
				{
					sb.append(", ");
				}
			}
			
			String primaryKey = tableDef.getPrimaryKeyColumnName();
			if (primaryKey != null)
			{
				sb.append(", PRIMARY KEY(" + primaryKey + ")");
			}
		}
		else
		{
			throw new RuntimeException("Column Names and Datatypes do not match!");
		}
		
		sb.append(");");
		
		return sb.toString();
		
	}
}  