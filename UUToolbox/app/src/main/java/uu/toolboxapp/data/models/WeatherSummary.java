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
import uu.toolbox.data.UUSqlColumnType;

/**
 * Created by ryandevore on 9/14/16.
 */
public class WeatherSummary
        implements UUJsonConvertible, UUDataModel
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static String TableName = "weather_summary";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Enums
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private enum Columns
    {
        id,
        city,
        sunrise,
        sunset,
        latitude,
        longitude,
        weather_main,
        weather_description,
        weather_icon,
        temperature,
        pressure,
        humidity,
        min_temperature,
        max_temperature,
        wind_speed,
        wind_direction,
        visibility,
        timestamp,
        cloud_percent
    }

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
    public void fillFromJson(@NonNull final JSONObject json)
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

    @NonNull
    @Override
    public JSONObject toJsonObject()
    {
        // Return null because we only care about deserializing from JSON
        return new JSONObject();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // System.Object Overrides
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        try
        {
            return String.format(Locale.US, "city: %s, sunrise: %s, sunset: %s, temp: %f, minTemp: %f, maxTemp: %f, clouds: %f%%, pressure: %d, humidity: %d, visibility: %d, main: %s, description: %s, icon: %s, lat: %f, lng: %f, timestamp: %s",
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

    //@Override
    public int getVersion()
    {
        return 1;
    }

    @NonNull
    @Override
    public String getTableName()
    {
        return TableName;
    }

    @NonNull
    @Override
    public HashMap<Object, Object> getColumnMap(final int version)
    {
        HashMap<Object, Object> map = new HashMap<>();

        map.put(Columns.id, UUSqlColumnType.integerPrimaryKeyAutoIncrement);
        map.put(Columns.city, UUSqlColumnType.text);
        map.put(Columns.sunrise, UUSqlColumnType.int64);
        map.put(Columns.sunset, UUSqlColumnType.int64);
        map.put(Columns.latitude, UUSqlColumnType.real);
        map.put(Columns.longitude, UUSqlColumnType.real);
        map.put(Columns.weather_main, UUSqlColumnType.text);
        map.put(Columns.weather_description, UUSqlColumnType.text);
        map.put(Columns.weather_icon, UUSqlColumnType.text);
        map.put(Columns.temperature, UUSqlColumnType.real);
        map.put(Columns.pressure, UUSqlColumnType.int32);
        map.put(Columns.humidity, UUSqlColumnType.int32);
        map.put(Columns.min_temperature, UUSqlColumnType.real);
        map.put(Columns.max_temperature, UUSqlColumnType.real);
        map.put(Columns.wind_speed, UUSqlColumnType.real);
        map.put(Columns.wind_direction, UUSqlColumnType.real);
        map.put(Columns.visibility, UUSqlColumnType.int32);
        map.put(Columns.timestamp, UUSqlColumnType.int64);
        map.put(Columns.cloud_percent, UUSqlColumnType.real);

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
        return String.format(Locale.US, "%s = ?", Columns.id);
    }

    @NonNull
    public String[] getPrimaryKeyWhereArgs()
    {
        return new String[] { String.valueOf(id) };
    }

    @NonNull
    public ContentValues getContentValues(final int version)
    {
        ContentValues cv = new ContentValues();

        UUContentValues.putIfNotNull(cv, Columns.id, getId());
        UUContentValues.putIfNotNull(cv, Columns.city, getCity());
        UUContentValues.putIfNotNull(cv, Columns.sunrise, getSunrise());
        UUContentValues.putIfNotNull(cv, Columns.sunset, getSunset());
        UUContentValues.putIfNotNull(cv, Columns.latitude, getLatitude());
        UUContentValues.putIfNotNull(cv, Columns.longitude, getLongitude());
        UUContentValues.putIfNotNull(cv, Columns.weather_main, getWeatherMain());
        UUContentValues.putIfNotNull(cv, Columns.weather_description, getWeatherDescription());
        UUContentValues.putIfNotNull(cv, Columns.weather_icon, getWeatherIcon());
        UUContentValues.putIfNotNull(cv, Columns.temperature, getTemperature());
        UUContentValues.putIfNotNull(cv, Columns.pressure, getPressure());
        UUContentValues.putIfNotNull(cv, Columns.humidity, getHumidity());
        UUContentValues.putIfNotNull(cv, Columns.min_temperature, getMinTemperature());
        UUContentValues.putIfNotNull(cv, Columns.max_temperature, getMaxTemperature());
        UUContentValues.putIfNotNull(cv, Columns.wind_speed, getWindSpeed());
        UUContentValues.putIfNotNull(cv, Columns.wind_direction, getWindDirection());
        UUContentValues.putIfNotNull(cv, Columns.visibility, getVisibility());
        UUContentValues.putIfNotNull(cv, Columns.timestamp, getTimestamp());
        UUContentValues.putIfNotNull(cv, Columns.cloud_percent, getCloudPercent());

        return cv;
    }

    public void fillFromCursor(@NonNull final Cursor cursor)
    {
        setId(UUCursor.safeGetLong(cursor, Columns.id));
        setCity(UUCursor.safeGetString(cursor, Columns.city));
        setSunrise(UUCursor.safeGetLong(cursor, Columns.sunrise));
        setSunset(UUCursor.safeGetLong(cursor, Columns.sunset));
        setLatitude(UUCursor.safeGetDouble(cursor, Columns.latitude));
        setLongitude(UUCursor.safeGetDouble(cursor, Columns.longitude));
        setWeatherMain(UUCursor.safeGetString(cursor, Columns.weather_main));
        setWeatherDescription(UUCursor.safeGetString(cursor, Columns.weather_description));
        setWeatherIcon(UUCursor.safeGetString(cursor, Columns.weather_icon));
        setTemperature(UUCursor.safeGetFloat(cursor, Columns.temperature));
        setPressure(UUCursor.safeGetInt(cursor, Columns.pressure));
        setHumidity(UUCursor.safeGetInt(cursor, Columns.humidity));
        setMinTemperature(UUCursor.safeGetFloat(cursor, Columns.min_temperature));
        setMaxTemperature(UUCursor.safeGetFloat(cursor, Columns.max_temperature));
        setWindSpeed(UUCursor.safeGetFloat(cursor, Columns.wind_speed));
        setWindDirection(UUCursor.safeGetFloat(cursor, Columns.wind_direction));
        setVisibility(UUCursor.safeGetInt(cursor, Columns.visibility));
        setTimestamp(UUCursor.safeGetLong(cursor, Columns.timestamp));
        setCloudPercent(UUCursor.safeGetFloat(cursor, Columns.cloud_percent));
    }
}
