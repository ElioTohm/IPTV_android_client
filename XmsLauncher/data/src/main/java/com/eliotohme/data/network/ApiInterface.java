package com.eliotohme.data.network;

import com.eliotohme.data.Channel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @GET("channel")
    Call<List<Channel>> getChannel();
}
