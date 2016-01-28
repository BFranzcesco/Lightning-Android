package francescoboschini.com.lightning;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityRepository {

    public static final String CITY = "city";
    SharedPreferences sharedPreferences;
    Activity activity;

    public CityRepository(Activity activity){
        this.activity = activity;
        sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getLastCity() {
        return sharedPreferences.getString(CITY, "");
    }

    public void saveCity(String cityName) {
        sharedPreferences.edit().putString(CITY, cityName).commit();
    }
}
