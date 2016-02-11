package francescoboschini.com.lightning;

public interface CurrentLocationInterface {
    void onLocationGot(MyLocation location);
    void onProviderDisabled();
    void onProviderEnabled();
    void onProviderDisabledSuggestion();
}
