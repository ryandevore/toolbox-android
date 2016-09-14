package uu.toolbox.data;

import android.database.sqlite.SQLiteDatabase;

import java.util.Vector;

/**
 *
 * Interface for defining a SQL database definition
 */
public interface UUDatabaseDefinition
{
	String getDatabaseName();
	int getVersion();

	Vector<UUTableDefinition> getTableDefinitions();

	void handlePostOpen(SQLiteDatabase db);
	void handlePostCreate(SQLiteDatabase db);
	void handleUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}