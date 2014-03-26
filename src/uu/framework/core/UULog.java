package uu.framework.core;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

abstract public class UULog 
{
	private static final String LOG_TAG = UULog.class.getName();
	
	public static final void logScreenMetrics(final Activity activity)
	{
		try
		{
			Configuration cfg = activity.getResources().getConfiguration();
			
			String screenLayoutDescription = "unknown";
			int screenLayout = cfg.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
			switch (screenLayout)
			{
				case Configuration.SCREENLAYOUT_SIZE_SMALL:
					screenLayoutDescription = "small";
					break;
					
				case Configuration.SCREENLAYOUT_SIZE_NORMAL:
					screenLayoutDescription = "normal";
					break;
					
				case Configuration.SCREENLAYOUT_SIZE_LARGE:
					screenLayoutDescription = "large";
					break;
					
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
					screenLayoutDescription = "xlarge";
					break;
					
				case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
					screenLayoutDescription = "undefined";
					break;
					
				default:
					screenLayoutDescription = "unknown";
					break;
			}
			
			Log.d(LOG_TAG, "Screen Size: " + screenLayout + " (" + screenLayoutDescription + ")");
			
			
			int screenLayoutLong = cfg.screenLayout & Configuration.SCREENLAYOUT_LONG_MASK;
			String screenLayoutLongDescription = "unknown";
			switch (screenLayoutLong)
			{
				case Configuration.SCREENLAYOUT_LONG_NO:
					screenLayoutLongDescription = "no";
					break;
					
				case Configuration.SCREENLAYOUT_LONG_YES:
					screenLayoutLongDescription = "yes";
					break;
					
				case Configuration.SCREENLAYOUT_LONG_UNDEFINED:
					screenLayoutLongDescription = "undefined";
					break;
					
				default:
					screenLayoutLongDescription = "unknown";
					break;
			}
			
			Log.d(LOG_TAG, "Screen Layout Long:, " + screenLayoutLong + " (" + screenLayoutLongDescription + ")");
			
			String orientationDescription = "unknown";
			switch (cfg.orientation)
			{
				case Configuration.ORIENTATION_PORTRAIT:
					orientationDescription = "portrait";
					break;
					
				case Configuration.ORIENTATION_LANDSCAPE:
					orientationDescription = "landscape";
					break;
					
				case Configuration.ORIENTATION_UNDEFINED:
					orientationDescription = "undefined";
					break;
					
				default:
					orientationDescription = "unknown";
					break;
			}
			
			Log.d(LOG_TAG, "Screen Orientation:, " + cfg.orientation + " (" + orientationDescription + ")");
			
			DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
			
			String dpiDescription = "unknown";
			switch (metrics.densityDpi)
			{	
				case DisplayMetrics.DENSITY_LOW:
					dpiDescription = "low";
					break;
					
				case DisplayMetrics.DENSITY_MEDIUM:
					dpiDescription = "medium";
					break;
					
				case DisplayMetrics.DENSITY_HIGH:
					dpiDescription = "high";
					break;
					
				case DisplayMetrics.DENSITY_XHIGH:
					dpiDescription = "xhigh";
					break;
					
				case DisplayMetrics.DENSITY_XXHIGH:
					dpiDescription = "xxhigh";
					break;
					
				case DisplayMetrics.DENSITY_TV:
					dpiDescription = "tv";
					break;
					
				default:
					dpiDescription = "unknownt";
					break;
			}
			
			Log.d(LOG_TAG, "DisplayMetrics, density: " + metrics.density);
			Log.d(LOG_TAG, "DisplayMetrics, densityDpi: " + metrics.densityDpi + " (" + dpiDescription + ")");
			Log.d(LOG_TAG, "DisplayMetrics, heightPixels: " + metrics.heightPixels);
			Log.d(LOG_TAG, "DisplayMetrics, scaledDensity: " + metrics.scaledDensity);
			Log.d(LOG_TAG, "DisplayMetrics, widthPixels: " + metrics.widthPixels);
			Log.d(LOG_TAG, "DisplayMetrics, xdpi: " + metrics.xdpi);
			Log.d(LOG_TAG, "DisplayMetrics, ydpi: " + metrics.ydpi);
			
		}
		catch (Exception ex)
		{
			// Eat it
			Log.e(LOG_TAG, "Eating exception trying to log screen metrics", ex);
		}
	}

}
