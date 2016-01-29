package francescoboschini.com.lightning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import francescoboschini.com.lightning.Utils.StringUtils;
import francescoboschini.com.lightning.Utils.WeatherIconHandler;

public class ForecastListAdapter extends ArrayAdapter<ForecastItem> {

    public ForecastListAdapter(Context context, int textViewResourceId, List<ForecastItem> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.forecast_item_raw, null);
            viewHolder = new ViewHolder();

            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.temperature = (TextView) convertView.findViewById(R.id.temperature);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.weatherIcon = (ImageView) convertView.findViewById(R.id.weather_icon);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ForecastItem forecastItem = getItem(position);
        viewHolder.temperature.setText(StringUtils.formatTemperature(forecastItem.getTemperature()) + getContext().getResources().getString(R.string.celsius_degrees));
        viewHolder.description.setText(StringUtils.toFirstCharUpperCase(forecastItem.getDescription()));
        viewHolder.date.setText(StringUtils.simpleFormatLongDate(forecastItem.getDate()));
        new WeatherIconHandler(getContext()).setIcon(viewHolder.weatherIcon, forecastItem.getWeatherCode());

        return convertView;
    }

    private class ViewHolder {
        public TextView description;
        public TextView date;
        public TextView temperature;
        public ImageView weatherIcon;
    }
}