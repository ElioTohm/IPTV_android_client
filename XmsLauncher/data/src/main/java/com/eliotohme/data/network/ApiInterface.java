package com.eliotohme.data.network;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("api/channel")
    Call<List<Channel>> getChannel();

    @FormUrlEncoded
    @POST("api/registerDevice")
    Call<User> registerdevice(@Query("id") int id,@Query("secret") String secret);

    @GET("api/clientInfo")
    Call<Client> getClientInfo(@Query("id") int user);
}
