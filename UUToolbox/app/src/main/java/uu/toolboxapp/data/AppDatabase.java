package uu.toolboxapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import uu.toolbox.data.UUDataModel;
import uu.toolbox.data.UUDatabase;
import uu.toolbox.logging.UULog;
import uu.toolboxapp.data.models.WeatherSummary;

/**
 * Sample usage of UUDatabase
 */
public class AppDatabase extends UUDatabase
{
    private static AppDatabase theSharedDatabase;

    private static final String DB_NAME = "UUSampleDb";
    private static final int DB_VERSION_ONE = 1;

    private static final int DB_VERSION = DB_VERSION_ONE;

    public static AppDatabase sharedInstance()
    {
        return theSharedDatabase;
    }

    public static void init(final Context context)
    {
        try
        {
            theSharedDatabase = new AppDatabase(context);
        }
        catch (Exception ex)
        {
            UULog.error(UUDatabase.class, "init", ex);

            theSharedDatabase = null;
        }
    }

    public AppDatabase(@NonNull final Context context)
    {
        super(context);
    }

    public void addWeatherSummary(final WeatherSummary summary)
    {
        updateObject(WeatherSummary.class, summary);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDatabaseDefinition
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String getDatabaseName()
    {
        return DB_NAME;
    }

    public int getVersion()
    {
        return DB_VERSION;
    }

    public ArrayList<UUDataModel> getDataModels(int version)
    {
        switch (version)
        {
            case DB_VERSION_ONE:
            {
                return getV1DataModels();
            }

            default:
            {
                return null;
            }
        }
    }

    //@Override
    public ArrayList<String> getSqlCreateLines(int version)
    {
        return null;
    }

    public void handlePostOpen(SQLiteDatabase db, int version)
    {

    }

    public void handlePostCreate(SQLiteDatabase db, int version)
    {

    }

    public void handlePostUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    private ArrayList<UUDataModel> getV1DataModels()
    {
        ArrayList<UUDataModel> list = new ArrayList<>();
        list.add(new WeatherSummary());
        return list;
    }
}
