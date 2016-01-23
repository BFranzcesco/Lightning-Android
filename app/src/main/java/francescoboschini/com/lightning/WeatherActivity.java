package francescoboschini.com.lightning;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lightning.francescoboschini.com.lightning.R;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TEMPERATURE_FORMAT = "%.1f";
    public static final String DATE_FORMAT = "EEE dd MMM yyyy, HH:mm";
    public static final String SIMPLE_DATE_FORMAT = "EEE dd MMM, HH:mm";
    public static final int DEFAULT_WEATHER_REFRESHING_TIME = 60;
    private Handler handler;
    private TextView tvTemperature;
    private final DateFormat lastUpdateFormat = new SimpleDateFormat(DATE_FORMAT);
    private final DateFormat lastUpdateSimpleFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
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
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.coordinator_layout);

        handler = new Handler();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);
        tvPlace = (TextView) findViewById(R.id.tv_place);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvHumidity = (TextView) findViewById(R.id.tv_humidity);
        tvLastUpdate = (TextView) findViewById(R.id.tv_last_update);
        weatherImage = (ImageView) findViewById(R.id.iv_weather);

        forecastListView = (ListView) findViewById(R.id.forecast_list_view);

        chooseCityButton = (FloatingActionButton) findViewById(R.id.choose_city);
        chooseCityButton.setOnClickListener(this);

        cityRepository = new CityRepository(this);

        updateCityWeatherData(cityRepository.getSavedCity());
        updateCityForecastData(cityRepository.getSavedCity());

        sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                updateCityWeatherData(cityRepository.getSavedCity());
                updateCityForecastData(cityRepository.getSavedCity());
            }
        }, 0, DEFAULT_WEATHER_REFRESHING_TIME, TimeUnit.MINUTES);

        weatherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCityWeatherData(cityRepository.getSavedCity());
                updateCityForecastData(cityRepository.getSavedCity());
            }
        });
    }

    private void updateCityWeatherData(final String city) {
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getWeather(getApplicationContext(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Snackbar.make(coordinatorLayout, getString(R.string.place) + Utils.toFirstCharUpperCase(city) + getString(R.string.not_found), Snackbar.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run() {
                            cityRepository.saveCity(city);
                            renderWeather(convertToWeather(json));
                        }
                    });
                }
            }
        }.start();
    }

    private void updateCityForecastData(final String city) {
        new Thread(){
            public void run(){
                final JSONObject jsonList = RemoteFetch.getForecast(getApplicationContext(), city);
                if(jsonList == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Snackbar.make(coordinatorLayout, getString(R.string.place) + Utils.toFirstCharUpperCase(city) + getString(R.string.not_found), Snackbar.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            cityRepository.saveCity(city);
                            renderForecast(jsonList);
                        }
                    });
                }
            }
        }.start();
    }

    private Weather convertToWeather(JSONObject json) {
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

            String updatedOn = lastUpdateFormat.format(new Date(weather.getLastUpdate() * 1000));
            tvLastUpdate.setText(getString(R.string.last_update) + updatedOn);

            new WeatherIconHandler(getApplicationContext()).setWeatherIcon(weatherImage, weather.getWeatherCode(), weather.getSunrise(), weather.getSunset());
        } else {
            Log.e("Lightning", "Weather object null");
            Snackbar.make(coordinatorLayout, R.string.some_details_not_found, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void renderForecast(JSONObject jsonObject) {
        try {
            ArrayList <String> forecastList = new ArrayList<String>();
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, forecastList);

            JSONArray jsonList = jsonObject.getJSONArray("list");

            for(int i=0; i<jsonList.length(); i++) {
                JSONObject jsonobject = jsonList.getJSONObject(i);
                Long dt = jsonobject.getLong("dt");
                JSONObject main = jsonobject.getJSONObject("main");
                Double temp = main.getDouble("temp");

                forecastList.add(lastUpdateSimpleFormat.format(new Date(dt * 1000)) + " " + temp + getResources().getString(R.string.celsius_degrees));
            }

            forecastListView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("ERRORE JSON LIST");
        }
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
                        updateCityWeatherData(input.toString());
                        updateCityForecastData(input.toString());
                    }
                }).show();
    }
}
