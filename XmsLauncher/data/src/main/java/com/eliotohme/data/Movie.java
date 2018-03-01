package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Movie extends RealmObject implements Serializable {
    @SerializedName("id")
    @PrimaryKey
    private int id;
    @SerializedName("stream")
    private Stream stream = null;
    @SerializedName("title")
    private String title;
    @SerializedName("poster")
    private String poster;

    /**
     * No args constructor for use in serialization
     */
    public Movie() {
    }

    public Movie(int id, Stream stream, String title, String poster) {
        this.id = id;
        this.stream = stream;
        this.title = title;
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}