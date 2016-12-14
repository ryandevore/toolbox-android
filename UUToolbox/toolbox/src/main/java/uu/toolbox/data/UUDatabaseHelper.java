package uu.toolbox.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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
            int version = databaseDefinition.getVersion();
            ArrayList<String> lines = buildCreateLines(version);

            db.beginTransaction();

            for (String line : lines)
            {
                logSql(line);
                db.execSQL(line);
            }

            db.setTransactionSuccessful();

            databaseDefinition.handlePostCreate(db);
		}
		catch (Exception ex)
		{
			logException("onCreate", ex);
		}
        finally
        {
            safeEndTransaction(db);
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

    private ArrayList<String> buildCreateLines(final int version)
    {
        ArrayList<String> list = new ArrayList<>();

        ArrayList<UUDataModel> tableDefs = databaseDefinition.getDataModels(version);
        if (tableDefs != null)
        {
            for (UUDataModel dataModel : tableDefs)
            {
                list.add(UUSql.buildCreateSql(dataModel));
            }
        }

        ArrayList<String> rawLines = databaseDefinition.getSqlCreateLines(version);
        if (rawLines != null)
        {
            list.addAll(rawLines);
        }

        return list;
    }
}