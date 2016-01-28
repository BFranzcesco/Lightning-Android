package francescoboschini.com.lightning;

import android.location.Location;

import org.json.JSONObject;

public interface UpdateWeatherInterface {
    void onWeatherSuccess(Location location, JSONObject json);
    void onForecastSuccess(Location location, JSONObject json);
    void onFailure(Location location);
}
