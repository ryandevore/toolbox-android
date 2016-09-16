package uu.toolboxapp.data.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import uu.toolbox.core.UUDate;
import uu.toolbox.core.UUJson;
import uu.toolbox.core.UUJsonConvertible;
import uu.toolbox.data.UUContentValues;
import uu.toolbox.data.UUCursor;
import uu.toolbox.data.UUDataModel;
import uu.toolbox.data.UUSql;

/**
 * Created by ryandevore on 9/14/16.
 */
public class WeatherSummary
        implements UUJsonConvertible, UUDataModel
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDataModel Constants
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String TABLE_NAME = "weather_summary";

    public static final String ID_COLUMN = "id";
    public static final String CITY_COLUMN = "city";
    public static final String SUNRISE_COLUMN = "sunrise";
    public static final String SUNSET_COLUMN = "sunset";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LONGITUDE_COLUMN = "longitude";
    public static final String WEATHER_MAIN_COLUMN = "weather_main";
    public static final String WEATHER_DESCRIPTION_COLUMN = "weather_description";
    public static final String WEATHER_ICON_COLUMN = "weather_icon";
    public static final String TEMPERATURE_COLUMN = "temperature";
    public static final String PRESSURE_COLUMN = "pressure";
    public static final String HUMIDITY_COLUMN = "humidity";
    public static final String MIN_TEMPERATURE_COLUMN = "min_temperature";
    public static final String MAX_TEMPERATURE_COLUMN = "max_temperature";
    public static final String WIND_SPEED_COLUMN = "wind_speed";
    public static final String WIND_DIRECTION_COLUMN = "wind_direction";
    public static final String VISIBILITY_COLUMN = "visibility";
    public static final String TIMESTAMP_COLUMN = "timestamp";
    public static final String CLOUD_PERCENT_COLUMN = "cloud_percent";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Data elements
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Long id;
    private String city;
    private Long sunrise;
    private Long sunset;
    private Double latitude;
    private Double longitude;
    private String weatherMain;
    private String weatherDescription;
    private String weatherIcon;
    private Float temperature;
    private Integer pressure;
    private Integer humidity;
    private Float minTemperature;
    private Float maxTemperature;
    private Float windSpeed;
    private Float windDirection;
    private Integer visibility;
    private Long timestamp;
    private Float cloudPercent;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Accessors
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public Long getSunrise()
    {
        return sunrise;
    }

    public void setSunrise(Long sunrise)
    {
        this.sunrise = sunrise;
    }

    public Long getSunset()
    {
        return sunset;
    }

    public void setSunset(Long sunset)
    {
        this.sunset = sunset;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

    public String getWeatherMain()
    {
        return weatherMain;
    }

    public void setWeatherMain(String weatherMain)
    {
        this.weatherMain = weatherMain;
    }

    public String getWeatherDescription()
    {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription)
    {
        this.weatherDescription = weatherDescription;
    }

    public String getWeatherIcon()
    {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon)
    {
        this.weatherIcon = weatherIcon;
    }

    public Float getTemperature()
    {
        return temperature;
    }

    public void setTemperature(Float temperature)
    {
        this.temperature = temperature;
    }

    public Integer getPressure()
    {
        return pressure;
    }

    public void setPressure(Integer pressure)
    {
        this.pressure = pressure;
    }

    public Integer getHumidity()
    {
        return humidity;
    }

    public void setHumidity(Integer humidity)
    {
        this.humidity = humidity;
    }

    public Float getMinTemperature()
    {
        return minTemperature;
    }

    public void setMinTemperature(Float minTemperature)
    {
        this.minTemperature = minTemperature;
    }

    public Float getMaxTemperature()
    {
        return maxTemperature;
    }

    public void setMaxTemperature(Float maxTemperature)
    {
        this.maxTemperature = maxTemperature;
    }

    public Float getWindSpeed()
    {
        return windSpeed;
    }

    public void setWindSpeed(Float windSpeed)
    {
        this.windSpeed = windSpeed;
    }

    public Float getWindDirection()
    {
        return windDirection;
    }

    public void setWindDirection(Float windDirection)
    {
        this.windDirection = windDirection;
    }

    public Integer getVisibility()
    {
        return visibility;
    }

    public void setVisibility(Integer visibility)
    {
        this.visibility = visibility;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    public Float getCloudPercent()
    {
        return cloudPercent;
    }

    public void setCloudPercent(Float cloudPercent)
    {
        this.cloudPercent = cloudPercent;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUJsonConvertible
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void fillFromJson(final Context context, final JSONObject json)
    {
        setId(null);

        JSONObject coord = UUJson.safeGetJsonObject(json, "coord");
        setLatitude(UUJson.safeGetDouble(coord, "lat"));
        setLongitude(UUJson.safeGetDouble(coord, "lon"));

        JSONObject weather = null;
        JSONArray weatherArray = UUJson.safeGetJsonArray(json, "weather");
        if (weatherArray != null)
        {
            Object weatherTmp = UUJson.safeGetJsonObject(weatherArray, 0);
            if (weatherTmp != null && weatherTmp instanceof JSONObject)
            {
                weather = (JSONObject)weatherTmp;
            }
        }

        setWeatherMain(UUJson.safeGetString(weather, "main"));
        setWeatherDescription(UUJson.safeGetString(weather, "description"));
        setWeatherIcon(UUJson.safeGetString(weather, "icon"));

        JSONObject main = UUJson.safeGetJsonObject(json, "main");
        setTemperature(UUJson.safeGetFloat(main, "temp"));
        setPressure(UUJson.safeGetInt(main, "pressure"));
        setHumidity(UUJson.safeGetInt(main, "humidity"));
        setMinTemperature(UUJson.safeGetFloat(main, "temp_min"));
        setMaxTemperature(UUJson.safeGetFloat(main, "temp_max"));

        setVisibility(UUJson.safeGetInt(json, "visibility"));

        JSONObject wind = UUJson.safeGetJsonObject(json, "wind");
        setWindSpeed(UUJson.safeGetFloat(wind, "speed"));
        setWindDirection(UUJson.safeGetFloat(wind, "deg"));

        JSONObject cloud = UUJson.safeGetJsonObject(json, "cloud");
        setCloudPercent(UUJson.safeGetFloat(cloud, "all"));

        setTimestamp(UUJson.safeGetLong(json, "dt") * 1000);
        setCity(UUJson.safeGetString(json, "name"));

        JSONObject sys = UUJson.safeGetJsonObject(json, "sys");
        setSunrise(UUJson.safeGetLong(sys, "sunrise") * 1000);
        setSunset(UUJson.safeGetLong(sys, "sunset") * 1000);
    }

    @Override
    public JSONObject toJsonObject()
    {
        // Return null because we only care about deserializing from JSON
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // System.Object Overrides
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        try
        {
            return String.format(Locale.getDefault(), "city: %s, sunrise: %s, sunset: %s, temp: %f, minTemp: %f, maxTemp: %f, clouds: %f%%, pressure: %d, humidity: %d, visibility: %d, main: %s, description: %s, icon: %s, lat: %f, lng: %f, timestamp: %s",
                    getCity(),
                    UUDate.formatExtendedDate(new Date(getSunrise())),
                    UUDate.formatExtendedDate(new Date(getSunset())),
                    getTemperature(),
                    getMinTemperature(),
                    getMaxTemperature(),
                    getCloudPercent(),
                    getPressure(),
                    getHumidity(),
                    getVisibility(),
                    getWeatherMain(),
                    getWeatherDescription(),
                    getWeatherIcon(),
                    getLatitude(),
                    getLongitude(),
                    UUDate.formatExtendedDate(new Date(getTimestamp())));
        }
        catch (Exception ex)
        {
            return super.toString();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDataModel Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @NonNull
    @Override
    public HashMap<String, String> getColumnMap()
    {
        HashMap<String, String> map = new HashMap<>();

        map.put(ID_COLUMN, UUSql.INTEGER_PRIMARY_KEY_AUTO_INCREMENT_TYPE);
        map.put(CITY_COLUMN, UUSql.TEXT_COLUMN_TYPE);
        map.put(SUNRISE_COLUMN, UUSql.INT64_COLUMN_TYPE);
        map.put(SUNSET_COLUMN, UUSql.INT64_COLUMN_TYPE);
        map.put(LATITUDE_COLUMN, UUSql.REAL_COLUMN_TYPE);
        map.put(LONGITUDE_COLUMN, UUSql.REAL_COLUMN_TYPE);
        map.put(WEATHER_MAIN_COLUMN, UUSql.TEXT_COLUMN_TYPE);
        map.put(WEATHER_DESCRIPTION_COLUMN, UUSql.TEXT_COLUMN_TYPE);
        map.put(WEATHER_ICON_COLUMN, UUSql.TEXT_COLUMN_TYPE);
        map.put(TEMPERATURE_COLUMN, UUSql.REAL_COLUMN_TYPE);
        map.put(PRESSURE_COLUMN, UUSql.INT32_COLUMN_TYPE);
        map.put(HUMIDITY_COLUMN, UUSql.INT32_COLUMN_TYPE);
        map.put(MIN_TEMPERATURE_COLUMN, UUSql.REAL_COLUMN_TYPE);
        map.put(MAX_TEMPERATURE_COLUMN, UUSql.REAL_COLUMN_TYPE);
        map.put(WIND_SPEED_COLUMN, UUSql.REAL_COLUMN_TYPE);
        map.put(WIND_DIRECTION_COLUMN, UUSql.REAL_COLUMN_TYPE);
        map.put(VISIBILITY_COLUMN, UUSql.INT32_COLUMN_TYPE);
        map.put(TIMESTAMP_COLUMN, UUSql.INT64_COLUMN_TYPE);
        map.put(CLOUD_PERCENT_COLUMN, UUSql.REAL_COLUMN_TYPE);

        return map;
    }

    @Override
    public String getPrimaryKeyColumnName()
    {
        // Return null because ID column is defined as auto increment.
        return null;
    }

    @Override
    @NonNull
    public String getPrimaryKeyWhereClause()
    {
        return String.format(Locale.US, "%s = ?", ID_COLUMN);
    }

    @NonNull
    public String getPrimaryKey()
    {
        return String.valueOf(id);
    }

    public ContentValues getContentValues()
    {
        ContentValues cv = new ContentValues();

        UUContentValues.putIfNotNull(cv, ID_COLUMN, getId());
        UUContentValues.putIfNotNull(cv, CITY_COLUMN, getCity());
        UUContentValues.putIfNotNull(cv, SUNRISE_COLUMN, getSunrise());
        UUContentValues.putIfNotNull(cv, SUNSET_COLUMN, getSunset());
        UUContentValues.putIfNotNull(cv, LATITUDE_COLUMN, getLatitude());
        UUContentValues.putIfNotNull(cv, LONGITUDE_COLUMN, getLongitude());
        UUContentValues.putIfNotNull(cv, WEATHER_MAIN_COLUMN, getWeatherMain());
        UUContentValues.putIfNotNull(cv, WEATHER_DESCRIPTION_COLUMN, getWeatherDescription());
        UUContentValues.putIfNotNull(cv, WEATHER_ICON_COLUMN, getWeatherIcon());
        UUContentValues.putIfNotNull(cv, TEMPERATURE_COLUMN, getTemperature());
        UUContentValues.putIfNotNull(cv, PRESSURE_COLUMN, getPressure());
        UUContentValues.putIfNotNull(cv, HUMIDITY_COLUMN, getHumidity());
        UUContentValues.putIfNotNull(cv, MIN_TEMPERATURE_COLUMN, getMinTemperature());
        UUContentValues.putIfNotNull(cv, MAX_TEMPERATURE_COLUMN, getMaxTemperature());
        UUContentValues.putIfNotNull(cv, WIND_SPEED_COLUMN, getWindSpeed());
        UUContentValues.putIfNotNull(cv, WIND_DIRECTION_COLUMN, getWindDirection());
        UUContentValues.putIfNotNull(cv, VISIBILITY_COLUMN, getVisibility());
        UUContentValues.putIfNotNull(cv, TIMESTAMP_COLUMN, getTimestamp());
        UUContentValues.putIfNotNull(cv, CLOUD_PERCENT_COLUMN, getCloudPercent());

        return cv;
    }

    public void fillFromCursor(final Cursor cursor)
    {
        setId(UUCursor.safeGetLong(cursor, ID_COLUMN));
        setCity(UUCursor.safeGetString(cursor, CITY_COLUMN));
        setSunrise(UUCursor.safeGetLong(cursor, SUNRISE_COLUMN));
        setSunset(UUCursor.safeGetLong(cursor, SUNSET_COLUMN));
        setLatitude(UUCursor.safeGetDouble(cursor, LATITUDE_COLUMN));
        setLongitude(UUCursor.safeGetDouble(cursor, LONGITUDE_COLUMN));
        setWeatherMain(UUCursor.safeGetString(cursor, WEATHER_MAIN_COLUMN));
        setWeatherDescription(UUCursor.safeGetString(cursor, WEATHER_DESCRIPTION_COLUMN));
        setWeatherIcon(UUCursor.safeGetString(cursor, WEATHER_ICON_COLUMN));
        setTemperature(UUCursor.safeGetFloat(cursor, TEMPERATURE_COLUMN));
        setPressure(UUCursor.safeGetInt(cursor, PRESSURE_COLUMN));
        setHumidity(UUCursor.safeGetInt(cursor, HUMIDITY_COLUMN));
        setMinTemperature(UUCursor.safeGetFloat(cursor, MIN_TEMPERATURE_COLUMN));
        setMaxTemperature(UUCursor.safeGetFloat(cursor, MAX_TEMPERATURE_COLUMN));
        setWindSpeed(UUCursor.safeGetFloat(cursor, WIND_SPEED_COLUMN));
        setWindDirection(UUCursor.safeGetFloat(cursor, WIND_DIRECTION_COLUMN));
        setVisibility(UUCursor.safeGetInt(cursor, VISIBILITY_COLUMN));
        setTimestamp(UUCursor.safeGetLong(cursor, TIMESTAMP_COLUMN));
        setCloudPercent(UUCursor.safeGetFloat(cursor, CLOUD_PERCENT_COLUMN));
    }
}
