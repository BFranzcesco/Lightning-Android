package francescoboschini.com.lightning.Utils;

import android.support.design.widget.Snackbar;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import francescoboschini.com.lightning.ForecastItem;
import francescoboschini.com.lightning.Weather;

public class WeatherUtils {

    public static Weather convertToCurrentWeather(JSONObject json) {
        Weather weather = null;
        try {
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            weather = new Weather(
                    main.getDouble("temp"),
                    json.getString("name"),
                    json.getJSONObject("sys").getString("country"),
                    details.getString("description"),
                    details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise"),
                    json.getJSONObject("sys").getLong("sunset"));

        } catch(Exception e) {
            Log.e("Lightning", "One or more fields not found in the JSON data");
        }

        return weather;
    }

    public static List<ForecastItem> convertToForecast(JSONObject json) {
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
        }

        return forecast;
    }
}
