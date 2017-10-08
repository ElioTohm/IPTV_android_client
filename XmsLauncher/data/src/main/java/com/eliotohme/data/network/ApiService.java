package com.eliotohme.data.network;

import android.support.v4.BuildConfig;
import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    public static final String BASE_URL  = "http://xmsgit.ddns.net/";//"http://192.168.0.78/";
    public static final String SOCKET_URL = "http://xmsgit.ddns.net:6001";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL )
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(
            Class<S> serviceClass) {
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor debuginterceptor = new HttpLoggingInterceptor();
                debuginterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

                httpClient.addInterceptor(debuginterceptor);
                httpClient.readTimeout(20, TimeUnit.SECONDS);
                httpClient.connectTimeout(20, TimeUnit.SECONDS);
            }


            builder.client(httpClient.build());
            retrofit = builder.build();

        return retrofit.create(serviceClass);
    }

    public static <S> S createService(
            Class<S> serviceClass, final String tokenType, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(tokenType, authToken);

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor debuginterceptor = new HttpLoggingInterceptor();
                debuginterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

                httpClient.addInterceptor(debuginterceptor);
            }

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                httpClient.readTimeout(20, TimeUnit.SECONDS);
                httpClient.connectTimeout(20, TimeUnit.SECONDS);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
}

