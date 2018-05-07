package com.eliotohme.data;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("room")
    private String room;

    @SerializedName("error")
    private int error;

    @SerializedName("access_token")
    private String access_token;

    private long tkn_expires_in;

    @SerializedName("token_type")
    private String token_type;

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setTkn_expires_in(long tkn_expires_in) {
        this.tkn_expires_in = tkn_expires_in;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public long getTkn_expires_in() {
        return tkn_expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public int getError() { return error; }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

}
