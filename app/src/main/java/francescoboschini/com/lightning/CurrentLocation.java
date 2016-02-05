package francescoboschini.com.lightning;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CurrentLocation extends Service implements LocationListener {

    private final Context context;
    private boolean canGetLocation = false;

    private CurrentLocationInterface currentLocationInterface;
    private double latitude;
    private double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2000; // 1 km
    private static final long MIN_LOCATION_UPDATE_REFRESHING_TIME = 1000 * 60 * 60 * 6; // 6 hours

    protected LocationManager locationManager;

    public CurrentLocation(Context context, CurrentLocationInterface currentLocationInterface) {
        this.context = context;
        this.currentLocationInterface = currentLocationInterface;
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_LOCATION_UPDATE_REFRESHING_TIME, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (!isGPSEnabled() && !isNetworkEnabled()) {
            } else {
                Location location = null;
                this.canGetLocation = true;
                if (isNetworkEnabled()) {
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled()) {
                    locationManager. requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_LOCATION_UPDATE_REFRESHING_TIME, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (location == null) {
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                currentLocationInterface.onLocationGot(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public String convertLocationToFullCityName(Location location) {
        String cityName = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
            cityName = addresses.get(0).getLocality();

        return cityName;
    }

    @Override
    public void onProviderDisabled(String provider) {
        currentLocationInterface.onProviderDisabled();
    }

    @Override
    public void onProviderEnabled(String provider) {
        currentLocationInterface.onProviderEnabled();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
