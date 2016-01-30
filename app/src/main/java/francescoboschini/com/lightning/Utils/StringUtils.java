package francescoboschini.com.lightning.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtils {

    public static final String SIMPLE_DATE_FORMAT = "EEE dd MMM, HH:mm";
    public static final String HOUR_FORMAT = "HHmm";
    public static final String TEMPERATURE_FORMAT = "%.1f";
    private static final DateFormat lastUpdateSimpleFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
    private static final DateFormat hourFormat = new SimpleDateFormat(HOUR_FORMAT);

    public static String toFirstCharUpperCase(String string) {
        if (string.length() >= 1)
            string = string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1);
        return string;
    }

    public static String simpleFormatLongDate(Long date) {
        return lastUpdateSimpleFormat.format(new Date(date * 1000));
    }

    public static String formatTemperature(Double temperature) {
        return String.format(TEMPERATURE_FORMAT, temperature);
    }

    public static String formatHour(long date) {
        return hourFormat.format(date);
    }

}
