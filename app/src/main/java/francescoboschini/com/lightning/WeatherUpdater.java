package francescoboschini.com.lightning;

import android.content.Context;
import android.location.Location;
import android.os.Handler;

import org.json.JSONObject;

public class WeatherUpdater {
    private Context context;
    private Handler handler;
    private UpdateWeatherInterface updateWeatherInterface;

    public WeatherUpdater(Context context, UpdateWeatherInterface updateWeatherInterface) {
        this.context = context;
        this.updateWeatherInterface = updateWeatherInterface;
    }

    public void getCurrentWeather(final Location location) {
        handler = new Handler();
        new Thread(){
            public void run(){
                final JSONObject json = RemoteData.getWeather(context, location);
                if(json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            updateWeatherInterface.onFailure(location);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            updateWeatherInterface.onWeatherSuccess(location, json);
                        }
                    });
                }
            }
        }.start();
    }

    public void getForecast(final Location location, final long sunrise, final long sunset) {
        handler = new Handler();
        new Thread() {
            public void run() {
                final JSONObject json = RemoteData.getForecast(context, location);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            updateWeatherInterface.onFailure(location);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            updateWeatherInterface.onForecastSuccess(location, json, sunrise, sunset);
                        }
                    });
                }
            }
        }.start();
    }
}
