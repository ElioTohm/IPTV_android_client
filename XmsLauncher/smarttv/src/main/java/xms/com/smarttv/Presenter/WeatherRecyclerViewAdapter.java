package xms.com.smarttv.Presenter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eliotohme.data.Weather;

import java.util.List;

import xms.com.smarttv.R;

public class WeatherRecyclerViewAdapter  extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.ViewHolder> {

    private final List<Weather.Forecast> mValues;

    public WeatherRecyclerViewAdapter(List<Weather.Forecast> items) {
        mValues = items;
    }

    @Override
    public WeatherRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_adapter, parent, false);
        return new WeatherRecyclerViewAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final WeatherRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.section = mValues.get(position);
        holder.day.setText(holder.section.getDay());
        holder.high.setText(holder.section.getHigh());
        holder.low.setText(holder.section.getLow());

        int resource = 0 ;
        int code = Integer.parseInt(holder.section.getCode());
        if ( 26 >= code &&  code >= 30) {
            //cloudy
            resource = R.drawable.partlycloudy;
        } else if (code <= 17 ) {
            if ( code <= 10) {
                resource = R.drawable.rain;
            } else {
                resource = R.drawable.storm;
            }
        } else if (code >= 31 && code <= 34) {
            resource = R.drawable.sun;
        } else {
            resource = R.drawable.partlycloudy;
        }

        Glide.with(holder.mView.getContext())
                .load(holder.mView.getContext().getResources().getDrawable(resource))
                .into(holder.weathericon);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView day, high, low;
        final ImageView weathericon;
        Weather.Forecast section;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            day = view.findViewById(R.id.day);
            high = view.findViewById(R.id.high);
            low = view.findViewById(R.id.low);
            weathericon = view.findViewById(R.id.code);
        }
    }
}
