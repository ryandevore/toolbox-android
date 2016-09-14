package uu.toolboxapp.data.models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

import uu.toolbox.core.UUDate;
import uu.toolbox.core.UUJson;
import uu.toolbox.core.UUJsonConvertible;

/**
 * Created by ryandevore on 9/14/16.
 */
public class WeatherSummary implements UUJsonConvertible
{
    private String city;
    private long sunrise;
    private long sunset;
    private double latitude;
    private double longitude;
    private String weatherMain;
    private String weatherDescription;
    private String weatherIcon;
    private float temperature;
    private int pressure;
    private int humidity;
    private float minTemperature;
    private float maxTemperature;
    private float windSpeed;
    private float windDirection;
    private int visibility;
    private long timestamp;
    private float cloudPercent;

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public long getSunrise()
    {
        return sunrise;
    }

    public void setSunrise(long sunrise)
    {
        this.sunrise = sunrise;
    }

    public long getSunset()
    {
        return sunset;
    }

    public void setSunset(long sunset)
    {
        this.sunset = sunset;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
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

    public float getTemperature()
    {
        return temperature;
    }

    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
    }

    public int getPressure()
    {
        return pressure;
    }

    public void setPressure(int pressure)
    {
        this.pressure = pressure;
    }

    public int getHumidity()
    {
        return humidity;
    }

    public void setHumidity(int humidity)
    {
        this.humidity = humidity;
    }

    public float getMinTemperature()
    {
        return minTemperature;
    }

    public void setMinTemperature(float minTemperature)
    {
        this.minTemperature = minTemperature;
    }

    public float getMaxTemperature()
    {
        return maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature)
    {
        this.maxTemperature = maxTemperature;
    }

    public float getWindSpeed()
    {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed)
    {
        this.windSpeed = windSpeed;
    }

    public float getWindDirection()
    {
        return windDirection;
    }

    public void setWindDirection(float windDirection)
    {
        this.windDirection = windDirection;
    }

    public int getVisibility()
    {
        return visibility;
    }

    public void setVisibility(int visibility)
    {
        this.visibility = visibility;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public float getCloudPercent()
    {
        return cloudPercent;
    }

    public void setCloudPercent(float cloudPercent)
    {
        this.cloudPercent = cloudPercent;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUJsonConvertible
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void fillFromJson(final Context context, final JSONObject json)
    {
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
        setWindDirection(UUJson.safeGetInt(wind, "deg"));

        JSONObject cloud = UUJson.safeGetJsonObject(json, "cloud");
        setCloudPercent(UUJson.safeGetFloat(cloud, "all"));

        setTimestamp(UUJson.safeGetInt(json, "dt") * 1000);
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
    // System Overrides
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
}
