package francescoboschini.com.lightning;

import android.location.Location;
import org.json.JSONObject;

public interface UpdateWeatherInterface {
    void onWeatherSuccess(MyLocation location, JSONObject json);
    void onForecastSuccess(MyLocation location, JSONObject json, long sunrise, long sunset);
    void onFailure(MyLocation location);
}
