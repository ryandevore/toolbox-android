package uu.toolboxapp;

import android.app.Activity;
import android.os.Bundle;

import uu.toolboxapp.server.WeatherService;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WeatherService.init(getApplicationContext());

        WeatherService.sharedInstance().fetchWeather("Portland", "us", new WeatherService.WeatherDelegate()
        {
            @Override
            public void onComplete()
            {

            }
        });
    }
}
