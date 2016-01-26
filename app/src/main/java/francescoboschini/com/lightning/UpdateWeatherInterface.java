package francescoboschini.com.lightning;

import org.json.JSONObject;

public interface UpdateWeatherInterface {
    void onWeatherSuccess(String city, JSONObject json);
    void onForecastSuccess(String city, JSONObject json);
    void onFailure(String city);
}
