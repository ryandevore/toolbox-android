package uu.toolbox.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Vector;

import uu.toolbox.logging.UULog;

@SuppressWarnings("unused")
public class UUDatabaseHelper extends SQLiteOpenHelper
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
				String sql = UUSql.buildCreateSql(tableDef);
				logSql(sql);
				db.execSQL(sql);
			}

            databaseDefinition.handlePostCreate(db);
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

        try
        {
            databaseDefinition.handlePostOpen(db);
        }
        catch (Exception ex)
        {
            logException("onOpen", ex);
        }
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        try
        {
            databaseDefinition.handleUpgrade(db, oldVersion, newVersion);
        }
        catch (Exception ex)
        {
            logException("onUpgrade", ex);
        }
    }
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Private Methods
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private void logSql(final String sql)
    {
		UULog.debug(getClass(), "logSql", sql);
    }
	
	private void logException(final String message, final Throwable throwable)
    {
    	UULog.error(getClass(), "logException", message, throwable);
    }
}