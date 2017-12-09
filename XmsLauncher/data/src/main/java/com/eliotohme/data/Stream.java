package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Stream extends RealmObject {
    public int TYPE_UDP = 1;
    public int TYPE_HLS = 2;
    public int DASH = 3;
    public int SS = 4;
    public int MISC = 5;

    @SerializedName("vid_stream")
    private String vid_stream;
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
}
