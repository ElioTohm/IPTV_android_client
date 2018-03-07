package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Stream extends RealmObject {
    public final static int TYPE_UDP = 1;
    public final static int TYPE_HLS = 2;
    public final static int DASH = 3;
    public final static int SS = 4;
    public final static int MISC = 5;

    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("vid_stream")
    private String vid_stream;
    @SerializedName("trailer_stream")
    private String trailer_stream;
    @SerializedName("sub_stream")
    private String sub_stream;
    @SerializedName("type")
    private int type;

    public Stream() {
    }

    public Stream(String vid_stream, String sub_stream, int type) {
        this.vid_stream = vid_stream;
        this.sub_stream = sub_stream;
        this.type = type;
    }

    public String getVid_stream() {
        return vid_stream;
    }

    public void setVid_stream(String vid_stream) {
        this.vid_stream = vid_stream;
    }

    public String getSub_stream() {
        return sub_stream;
    }

    public void setSub_stream(String sub_stream) {
        this.sub_stream = sub_stream;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTrailer_stream() {
        return trailer_stream;
    }

    public void setTrailer_stream(String trailer_stream) {
        this.trailer_stream = trailer_stream;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
