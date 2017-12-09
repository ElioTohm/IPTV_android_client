package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("id")
    private int id;
    @SerializedName("stream")
    private Stream stream = null;
    @SerializedName("title")
    private String title;
    @SerializedName("poster")
    private String poster;
    @SerializedName("imdbinfo")
    private ImdbInfo imdbInfo;

    /**
     * No args constructor for use in serialization
     */
    public Movie() {
    }

    public Movie(int id, Stream stream, String title, String poster, ImdbInfo imdbInfo) {
        this.id = id;
        this.stream = stream;
        this.title = title;
        this.poster = poster;
        this.imdbInfo = imdbInfo;
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

    public ImdbInfo getImdbInfo() {
        return imdbInfo;
    }

    public void setImdbInfo(ImdbInfo imdbInfo) {
        this.imdbInfo = imdbInfo;
    }
}