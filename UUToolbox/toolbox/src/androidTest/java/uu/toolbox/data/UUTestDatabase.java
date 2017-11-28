package uu.toolbox.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class UUTestDatabase extends UUDatabase
{
    public UUTestDatabase(@NonNull final Context context)
    {
        super(context);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDatabaseDefinition
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String getDatabaseName()
    {
        return "uu_test_db";
    }

    public int getVersion()
    {
        return 1;
    }

    public ArrayList<UUDataModel> getDataModels(int version)
    {
        ArrayList<UUDataModel> list = new ArrayList<>();
        list.add(new UUDataModelWithCompoundKey());
        return list;
    }

    @Override
    public ArrayList<String> getSqlCreateLines(int version)
    {
        return null;
    }

    public void handlePostOpen(SQLiteDatabase db)
    {

    }

    public void handlePostCreate(SQLiteDatabase db)
    {

    }

    public void handleUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
