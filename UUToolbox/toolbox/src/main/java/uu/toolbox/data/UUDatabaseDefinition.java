package uu.toolbox.data;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 *
 * Interface for defining a SQL database definition
 */
public interface UUDatabaseDefinition
{
	String getDatabaseName();
	int getVersion();

	ArrayList<UUDataModel> getDataModels(int version);

	void handlePostOpen(SQLiteDatabase db, int version);
	void handlePostCreate(SQLiteDatabase db, int version);
	void handlePostUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}