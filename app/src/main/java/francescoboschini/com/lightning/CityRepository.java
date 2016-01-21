package francescoboschini.com.lightning;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityRepository {

    SharedPreferences sharedPreferences;
    Activity activity;

    public CityRepository(Activity activity){
        this.activity = activity;
        sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getSavedCity() {
        return sharedPreferences.getString("city", City.DEFAULT_CITY_NAME);
    }

    public void saveCity(String cityName) {
        sharedPreferences.edit().putString("city", cityName).commit();
    }
}
