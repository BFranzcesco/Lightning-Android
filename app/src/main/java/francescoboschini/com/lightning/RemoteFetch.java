package francescoboschini.com.lightning;


import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lightning.francescoboschini.com.lightning.R;

public class RemoteFetch {

    private static final String WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    private static final String FORECAST_MAP_API = "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric";


    public static JSONObject getWeather(Context context, String city){
            try {
                URL url = new URL(String.format(WEATHER_MAP_API, city));
                HttpURLConnection connection =
                (HttpURLConnection)url.openConnection();

                connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
                reader.close();

                JSONObject data = new JSONObject(json.toString());

                if(data.getInt("cod") != 200)
                    return null;

                return data;

            } catch(Exception e) {
                return null;
            }
        }

    public static JSONObject getForecast(Context context, String city) {
        try {
            URL url = new URL(String.format(FORECAST_MAP_API, city));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if(data.getInt("cod") != 200)
                return null;

            return data;

        } catch(Exception e) {
            return null;
        }
    }
}
