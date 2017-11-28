package uu.toolboxapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import uu.toolboxapp.R;
import uu.toolboxapp.server.WeatherService;

public class MainActivity extends AppCompatActivity
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


        Intent intent = new Intent(this, PhotoGalleryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
}
