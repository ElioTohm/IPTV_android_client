package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

public class Episode {
    @SerializedName("stream")
    private Stream stream;

    @SerializedName("name")
    private String name;

    public Episode() {
    }

    public Episode(Stream stream, String name) {
        this.stream = stream;
        this.name = name;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
