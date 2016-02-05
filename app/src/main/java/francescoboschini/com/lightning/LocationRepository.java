package francescoboschini.com.lightning;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class LocationRepository {

    SharedPreferences sharedPreferences;
    Context context;

    public LocationRepository(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Locations", Context.MODE_PRIVATE);
    }

    public MyLocation getSavedLocation() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("location", "");
        MyLocation location = gson.fromJson(json, MyLocation.class);
        return location;
    }

    public void saveCity(MyLocation location) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        String json = new Gson().toJson(location);
        prefsEditor.putString("location", json);
        prefsEditor.commit();
    }

    public boolean isEmpty() {
        return !sharedPreferences.contains("location");
    }
}
