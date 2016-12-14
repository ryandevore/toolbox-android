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
	ArrayList<String> getSqlCreateLines(int version);

	void handlePostOpen(SQLiteDatabase db);
	void handlePostCreate(SQLiteDatabase db);
	void handleUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}