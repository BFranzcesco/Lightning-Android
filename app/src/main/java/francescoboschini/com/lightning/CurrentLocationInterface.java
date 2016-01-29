package francescoboschini.com.lightning;

import android.location.Location;

public interface CurrentLocationInterface {
    void onLocationGot(Location location);
    void onProviderDisabled();
    void onProviderEnabled();
}
