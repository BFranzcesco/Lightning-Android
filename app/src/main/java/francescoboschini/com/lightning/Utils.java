package francescoboschini.com.lightning;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String DATE_FORMAT = "EEE dd MMM yyyy, HH:mm";
    public static final String SIMPLE_DATE_FORMAT = "EEE dd MMM, HH:mm";
    private static final DateFormat lastUpdateFormat = new SimpleDateFormat(DATE_FORMAT);
    private static final DateFormat lastUpdateSimpleFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);

    public static String toFirstCharUpperCase(String string) {
        if (string.length() >= 1)
            string = string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1);
        return string;
    }

    public static String formatLongDate(Long date) {
        return lastUpdateFormat.format(new Date(date * 1000));
    }

    public static String simpleFormatLongDate(Long date) {
        return lastUpdateSimpleFormat.format(new Date(date * 1000));
    }

}
