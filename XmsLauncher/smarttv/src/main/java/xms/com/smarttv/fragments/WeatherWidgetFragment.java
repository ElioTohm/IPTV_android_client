package xms.com.smarttv.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eliotohme.data.User;
import com.eliotohme.data.Weather;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.Presenter.WeatherRecyclerViewAdapter;
import xms.com.smarttv.R;
import xms.com.smarttv.app.Preferences;

public class WeatherWidgetFragment extends Fragment {
    Weather weather;

    public WeatherWidgetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather_widget, container, false);
        Context context = view.getContext();
        User user = Realm.getDefaultInstance().where(User.class).findFirst();
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, Preferences.getServerUrl(), user.getToken_type(), user.getAccess_token());

        final RecyclerView forcastRecyclerView = view.findViewById(R.id.forcast_recycler_view);
        forcastRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        final TextView temperaturehigh = view.findViewById(R.id.weather_temperaturehigh);
        final TextView temperaturelow = view.findViewById(R.id.weather_temperaturelow);
        final ImageView weatherIcon = view.findViewById(R.id.weather_icon_widget);

        final TextView windspeed = view.findViewById(R.id.wind);
        final TextView sunrise = view.findViewById(R.id.sunrise);
        final TextView sunset = view.findViewById(R.id.sunset);
        final ImageView wind_icon = view.findViewById(R.id.wind_icon);
        final ImageView sunset_icon = view.findViewById(R.id.sunset_icon);
        final ImageView sunrise_icon = view.findViewById(R.id.sunrise_icon);

        final Call<Weather> weatherCall = apiInterface.getWeather();

        weatherCall.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                weather = response.body();

                temperaturehigh.setText(String.format("%s \u2103", weather.getQuery().getResults().getChannel().getItem().getForecast().get(0).getHigh()));
                temperaturelow.setText(String.format("%s \u2103", weather.getQuery().getResults().getChannel().getItem().getForecast().get(0).getLow()));
                windspeed.setText(String.format("%s %s", weather.getQuery().getResults().getChannel().getWind().getSpeed(),
                weather.getQuery().getResults().getChannel().getUnits().getSpeed()));
                sunrise.setText(String.format("%s", weather.getQuery().getResults().getChannel().getAstronomy().getSunrise()));
                sunset.setText(String.format("%s", weather.getQuery().getResults().getChannel().getAstronomy().getSunset()));


                int resource = 0 ;
                int code = Integer.parseInt(weather.getQuery().getResults().getChannel().getItem().getForecast().get(0).getCode());
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

                Glide.with(getActivity())
                        .load(getActivity().getResources().getDrawable(resource))
                        .into(weatherIcon);

                Glide.with(getActivity())
                        .load(getActivity().getResources().getDrawable(R.drawable.windsock))
                        .into(wind_icon);

                Glide.with(getActivity())
                        .load(getActivity().getResources().getDrawable(R.drawable.sunrise))
                        .into(sunrise_icon);

                Glide.with(getActivity())
                        .load(getActivity().getResources().getDrawable(R.drawable.sunset))
                        .into(sunset_icon);

                List<Weather.Forecast> forecastList = new ArrayList<>();
                forecastList.add(weather.getQuery().getResults().getChannel().getItem().getForecast().get(1));
                forecastList.add(weather.getQuery().getResults().getChannel().getItem().getForecast().get(2));
                forecastList.add(weather.getQuery().getResults().getChannel().getItem().getForecast().get(3));
                forecastList.add(weather.getQuery().getResults().getChannel().getItem().getForecast().get(4));
                forecastList.add(weather.getQuery().getResults().getChannel().getItem().getForecast().get(5));
                forecastList.add(weather.getQuery().getResults().getChannel().getItem().getForecast().get(6));
                WeatherRecyclerViewAdapter weatherRecyclerViewAdapter = new WeatherRecyclerViewAdapter(forecastList);
                forcastRecyclerView.setAdapter(weatherRecyclerViewAdapter);
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {

            }
        });


        return view;
    }

}
