package com.eliotohme.data.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private String auth_token;
    private String token_type;

    public AuthenticationInterceptor(String type, String token) {
        this.auth_token = token;
        this.token_type = type;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Accept","application/json")
                .header("Authorization", this.token_type + " " + this.auth_token);

        Request request = builder.build();
        return chain.proceed(request);
    }
}
