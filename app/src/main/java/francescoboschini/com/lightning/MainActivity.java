package francescoboschini.com.lightning;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import com.crashlytics.android.Crashlytics;
import com.melnykov.fab.FloatingActionButton;

import francescoboschini.com.lightning.Utils.StringUtils;
import francescoboschini.com.lightning.Utils.WeatherIconHandler;
import francescoboschini.com.lightning.Utils.WeatherUtils;

import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UpdateWeatherInterface, CurrentLocationInterface {

    private TextView tvTemperature;
    private ImageView weatherImage;
    private TextView tvPlace;
    private TextView tvDescription;
    private CoordinatorLayout coordinatorLayout;
    private ListView forecastListView;
    private List<ForecastItem> forecastList;
    private ForecastListAdapter adapter;
    private WeatherUpdater weatherUpdater;
    private CurrentLocation currentLocation;
    private boolean isShowing = false;
    private LocationRepository locationRepository;
    private View nightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.coordinator_layout);

        weatherUpdater = new WeatherUpdater(getApplicationContext(), this);
        locationRepository = new LocationRepository(this);

        setUpUI();

        currentLocation = new CurrentLocation(getApplicationContext(), this);

        forecastList = new ArrayList<>();
        adapter = new ForecastListAdapter(this, R.layout.forecast_item_raw, forecastList);
        forecastListView.setAdapter(adapter);

        currentLocation.getLocation();
    }

    private void setUpUI() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        Typeface typeFaceMedium = Typeface.createFromAsset(getAssets(), "fonts/brandon_medium.ttf");
        Typeface typeFaceRegular = Typeface.createFromAsset(getAssets(), "fonts/brandon_regular.ttf");

        weatherImage = (ImageView) findViewById(R.id.iv_weather);

        forecastListView = (ListView) findViewById(R.id.forecast_list_view);
        final View weatherInfosHeader = getLayoutInflater().inflate(R.layout.weather_infos_layout, forecastListView, false);

        tvTemperature = (TextView) weatherInfosHeader.findViewById(R.id.tv_temperature);
        tvTemperature.setTypeface(typeFaceMedium);

        tvPlace = (TextView) weatherInfosHeader.findViewById(R.id.tv_place);
        tvPlace.setTypeface(typeFaceRegular);

        tvDescription = (TextView) weatherInfosHeader.findViewById(R.id.tv_description);
        tvDescription.setTypeface(typeFaceMedium);

        nightView = findViewById(R.id.night);

        forecastListView.addHeaderView(weatherInfosHeader);

        FloatingActionButton chooseCityButton = (FloatingActionButton) findViewById(R.id.reload_weather);
        chooseCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLocation.getLocation();
            }
        });

        chooseCityButton.attachToListView(forecastListView);
    }

    private void updateCurrentWeather(Location location) {
        weatherUpdater.getCurrentWeather(location);
    }

    private void updateForecast(Location location, Long sunrise, long sunset) {
        weatherUpdater.getForecast(location, sunrise, sunset);
    }

    private void renderWeather(Weather weather) {
        if (weather != null) {
            tvTemperature.setText(StringUtils.formatTemperature(weather.getTemperature()) + getString(R.string.celsius_degrees));
            tvPlace.setText(StringUtils.toFirstCharUpperCase(weather.getCityName()) + ", " + weather.getCountry());
            tvDescription.setText(StringUtils.toFirstCharUpperCase(weather.getDescription()));

            WeatherIconHandler weatherIconHandler = new WeatherIconHandler(getApplicationContext());
            weatherIconHandler.setIconBasedOnCurrentTime(weatherImage, weather.getWeatherCode(), weather.getSunrise(), weather.getSunset());

            nightView.setVisibility(weatherIconHandler.isDay(weather.getSunrise(), weather.getSunset()) ? View.GONE : View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.setStatusBarColor(getResources().getColor(R.color.header_color_night));
            }

        } else {
            Snackbar.make(coordinatorLayout, R.string.some_details_not_found, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void populateForecastList(List<ForecastItem> forecast) {
        forecastList.clear();
        for (int i = 0; i < forecast.size(); i++) {
            forecastList.add(forecast.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onWeatherSuccess(Location location, JSONObject json) {
        Weather weather = WeatherUtils.convertToCurrentWeather(json);
        renderWeather(weather);
        updateForecast(location, weather.getSunrise(), weather.getSunset());
    }

    @Override
    public void onFailure(Location location) {
        if (location != null)
            showUnableToFindWeatherForLocation(location);
    }

    @Override
    public void onForecastSuccess(Location location, JSONObject json, long sunrise, long sunset) {
        populateForecastList(WeatherUtils.convertToForecast(json, sunrise, sunset));
    }

    @Override
    public void onLocationGot(Location location) {
        updateCurrentWeather(location);
    }

    @Override
    public void onProviderDisabled() {
        showEnableLocationServicesDialog();
    }

    @Override
    public void onProviderEnabled() {
    }

    private void showEnableLocationServicesDialog() {
        if (!isShowing) {
            isShowing = true;
            MaterialDialog enableLocationServicesDialog = new MaterialDialog.Builder(this)
                    .title(getResources().getString(R.string.unable_to_find_location))
                    .titleColorRes(R.color.light_blue)
                    .contentColor(getResources().getColor(R.color.blue_night))
                    .backgroundColorRes(R.color.white)
                    .widgetColor(getResources().getColor(R.color.light_blue))
                    .autoDismiss(false)
                    .content(getResources().getString(R.string.enable_location_services))
                    .positiveText(getResources().getString(R.string.go_to_settings))
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            isShowing = false;
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            isShowing = false;
                            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(viewIntent);
                        }
                    }).build();

            enableLocationServicesDialog.show();

        }
    }

    private void showUnableToFindWeatherForLocation(Location location) {
        Snackbar.make(coordinatorLayout, getString(R.string.unable_to_find_weather) + " " + StringUtils.toFirstCharUpperCase(currentLocation.convertLocationToFullCityName(location)), Snackbar.LENGTH_LONG).show();
    }
}
