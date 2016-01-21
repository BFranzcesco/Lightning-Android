package francescoboschini.com.lightning;

import android.content.Context;
import android.widget.ImageView;

import java.util.Date;

import lightning.francescoboschini.com.lightning.R;

public class WeatherIconHandler {

    private Context context;

    public WeatherIconHandler(Context context) {
        this.context = context;
    }

    public void setWeatherIcon(ImageView image, int actualId, long sunrise, long sunset) {
        sunrise = sunrise * 1000;
        sunset = sunset * 1000;

        int id = actualId / 100;

        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                image.setImageDrawable(context.getResources().getDrawable(R.drawable.sunny));
            } else {
                image.setImageDrawable(context.getResources().getDrawable(R.drawable.clear_night));
            }
        } else {
            switch (id) {
                case 2:
                    image.setImageDrawable(context.getResources().getDrawable(R.drawable.thunder));
                    break;
                case 3:
                    image.setImageDrawable(context.getResources().getDrawable(R.drawable.drizzle));
                    break;
                case 7:
                    image.setImageDrawable(context.getResources().getDrawable(R.drawable.foggy));
                    break;
                case 8:
                    image.setImageDrawable(context.getResources().getDrawable(R.drawable.cloudy));
                    break;
                case 6:
                    image.setImageDrawable(context.getResources().getDrawable(R.drawable.snowy));
                    break;
                case 5:
                    image.setImageDrawable(context.getResources().getDrawable(R.drawable.rainy));
                    break;
            }
        }
    }
}
