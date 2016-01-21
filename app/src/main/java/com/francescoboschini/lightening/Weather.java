package lightning.francescoboschini.com.lightning;

public class Weather {

    private double temperature;
    private String cityName;
    private String country;
    private String description;
    private String humidity;
    private long lastUpdate;
    private int weatherCode;
    private long sunrise;
    private long sunset;

    public Weather(double temperature, String cityName, String country, String description, String humidity, long lastUpdate, int weatherCode, long sunrise, long sunset) {
        this.temperature = temperature;
        this.cityName = cityName;
        this.country = country;
        this.description = description;
        this.humidity = humidity;
        this.lastUpdate = lastUpdate;
        this.weatherCode = weatherCode;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

}
