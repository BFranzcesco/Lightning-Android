package lightning.francescoboschini.com.lightning;

import java.util.Locale;

public class Utils {

    public static String toFirstCharUpperCase(String string) {
        if (string.length() >= 1)
            string = string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1);
        return string;
    }
}
