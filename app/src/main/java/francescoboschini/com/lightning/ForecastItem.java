package francescoboschini.com.lightning;

public class ForecastItem {

    protected double temperature;
    protected String description;
    protected long lastUpdate;
    protected int weatherCode;
    private long sunrise;
    private long sunset;

    public ForecastItem(double temperature, String description, long lastUpdate, int weatherCode, long sunrise, long sunset) {
        this.temperature = temperature;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.weatherCode = weatherCode;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public double getTemperature() {
        return temperature;
    }

    public long getDate() {
        return lastUpdate;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public String getDescription() {
        return description;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }
}
