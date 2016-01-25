package francescoboschini.com.lightning;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.crashlytics.android.Crashlytics;
import com.melnykov.fab.FloatingActionButton;

import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lightning.francescoboschini.com.lightning.R;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener, UpdateWeatherInterface {

    public static final String TEMPERATURE_FORMAT = "%.1f";
    public static final int DEFAULT_WEATHER_REFRESHING_TIME = 60;
    private Handler handler;
    private TextView tvTemperature;
    private FloatingActionButton chooseCityButton;
    private CityRepository cityRepository;
    private ImageView weatherImage;
    private TextView tvPlace;
    private TextView tvDescription;
    private TextView tvHumidity;
    private TextView tvLastUpdate;
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences sharedPreferences;
    private ListView forecastListView;
    private List<ForecastItem> forecastList;
    private ForecastListAdapter adapter;
    private WeatherUpdater weatherUpdater;
    private View currentWeatherInfosHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.coordinator_layout);

        handler = new Handler();
        weatherUpdater = new WeatherUpdater(getApplicationContext(), this);

        setUpUI();

        cityRepository = new CityRepository(this);

        forecastList = new ArrayList<ForecastItem>();
        adapter = new ForecastListAdapter(this, R.layout.forecast_item_raw, forecastList);
        forecastListView.setAdapter(adapter);

        updateCurrentWeather(cityRepository.getSavedCity());
        updateForecast(cityRepository.getSavedCity());

        sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);

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
        currentWeatherInfosHeader = getLayoutInflater().inflate(R.layout.current_weather_infos_layout, forecastListView, false);
        tvPlace = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_place);
        tvDescription = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_description);
        tvHumidity = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_humidity);
        tvLastUpdate = (TextView) currentWeatherInfosHeader.findViewById(R.id.tv_last_update);

        forecastListView.addHeaderView(currentWeatherInfosHeader);

        chooseCityButton = (FloatingActionButton) findViewById(R.id.choose_city);

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

    private List<ForecastItem> convertToForecast(JSONObject json) {
        List<ForecastItem> forecast = new ArrayList<ForecastItem>();

        try {
            JSONArray jsonList = json.getJSONArray("list");

            for(int i=0; i<(jsonList.length()/2); i++) {
                JSONObject jsonObject = jsonList.getJSONObject(i);
                JSONObject main = jsonObject.getJSONObject("main");
                JSONArray weatherList = jsonObject.getJSONArray("weather");
                String description = weatherList.getJSONObject(0).getString("description");
                int weatherId = weatherList.getJSONObject(0).getInt("id");

                forecast.add(new ForecastItem (
                        main.getDouble("temp"),
                        description,
                        jsonObject.getLong("dt"),
                        weatherId));
            }

        } catch(Exception e) {
            Log.e("Lightning", "One or more fields not found in the JSON data");
            Snackbar.make(coordinatorLayout, R.string.some_details_not_found, Snackbar.LENGTH_SHORT).show();
        }

        return forecast;
    }

    private Weather convertToCurrentWeather(JSONObject json) {
        Weather weather = null;
        try {
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            weather = new Weather(
                    main.getDouble("temp"),
                    json.getString("name"),
                    json.getJSONObject("sys").getString("country"),
                    details.getString("description"),
                    main.getString("humidity"),
                    json.getLong("dt"),
                    details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise"),
                    json.getJSONObject("sys").getLong("sunset"));

        } catch(Exception e) {
            Log.e("Lightning", "One or more fields not found in the JSON data");
            Snackbar.make(coordinatorLayout, R.string.some_details_not_found, Snackbar.LENGTH_SHORT).show();
        }

        return weather;
    }

    private void renderWeather(Weather weather) {
        if(weather != null) {
            tvTemperature.setText(String.format(TEMPERATURE_FORMAT, weather.getTemperature()) + getString(R.string.celsius_degrees));
            tvPlace.setText(Utils.toFirstCharUpperCase(weather.getCityName()) + ", " + weather.getCountry());
            tvDescription.setText(Utils.toFirstCharUpperCase(weather.getDescription()));
            tvHumidity.setText(getString(R.string.humidity) + weather.getHumidity() + "%");

            String updatedOn = Utils.formatLongDate(weather.getLastUpdate());
            tvLastUpdate.setText(getString(R.string.last_update) + updatedOn);

            new WeatherIconHandler(getApplicationContext()).setIconBasedOnCurrentTime(weatherImage, weather.getWeatherCode(), weather.getSunrise(), weather.getSunset());
        } else {
            Log.e("Lightning", "Weather object null");
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

    @Override
    public void onClick(View v) {
        showCityNameInputDialog();
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
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        updateWeatherAndForecast(input.toString());
                    }
                }).show();
    }

    @Override
    public void onWeatherSuccess(String city, JSONObject json) {
        cityRepository.saveCity(city);
        renderWeather(convertToCurrentWeather(json));
    }

    @Override
    public void onFailure(String city) {
        Snackbar.make(coordinatorLayout, getString(R.string.place) + Utils.toFirstCharUpperCase(city) + getString(R.string.not_found), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onForecastSuccess(String city, JSONObject json) {
        cityRepository.saveCity(city);
        populateForecastList(convertToForecast(json));
    }
}
