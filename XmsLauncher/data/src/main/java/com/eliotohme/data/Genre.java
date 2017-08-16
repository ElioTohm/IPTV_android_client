package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Genre extends RealmObject {
    @PrimaryKey
    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    public Genre  (int id, String name, String stream, String thumbnail) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
