package com.eliotohme.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Channel extends RealmObject {
    @PrimaryKey
    @SerializedName("number")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("stream")
    @Expose
    private String stream;

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("stream_type")
    @Expose
    private int stream_type;

    private RealmList<Genre> genres = null;

    public Channel() {}

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getStream() { return stream; }

    public void setStream(String stream) { this.stream = stream; }

    public String getThumbnail() { return thumbnail; }

    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public RealmList<Genre> getGenres() { return genres; }

    public void setGenres(RealmList<Genre> genres) { this.genres = genres; }

    public int getStream_type() {
        return stream_type;
    }

    public void setStream_type(int stream_type) {
        this.stream_type = stream_type;
    }
}