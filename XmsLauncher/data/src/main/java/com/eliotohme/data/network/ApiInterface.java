package com.eliotohme.data.network;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.User;
import com.eliotohme.data.Weather;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
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

    @GET("/api/registerDevice")
    Call<User> registerdevice(@Query("id") int id, @Query("secret") String secret);

    @GET("/api/clientInfo")
    Call<Client> getClientInfo(@Query("id") int user);

    @GET("v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22beirut%2C%20lb%22)%20and%20u%3D'c'&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
    Call<Weather> getWeather();

    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
