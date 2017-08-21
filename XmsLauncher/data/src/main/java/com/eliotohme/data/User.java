package com.eliotohme.data;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("access_token")
    private String access_token;

    @SerializedName("expires_in")
    private long tkn_expires_in;

    @SerializedName("token_type")
    private String token_type;

    public User() {}

    public String getAccess_token() {
        return access_token;
    }

    public long getTkn_expires_in() {
        return tkn_expires_in;
    }

    public String getToken_type() {
        return token_type;
    }
}
