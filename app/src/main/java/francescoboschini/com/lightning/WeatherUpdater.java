package francescoboschini.com.lightning;

import android.content.Context;
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

    public void getCurrentWeather(final String city) {
        handler = new Handler();
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getWeather(context, city);
                if(json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            updateWeatherInterface.onFailure(city);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            updateWeatherInterface.onWeatherSuccess(city, json);
                        }
                    });
                }
            }
        }.start();
    }

    public void getForecast(final String city) {
        handler = new Handler();
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getForecast(context, city);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            updateWeatherInterface.onFailure(city);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            updateWeatherInterface.onForecastSuccess(city, json);
                        }
                    });
                }
            }
        }.start();
    }
}
