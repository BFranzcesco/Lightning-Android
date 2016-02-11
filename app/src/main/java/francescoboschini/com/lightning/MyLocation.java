package francescoboschini.com.lightning;

public class MyLocation {

    private double latitude;
    private double longitude;
    private String cityName;

    public MyLocation(double latitude, double longitude, String cityName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityName = cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCityName() {
        return cityName;
    }
}
