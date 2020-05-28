package com.example.climaapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.climaapp.R;
import com.example.climaapp.model.ApiResponse;
import com.example.climaapp.model.Weather;
import com.example.climaapp.retrofit.APIInterface;
import com.example.climaapp.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    // Constants:
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/";
    // App ID to use OpenWeather data
    private static final String APP_KEY = "0d727fcbd54f9689071a8afc15ee22d2";

    private APIInterface apiInterface;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    private ProgressBar progressBar;



    private LocationManager locationManager;
    private LocationListener locationListener;

    private Double currentLat;
    private Double currentLon;

    public static final int NEW_ACTIVITY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Linking the elements in the layout to Java code
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);
        progressBar = findViewById(R.id.progress_bar);

//        getWeatherDataFromServer("London",APP_KEY,false,0.0,0.0);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChangeCity.class);
                startActivityForResult(intent, NEW_ACTIVITY_REQUEST_CODE);
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.hasAccuracy()) {
                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();

                    getWeatherDataFromServer("", APP_KEY, true, currentLat, currentLon);
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("status", status + "");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("status", provider.toString());
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("status", provider.toString());
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//             TODO: Consider calling
//                ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates("gps", 0, 9000, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
             == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,9000,locationListener);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_ACTIVITY_REQUEST_CODE  && resultCode == RESULT_OK){
            String enteredCity = data.getStringExtra("city");
            getWeatherDataFromServer(enteredCity,APP_KEY,false,0.0,0.0);
        }else {
            Toast.makeText(getApplicationContext(), "City not entered", Toast.LENGTH_SHORT).show();
        }
    }

    public void getWeatherDataFromServer(String city , String appkey, boolean withCoordinates,
                                         Double lat , Double lon){

        progressBar.setVisibility(View.VISIBLE);

        apiInterface = RetrofitClient.getRetrofitClient().create(APIInterface.class);
        final Call<ApiResponse> mService ;

        if (!withCoordinates){
            mService = apiInterface.getWeatherData(city , appkey, "metric");
        }else {
            mService = apiInterface.getWeatherByCoordinates(lat, lon,appkey,"metric");
        }

        mService.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()){
                    ApiResponse responseBody = response.body();

                    mCityLabel.setText(responseBody.getName());

                    Double temperature =  responseBody.getMain().getTemp();
                    int integerTemperature = temperature.intValue();

                    mTemperatureLabel.setText(integerTemperature + "°C");

                    Weather weatherInfo = responseBody.getWeather().get(0);

                    String currentWeatherIcon = weatherInfo.getIcon();
                    String iconUrl = "https://openweathermap.org/img/wn/" + currentWeatherIcon + "@2x.png";

                    Picasso.get().load(iconUrl).into(mWeatherImage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
