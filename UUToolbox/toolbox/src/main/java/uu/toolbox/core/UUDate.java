package uu.toolbox.core;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * UUDate
 * 
 * Useful Utilities - A set of extension methods for Date
 *  
 */
public class UUDate 
{
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Constants
	///////////////////////////////////////////////////////////////////////////////////////////////
	private static final String LOG_TAG = UUDate.class.getName();

	
	/**
	 * Create's a Date object filled with only an hour and minute
	 * 
	 * @param hourOfDay hour of day (0-23)
	 * @param minute minute of hour (0-59)
	 * @return a valid Date object
	 */
	public static Date fromHourAndMinute(final int hourOfDay, final int minute)
	{
		Calendar c = Calendar.getInstance(utcTimeZone());
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		return c.getTime();
	}
	
	/**
	 * Create's a Date object filled with only year, month, and day
	 * @param year the year
	 * @param month the month
	 * @param day the day
	 * @return a valid Date object
	 */
	public static Date fromYearMonthDay(final int year, final int month, final int day)
	{
		Calendar c = Calendar.getInstance(utcTimeZone());
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		return c.getTime();
	}
	
	/**
	 * Create's a Date object with the date set to today and a specific Hour and Minute pulled from another date object
	 * 
	 * @param date the date object to pull the hour and minute from
	 * @return a valid Date object
	 */
	public static Date todayWithHourAndMinute(final Date date)
	{
		Calendar c = Calendar.getInstance(utcTimeZone());
		c.setTime(date);
		
		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		c.setTime(today());
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}
	
	/**
	 * Create's a Date object with the time set to midnight and the day, month, year set from another date object
	 * @param date the other date object to pull the day, month, year from
	 * @return a valid date object
	 */
	public static Date dateWithMidnight(final Date date)
	{
		Calendar c = Calendar.getInstance(utcTimeZone());
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}
	
	/**
	 * Returns the current date
	 * 
	 * @return a valid Date object
	 */
	public static Date today()
	{
		Calendar c = Calendar.getInstance(utcTimeZone());
		c.setTime(new Date());
		return c.getTime();
	}
	
	/**
	 * Returns a date object representing one day from the current time
	 * @return a valid date object.
	 */
	public static Date tomorrow()
	{
		return daysFromToday(1);
	}
	
	/**
	 * Returns a date object representing a number of days from the current time.  Use a negavite value to indicate a time
	 * in the past
	 * @param days the days to offset
	 * @return a valid date object
	 */
	public static Date daysFromToday(final int days)
	{
		Calendar c = Calendar.getInstance(utcTimeZone());
		c.setTime(today());
		c.add(Calendar.DAY_OF_YEAR, days);
		return c.getTime();
	}
	
	/**
	 * Returns a date object representing a number of hours from the current time.  Use a negavite value to indicate a time
	 * in the past
	 * @param hours the hours to offset
	 * @return a valid date object
	 */
	public static Date hoursFromNow(final int hours)
	{
		Calendar c = Calendar.getInstance(utcTimeZone());
		c.setTime(today());
		c.add(Calendar.HOUR_OF_DAY, hours);
		return c.getTime();
	}
	
	/**
	 * Checks two dates to see if they are on the same day of the year
	 * 
	 * @param d1 the first date to check
	 * @param d2 the second date to check
	 * @return boolean if they are on the same day or not
	 */
	public static boolean areSameDay(final Date d1, final Date d2)
	{
		Calendar c1 = Calendar.getInstance();//utcTimeZone());
		c1.setTime(d1);
		
		Calendar c2 = Calendar.getInstance();//utcTimeZone());
		c2.setTime(d2);
		
		int y1 = c1.get(Calendar.YEAR);
		int day1 = c1.get(Calendar.DAY_OF_YEAR);
		
		int y2 = c2.get(Calendar.YEAR);
		int day2 = c2.get(Calendar.DAY_OF_YEAR);
		
		return (y1 == y2 && day1 == day2);
	}
	
	public static Date parseDate(final String string, final String formatter)
	{
		return parseDate(string, TimeZone.getDefault(), formatter);
	}

    public static Date parseDate(final String string, final String[] formatters)
    {
        return parseDate(string, TimeZone.getDefault(), formatters);
    }
	
	public static Date parseUtcDate(final String string, final String formatter)
	{
		return parseDate(string, utcTimeZone(), formatter);
	}

    public static Date parseUtcDate(final String string, final String[] formatters)
    {
        return parseDate(string, utcTimeZone(), formatters);
    }

	public static Date parseDate(final String string, final TimeZone timeZone, final String formatter)
	{
		try
		{
			SimpleDateFormat df = new SimpleDateFormat(formatter);
			df.setTimeZone(timeZone);
			return df.parse(string);
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "parseDate", ex);
		}

		return null;
	}

	public static Date parseDate(final String string, final TimeZone timeZone, final String[] formatters)
	{
		if (formatters != null)
		{
			for (String formatter : formatters)
			{
				Date val = parseDate(string, timeZone, formatter);
				if (val != null)
				{
					return val;
				}
			}
		}

		return null;
	}
	
	public static final String EXTENDED_FILE_NAME_FORMAT = "yyyy_MM_dd_HH_mm_ss";
	public static final String EXTENDED_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String MONTH_DAY_YEAR_FORMAT = "MMM dd yyyy";
	public static final String TIME_FORMAT = "hh:mm a";
	public static final String TIME_FORMAT_NO_AM_PM = "hh:mm";
	public static final String VERBOSE_DATE_FORMAT = "MMMM dd, yyyy";
	public static final String MONTH_DAY_FORMAT = "MMMM dd";
	public static final String DATE_TIME_FORMAT = "MM-dd-yyyy hh:mm a";
	
	public static String formatDate(final Long javaDate, final String formatter, final TimeZone timeZone)
	{
		if (javaDate != null)
		{
			Date d = new Date(javaDate.longValue());
			return formatDate(d, formatter, timeZone);
		}
		
		return null;
	}
	
	public static String formatDateTime(final Date date)
	{
		if (date != null)
		{
			return formatDate(date, DATE_TIME_FORMAT, TimeZone.getDefault());
		}
		
		return null;	
	}
	
	public static String formatUtcDateTime(final Date date)
	{
		if (date != null)
		{
			return formatDate(date, DATE_TIME_FORMAT, utcTimeZone());
		}
		
		return null;	
	}
	
	public static String formatDate(final Date date, final String formatter, final TimeZone timeZone)
	{
		if (date != null)
		{
			SimpleDateFormat df = new SimpleDateFormat(formatter);
			df.setTimeZone(timeZone);
			return df.format(date);
		}
		
		return null;
	}
	
	public static TimeZone utcTimeZone()
	{
		return TimeZone.getTimeZone("UTC");
	}
	
	public static String formatAsUtcTimestamp(final Date date)
	{
		if (date != null)
		{
			return formatDate(date, EXTENDED_DATE_FORMAT, utcTimeZone());
		}
		
		return null;
	}
	
	public static String formatMonthDayYear(final Date date)
	{
		return formatDate(date, MONTH_DAY_YEAR_FORMAT, TimeZone.getDefault());
	}
	
	public static String formatMonthDay(final Date date)
	{
		return formatDate(date, MONTH_DAY_FORMAT, TimeZone.getDefault());
	}
	
	public static String formatUtcMonthDay(final Date date)
	{
		return formatDate(date, MONTH_DAY_FORMAT, utcTimeZone());
	}
	
	public static String formatTime(final Date date)
	{
		return formatDate(date, TIME_FORMAT, TimeZone.getDefault());
	}
	
	public static String formatTimeNoAmPm(final Date date)
	{
		return formatDate(date, TIME_FORMAT_NO_AM_PM, TimeZone.getDefault());
	}
	
	public static String formatUtcTime(final Date date)
	{
		return formatDate(date, TIME_FORMAT, utcTimeZone());
	}
	
	public static String formatVerboseDate(final Date date)
	{
		return formatDate(date, VERBOSE_DATE_FORMAT, TimeZone.getDefault());
	}
	
	public static String formatExtendedJavaDate(final Long javaDate)
	{
		return formatDate(javaDate, EXTENDED_DATE_FORMAT, TimeZone.getDefault());
	}
	
	public static String formatExtendedDate(final Date date)
	{
		return formatDate(date, EXTENDED_DATE_FORMAT, TimeZone.getDefault());
	}
	
	public static String currentUtcTime()
	{
		return formatDate(new Date(), EXTENDED_DATE_FORMAT, utcTimeZone());
	}
	
	public static String currentTime()
	{
		return formatDate(new Date(), EXTENDED_DATE_FORMAT, TimeZone.getDefault());
	}
	
	public static String currentTimeInFileNameFormat()
	{
		SimpleDateFormat df = new SimpleDateFormat(EXTENDED_FILE_NAME_FORMAT);
		Date d = new Date();
		return df.format(d);
	}
}
