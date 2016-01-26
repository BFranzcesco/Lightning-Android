package francescoboschini.com.lightning;

public class ForecastItem {

    protected double temperature;
    protected String description;
    protected long lastUpdate;
    protected int weatherCode;

    public ForecastItem(double temperature, String description, long lastUpdate, int weatherCode) {
        this.temperature = temperature;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.weatherCode = weatherCode;
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

}
