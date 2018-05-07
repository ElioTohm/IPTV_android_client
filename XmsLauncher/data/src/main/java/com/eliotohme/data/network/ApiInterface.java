package com.eliotohme.data.network;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.HotelService;
import com.eliotohme.data.Movie;
import com.eliotohme.data.Section;
import com.eliotohme.data.User;
import com.eliotohme.data.Weather;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * API Interface for retofit library network calls
 */
public interface ApiInterface {
    @GET("/api/launcherUpdate")
    Call<ResponseBody> checkUpdate(@Query("version") double version);

    @GET("/api/channel")
    Call<List<Channel>> getChannel();

    @GET("/api/vodmovies")
    Call<List<Movie>> getMovies();

    @GET("/api/sections")
    Call<List<Section>> getSections();

    @GET("/api/registerDevice")
    Call<User> registerdevice(@Query("id") int id, @Query("secret") String secret);

    @GET("/api/clientInfo")
    Call<Client> getClientInfo(@Query("id") int user);

    @POST("/api/clientpurchase")
    Call<Client> purchaseItem(@Body Object client);

    @GET("/api/weather")
    Call<Weather> getWeather();

    @GET("/api/services")
    Call<List<HotelService>> getService();



    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
