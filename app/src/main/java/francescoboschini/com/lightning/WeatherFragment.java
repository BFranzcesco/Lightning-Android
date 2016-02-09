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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import francescoboschini.com.lightning.Utils.StringUtils;
import francescoboschini.com.lightning.Utils.WeatherIconHandler;
import francescoboschini.com.lightning.Utils.WeatherUtils;

public class WeatherFragment extends Fragment implements UpdateWeatherInterface, CurrentLocationInterface {

    private ImageView weatherImage;
    private WeatherUpdater weatherUpdater;
    private CurrentLocation currentLocation;
    private boolean isShowing = false;
    private View nightView;
    private FloatingActionButton chooseCityButton;

    public WeatherFragment() {
    }

    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherUpdater = new WeatherUpdater(getActivity(), this);

        setUpUI();

        currentLocation = new CurrentLocation(getActivity(), this);

        currentLocation.getLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    private void setUpUI() {
        weatherImage = (ImageView) getActivity().findViewById(R.id.iv_weather);

        nightView = getActivity().findViewById(R.id.night);

        chooseCityButton = (FloatingActionButton) getActivity().findViewById(R.id.reload_weather);
        /*chooseCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLocation.getLocation();
            }
        });
        */
    }

    private void updateCurrentWeather(Location location) {
        weatherUpdater.getCurrentWeather(location);
    }

    private void updateForecast(Location location, Long sunrise, long sunset) {
        weatherUpdater.getForecast(location, sunrise, sunset);
    }

    private void renderWeather(Weather weather) {
        if (weather != null) {
            WeatherIconHandler weatherIconHandler = new WeatherIconHandler(getActivity());
            weatherIconHandler.setIconBasedOnCurrentTime(weatherImage, weather.getWeatherCode(), weather.getSunrise(), weather.getSunset());

            nightView.setVisibility(weatherIconHandler.isDay(weather.getSunrise(), weather.getSunset()) ? View.GONE : View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.setStatusBarColor(getResources().getColor(R.color.header_color_night));
            }

        } else {
            //Snackbar.make(coordinatorLayout, R.string.some_details_not_found, Snackbar.LENGTH_SHORT).show();
        }
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
            MaterialDialog enableLocationServicesDialog = new MaterialDialog.Builder(getContext())
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
        //Snackbar.make(coordinatorLayout, getString(R.string.unable_to_find_weather) + " " + StringUtils.toFirstCharUpperCase(currentLocation.convertLocationToFullCityName(location)), Snackbar.LENGTH_LONG).show();
    }
}
