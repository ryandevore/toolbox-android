package uu.framework.data;

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
}