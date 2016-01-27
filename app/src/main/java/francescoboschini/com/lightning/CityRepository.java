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

    public void saveCity(String cityName) {
        sharedPreferences.edit().putString("city", cityName).commit();
    }
}
