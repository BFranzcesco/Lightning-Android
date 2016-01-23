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

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public long getDate() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
