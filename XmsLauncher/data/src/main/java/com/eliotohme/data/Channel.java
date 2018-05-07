package com.eliotohme.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Channel extends RealmObject implements Serializable {
//    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("genres")
    @Expose
    private RealmList<Genre> genres = null;
    @SerializedName("stream")
    @Expose
    private Stream stream;

    boolean purchased = false;

    public Channel() {
    }

    public Channel(Integer number, String name, String thumbnail, RealmList<Genre> genres, Stream stream) {
        this.number = number;
        this.name = name;
        this.thumbnail = thumbnail;
        this.genres = genres;
        this.stream = stream;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(RealmList<Genre> genres) {
        this.genres = genres;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}