package francescoboschini.com.lightning;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.melnykov.fab.FloatingActionButton;

import francescoboschini.com.lightning.Utils.StringUtils;
import francescoboschini.com.lightning.Utils.WeatherIconHandler;
import francescoboschini.com.lightning.Utils.WeatherUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, UpdateWeatherInterface{

    public static final String TEMPERATURE_FORMAT = "%.1f";
    public static final int DEFAULT_WEATHER_REFRESHING_TIME = 60;
    private TextView tvTemperature;
    private CityRepository cityRepository;
    private ImageView weatherImage;
    private TextView tvPlace;
    private TextView tvDescription;
    private TextView tvHumidity;
    private TextView tvLastUpdate;
    private CoordinatorLayout coordinatorLayout;
    private ListView forecastListView;
    private List<ForecastItem> forecastList;
    private ForecastListAdapter adapter;
    private WeatherUpdater weatherUpdater;
    private CurrentLocation currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordinator_layout);

        weatherUpdater = new WeatherUpdater(getApplicationContext(), this);

        setUpUI();

        cityRepository = new CityRepository(this);
        currentLocation = new CurrentLocation(getApplicationContext());

        forecastList = new ArrayList<>();
        adapter = new ForecastListAdapter(this, R.layout.forecast_item_raw, forecastList);
        forecastListView.setAdapter(adapter);

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                updateWeatherAndForecast(cityRepository.getSavedCity());
            }
        }, 0, DEFAULT_WEATHER_REFRESHING_TIME, TimeUnit.MINUTES);

        weatherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWeatherAndForecast(cityRepository.getSavedCity());
            }
        });
    }

    private void setUpUI() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);
        weatherImage = (ImageView) findViewById(R.id.iv_weather);

        forecastListView = (ListView) findViewById(R.id.forecast_list_view);
        View currentWeatherInfosHeader = getLayoutInflater().inflate(R.layout.current_weather_infos_layout, forecastListView, false);
        tvPlace = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_place);
        tvDescription = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_description);
        tvHumidity = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_humidity);
        tvLastUpdate = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_last_update);

        forecastListView.addHeaderView(currentWeatherInfosHeader);

        FloatingActionButton chooseCityButton = (FloatingActionButton) findViewById(R.id.choose_city);

        chooseCityButton.setOnClickListener(this);
        chooseCityButton.attachToListView(forecastListView);
    }

    private void updateWeatherAndForecast(String city) {
        updateCurrentWeather(city);
        updateForecast(city);
    }

    private void updateCurrentWeather(final String city) {
        weatherUpdater.getCurrentWeather(city);
    }

    private void updateForecast(final String city) {
        weatherUpdater.getForecast(city);
    }

    private void renderWeather(Weather weather) {
        if(weather != null) {
            tvTemperature.setText(String.format(TEMPERATURE_FORMAT, weather.getTemperature()) + getString(R.string.celsius_degrees));
            tvPlace.setText(StringUtils.toFirstCharUpperCase(weather.getCityName()) + ", " + weather.getCountry());
            tvDescription.setText(StringUtils.toFirstCharUpperCase(weather.getDescription()));
            tvHumidity.setText(getString(R.string.humidity) + weather.getHumidity() + "%");

            String updatedOn = StringUtils.formatLongDate(weather.getLastUpdate());
            tvLastUpdate.setText(getString(R.string.last_update) + updatedOn);

            new WeatherIconHandler(getApplicationContext()).setIconBasedOnCurrentTime(weatherImage, weather.getWeatherCode(), weather.getSunrise(), weather.getSunset());
        } else {
            Snackbar.make(coordinatorLayout, R.string.some_details_not_found, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void populateForecastList(List<ForecastItem> forecast) {
        forecastList.clear();
        for(int i=0; i<forecast.size(); i++) {
            forecastList.add(forecast.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    private void showCityNameInputDialog() {
        new MaterialDialog.Builder(this)
                .titleColorRes(R.color.light_blue)
                .contentColor(getResources().getColor(R.color.dark_asphalt_blue))
                .backgroundColorRes(R.color.white)
                .widgetColor(getResources().getColor(R.color.light_blue))
                .title(R.string.alert_content)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.alert_input_hint), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        updateWeatherAndForecast(input.toString());
                    }
                }).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateCurrentWeather(cityRepository.isEmpty() ? currentLocation.getCityNameBasedOnLocation() : cityRepository.getSavedCity());
    }

    @Override
    public void onClick(View v) {
        showCityNameInputDialog();
    }

    @Override
    public void onWeatherSuccess(String city, JSONObject json) {
        cityRepository.saveCity(city);
        renderWeather(WeatherUtils.convertToCurrentWeather(json));
        updateForecast(city);
    }

    @Override
    public void onFailure(String city) {
        Snackbar.make(coordinatorLayout, getString(R.string.place) + StringUtils.toFirstCharUpperCase(city) + getString(R.string.not_found), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onForecastSuccess(String city, JSONObject json) {
        cityRepository.saveCity(city);
        populateForecastList(WeatherUtils.convertToForecast(json));
    }
}
