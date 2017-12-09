package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImdbInfo {
    @SerializedName("year")
    private String year;
    @SerializedName("rated")
    private String rated;
    @SerializedName("released")
    private String released;
    @SerializedName("runtime")
    private String runtime;
    @SerializedName("genre")
    private String genre;
    @SerializedName("director")
    private String director;
    @SerializedName("writer")
    private String writer;
    @SerializedName("actors")
    private String actors;
    @SerializedName("plot")
    private String plot;
    @SerializedName("language")
    private String language;
    @SerializedName("country")
    private String country;
    @SerializedName("awards")
    private String awards;
    @SerializedName("ratings")
    private List<Rating> ratings = null;
    @SerializedName("metascore")
    private String metascore;
    @SerializedName("imdbRating")
    private String imdbRating;
    @SerializedName("imdbVotes")
    private String imdbVotes;
    @SerializedName("type")
    private String type;
    @SerializedName("boxOffice")
    private String boxOffice;
    @SerializedName("production")
    private String production;

    public ImdbInfo() {
    }

    public ImdbInfo(String year, String rated, String released, String runtime, String genre, String director, String writer, String actors,
                    String plot, String language, String country, String awards, List<Rating> ratings, String metascore, String imdbRating,
                    String imdbVotes, String type, String boxOffice, String production) {
        this.year = year;
        this.rated = rated;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
        this.director = director;
        this.writer = writer;
        this.actors = actors;
        this.plot = plot;
        this.language = language;
        this.country = country;
        this.awards = awards;
        this.ratings = ratings;
        this.metascore = metascore;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
        this.type = type;
        this.boxOffice = boxOffice;
        this.production = production;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getMetascore() {
        return metascore;
    }

    public void setMetascore(String metascore) {
        this.metascore = metascore;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(String imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(String boxOffice) {
        this.boxOffice = boxOffice;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }
}
