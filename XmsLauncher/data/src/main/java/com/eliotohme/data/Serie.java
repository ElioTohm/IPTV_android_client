package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Serie {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("poster")
    private String poster;
    @SerializedName("season")
    private List<Integer> season;
    @SerializedName("episode")
    private List<Episode> episode;
    @SerializedName("imdbinfo")
    private ImdbInfo imdbInfo;

    public Serie() {
    }

    public Serie(int id, String title, String poster, List<Integer> season, List<Episode> episode, ImdbInfo imdbInfo) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.season = season;
        this.episode = episode;
        this.imdbInfo = imdbInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Integer> getSeason() {
        return season;
    }

    public void setSeason(List<Integer> season) {
        this.season = season;
    }

    public List<Episode> getEpisode() {
        return episode;
    }

    public void setEpisode(List<Episode> episode) {
        this.episode = episode;
    }

    public ImdbInfo getImdbInfo() {
        return imdbInfo;
    }

    public void setImdbInfo(ImdbInfo imdbInfo) {
        this.imdbInfo = imdbInfo;
    }
}
