package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Channel extends RealmObject {
    @PrimaryKey
    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    @SerializedName("stream")
    String stream;

    @SerializedName("thumbnail")
    String thumbnail;

    public  Channel () {}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStream() {
        return stream;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
