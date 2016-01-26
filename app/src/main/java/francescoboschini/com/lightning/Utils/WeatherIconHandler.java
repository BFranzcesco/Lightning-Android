package francescoboschini.com.lightning.Utils;

import android.content.Context;
import android.widget.ImageView;

import java.util.Date;

import francescoboschini.com.lightning.R;

public class WeatherIconHandler {

    // GET MILLISEC from DATE http://www.fileformat.info/tip/java/date2millis.htm
    public static final int DEFAULT_SUNRISE_TIME = 1;
    public static final int DEFAULT_SUNSET_TIME = 2000000000;
    private Context context;

    public WeatherIconHandler(Context context) {
        this.context = context;
    }

    public void setIcon(ImageView imageView, int weatherId) {
        setIconBasedOnCurrentTime(imageView, weatherId, DEFAULT_SUNRISE_TIME, DEFAULT_SUNSET_TIME);
    }

    public void setIconBasedOnCurrentTime(ImageView imageView, int weatherId, long sunrise, long sunset) {
        sunrise = sunrise * 1000;
        sunset = sunset * 1000;

        if (weatherId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.sunny));
            } else {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.clear_night));
            }
        } else {
            switch (weatherId / 100) {
                case 2:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.thunder));
                    break;
                case 3:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.drizzle));
                    break;
                case 7:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.foggy));
                    break;
                case 8:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cloudy));
                    break;
                case 6:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.snowy));
                    break;
                case 5:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.rainy));
                    break;
            }
        }
    }
}
