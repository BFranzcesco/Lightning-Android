package francescoboschini.com.lightning.Utils;

import android.content.Context;
import android.widget.ImageView;

import java.util.Date;

import francescoboschini.com.lightning.R;

public class WeatherIconHandler {

    private Context context;

    public WeatherIconHandler(Context context) {
        this.context = context;
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

    public void setIconBasedOnTime(ImageView imageView, int weatherCode, long date, long sunrise, long sunset) {

        long myDate = Long.parseLong(StringUtils.formatHour(date*1000), 10);
        long mySunset = Long.parseLong(StringUtils.formatHour(sunset*1000), 10);
        long mySunrise = Long.parseLong(StringUtils.formatHour(sunrise*1000), 10);

        if (weatherCode == 800) {
            if (myDate >= mySunrise && myDate < mySunset) {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.sunny));
            } else {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.clear_night));
            }
        } else {
            switch (weatherCode / 100) {
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
