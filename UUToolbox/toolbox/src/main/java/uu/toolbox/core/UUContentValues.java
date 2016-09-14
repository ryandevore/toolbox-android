package uu.toolbox.core;

import android.content.ContentValues;

/**
 * UUContentValues
 * 
 * Useful Utilities - A static wrapper around the ContentValues class
 *  
 */
public final class UUContentValues 
{
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Public Static Methods
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Sets a value if both the key and value are not null
	 * 
	 * @param cv destination content values
	 * @param key the key
	 * @param value the value
	 */
	public static final void putIfNotNull(final ContentValues cv, final String key, final String value)
	{
		if (cv != null && key != null && value != null)
		{
			cv.put(key, value);
		}
	}
	
	/**
	 * Sets a value if both the key and value are not null
	 * 
	 * @param cv destination content values
	 * @param key the key
	 * @param value the value
	 */
	public static final void putIfNotNull(final ContentValues cv, final String key, final Long value)
	{
		if (cv != null && key != null && value != null)
		{
			cv.put(key, value);
		}
	}
	
	/**
	 * Sets a value if both the key and value are not null
	 * 
	 * @param cv destination content values
	 * @param key the key
	 * @param value the value
	 */
	public static final void putIfNotNull(final ContentValues cv, final String key, final Integer value)
	{
		if (cv != null && key != null && value != null)
		{
			cv.put(key, value);
		}
	}
	
	/**
	 * Sets a value if both the key and value are not null
	 * 
	 * @param cv destination content values
	 * @param key the key
	 * @param value the value
	 */
	public static final void putIfNotNull(final ContentValues cv, final String key, final Short value)
	{
		if (cv != null && key != null && value != null)
		{
			cv.put(key, value);
		}
	}
	
	/**
	 * Sets a value if both the key and value are not null
	 * 
	 * @param cv destination content values
	 * @param key the key
	 * @param value the value
	 */
	public static final void putIfNotNull(final ContentValues cv, final String key, final Byte value)
	{
		if (cv != null && key != null && value != null)
		{
			cv.put(key, value);
		}
	}
	
	/**
	 * Sets a value if both the key and value are not null
	 * 
	 * @param cv destination content values
	 * @param key the key
	 * @param value the value
	 */
	public static final void putIfNotNull(final ContentValues cv, final String key, final Boolean value)
	{
		if (cv != null && key != null && value != null)
		{
			cv.put(key, value);
		}
	}
}
