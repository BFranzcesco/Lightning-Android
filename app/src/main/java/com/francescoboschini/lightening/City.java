package lightning.francescoboschini.com.lightning;

public class City {

    private String cityName;
    public static String DEFAULT_CITY_NAME = "Milano";

    public City(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
