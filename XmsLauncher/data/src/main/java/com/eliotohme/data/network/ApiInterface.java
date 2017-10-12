package com.eliotohme.data.network;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API Interface for retofit library network calls
 */
public interface ApiInterface {
    @GET("/api/channel")
    Call<List<Channel>> getChannel();

    @GET("/api/registerDevice")
    Call<User> registerdevice(@Query("id") int id, @Query("secret") String secret);

    @GET("/api/clientInfo")
    Call<Client> getClientInfo(@Query("id") int user);
}
