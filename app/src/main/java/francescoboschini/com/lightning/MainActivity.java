package francescoboschini.com.lightning;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import com.melnykov.fab.FloatingActionButton;

import francescoboschini.com.lightning.Utils.StringUtils;
import francescoboschini.com.lightning.Utils.WeatherIconHandler;
import francescoboschini.com.lightning.Utils.WeatherUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UpdateWeatherInterface, CurrentLocationInterface {

    public static final String TEMPERATURE_FORMAT = "%.1f";
    private TextView tvTemperature;
    private ImageView weatherImage;
    private TextView tvPlace;
    private TextView tvDescription;
    private TextView tvLastUpdate;
    private CoordinatorLayout coordinatorLayout;
    private ListView forecastListView;
    private List<ForecastItem> forecastList;
    private ForecastListAdapter adapter;
    private WeatherUpdater weatherUpdater;
    private CurrentLocation currentLocation;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordinator_layout);

        weatherUpdater = new WeatherUpdater(getApplicationContext(), this);

        setUpUI();

        currentLocation = new CurrentLocation(getApplicationContext(), this);

        forecastList = new ArrayList<>();
        adapter = new ForecastListAdapter(this, R.layout.forecast_item_raw, forecastList);
        forecastListView.setAdapter(adapter);

        weatherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherImage.setClickable(false);
                updateWeatherAndForecast(location);
            }
        });

        currentLocation.getLocation();

    }

    private void setUpUI() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);
        weatherImage = (ImageView) findViewById(R.id.iv_weather);

        forecastListView = (ListView) findViewById(R.id.forecast_list_view);
        View currentWeatherInfosHeader = getLayoutInflater().inflate(R.layout.current_weather_infos_layout, forecastListView, false);
        tvPlace = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_place);
        tvDescription = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_description);
        tvLastUpdate = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_last_update);

        forecastListView.addHeaderView(currentWeatherInfosHeader);

        FloatingActionButton chooseCityButton = (FloatingActionButton) findViewById(R.id.choose_city);

        Button reloadWeather = (Button) findViewById(R.id.reload_weather_button);
        reloadWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideReloadWeatherScreen();
                currentLocation.getLocation();
            }
        });

        chooseCityButton.attachToListView(forecastListView);
    }

    private void updateWeatherAndForecast(Location location) {
        updateCurrentWeather(location);
        updateForecast(location);
    }

    private void updateCurrentWeather(Location location) {
        weatherUpdater.getCurrentWeather(location);
    }

    private void updateForecast(Location location) {
        weatherUpdater.getForecast(location);
    }

    private void renderWeather(Weather weather) {
        if (weather != null) {
            tvTemperature.setText(String.format(TEMPERATURE_FORMAT, weather.getTemperature()) + getString(R.string.celsius_degrees));
            tvPlace.setText(StringUtils.toFirstCharUpperCase(weather.getCityName()) + ", " + weather.getCountry());
            tvDescription.setText(StringUtils.toFirstCharUpperCase(weather.getDescription()));

            String updatedOn = StringUtils.formatLongDate(weather.getLastUpdate());
            tvLastUpdate.setText(getString(R.string.last_update) + updatedOn);

            new WeatherIconHandler(getApplicationContext()).setIconBasedOnCurrentTime(weatherImage, weather.getWeatherCode(), weather.getSunrise(), weather.getSunset());
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
        renderWeather(WeatherUtils.convertToCurrentWeather(json));
        updateForecast(location);
    }

    @Override
    public void onFailure(Location location) {
        if (location != null)
            Snackbar.make(coordinatorLayout, getString(R.string.place) + StringUtils.toFirstCharUpperCase(currentLocation.convertLocationToFullCityName(location)) + getString(R.string.not_found), Snackbar.LENGTH_LONG).show();
        weatherImage.setClickable(true);
    }

    @Override
    public void onForecastSuccess(Location location, JSONObject json) {
        populateForecastList(WeatherUtils.convertToForecast(json));
        weatherImage.setClickable(true);
    }

    @Override
    public void onLocationGot(Location location) {
        this.location = location;
        updateCurrentWeather(location);
    }

    @Override
    public void onProviderDisabled() {
        new MaterialDialog.Builder(this)
            .title("No providers found")
            .titleColorRes(R.color.light_blue)
            .contentColor(getResources().getColor(R.color.dark_asphalt_blue))
            .backgroundColorRes(R.color.white)
            .widgetColor(getResources().getColor(R.color.light_blue))
            .content("Unable to find your location. Go to settings end enable location services or insert location manually")
            .positiveText("Go to settings")
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                    Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(viewIntent);
                    showReloadWeatherScreen();
                }
            })
            .show();
    }

    private void showReloadWeatherScreen() {
        findViewById(R.id.reload_weather_layout).setVisibility(View.VISIBLE);
    }

    private void hideReloadWeatherScreen() {
        findViewById(R.id.reload_weather_layout).setVisibility(View.GONE);
    }

}
