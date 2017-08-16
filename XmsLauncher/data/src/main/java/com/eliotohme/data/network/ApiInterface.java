package com.eliotohme.data.network;

import com.eliotohme.data.Channel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("clientsingin")
    Call<Channel> CheckUserEmail(@Body Object channel);
}
