package com.eliotohme.data.network;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("api/channel")
    Call<List<Channel>> getChannel();

    @FormUrlEncoded
    @POST("oauth/token")
    Call<User> registerdevice(@Field("grant_type") String client_credentials,
                              @Field("client_id") int id,
                              @Field("client_secret") String secret,
                              @Field("scope") String scope);

    @GET("api/clientInfo")
    Call<Client> getClientInfo(@Query("id") int user);
}
