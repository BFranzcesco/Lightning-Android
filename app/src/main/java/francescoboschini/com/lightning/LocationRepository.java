package francescoboschini.com.lightning;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocationRepository {

    public static final String LOCATION_LATITUDE = "location_latitude";
    public static final String LOCATION_LONGITUDE = "location_longitude";
    private static final String LOCATION_CITY_NAME = "location_city_name";
    SharedPreferences sharedPreferences;
    Context context;

    public LocationRepository(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Locations", Context.MODE_PRIVATE);
    }

    public MyLocation getSavedLocation() {
        double locationLatitude = getDouble(sharedPreferences, LOCATION_LATITUDE, 0);
        double locationLongitude = getDouble(sharedPreferences, LOCATION_LONGITUDE, 0);
        String locationCityName = sharedPreferences.getString(LOCATION_CITY_NAME, "");
        return new MyLocation(locationLatitude, locationLongitude, locationCityName);
    }

    public void saveCity(MyLocation location) {
        Editor prefsEditor = sharedPreferences.edit();
        putDouble(prefsEditor, LOCATION_LATITUDE, location.getLatitude());
        putDouble(prefsEditor, LOCATION_LONGITUDE, location.getLongitude());
        prefsEditor.putString(LOCATION_CITY_NAME, location.getCityName());
        prefsEditor.apply();
    }

    public boolean isEmpty() {
        return !sharedPreferences.contains("location");
    }

    private Editor putDouble(final Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
