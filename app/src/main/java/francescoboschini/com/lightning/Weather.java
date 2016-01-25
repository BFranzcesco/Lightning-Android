package francescoboschini.com.lightning;

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

    public long getLastUpdate() {
        return lastUpdate;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public long getSunrise() {
        return sunrise;
    }


    public long getSunset() {
        return sunset;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public String getCountry() {
        return country;
    }

    public String getCityName() {
        return cityName;
    }
}
