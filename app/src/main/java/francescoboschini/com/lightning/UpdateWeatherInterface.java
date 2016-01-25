package francescoboschini.com.lightning;

import org.json.JSONObject;

public interface UpdateWeatherInterface {

    public void onWeatherSuccess(String city, JSONObject json);
    public void onForecastSuccess(String city, JSONObject json);

    public void onFailure(String city);
}
