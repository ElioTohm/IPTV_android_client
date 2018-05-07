package com.eliotohme.data.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    public static <S> S createService(Class<S> serviceClass, String BASE_URL) {
        return createService(serviceClass, BASE_URL, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String BASE_URL, final String tokenType, final String authToken) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

//        if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor debuginterceptor = new HttpLoggingInterceptor();
        debuginterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        httpClient.addInterceptor(debuginterceptor);
//        }

        if (authToken != null && tokenType != null) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(tokenType, authToken);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
            }
        }
        httpClient.readTimeout(10, TimeUnit.SECONDS);
        httpClient.connectTimeout(10, TimeUnit.SECONDS);

        builder.client(httpClient.build());
        retrofit = builder.build();


        return retrofit.create(serviceClass);
    }
}

