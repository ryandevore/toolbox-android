package uu.toolboxapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uu.toolboxapp.R;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        WeatherService.init(getApplicationContext());

        WeatherService.sharedInstance().fetchWeather("Portland", "us", new WeatherService.WeatherDelegate()
        {
            @Override
            public void onComplete()
            {

            }
        });*/
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    public void onLeScannerClicked(View view)
    {
        Intent intent = new Intent(this, BtleScanActivity.class);
        startActivity(intent);
    }

    public void onClassicScannerClicked(View view)
    {
        Intent intent = new Intent(this, ClassicScanActivity.class);
        startActivity(intent);
    }

    public void onPhotoGalleryClicked(View view)
    {
        Intent intent = new Intent(this, PhotoGalleryActivity.class);
        startActivity(intent);
    }

    public void onWeatherClicked(View view)
    {

    }
}
