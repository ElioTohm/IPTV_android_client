package com.XmsPro.xmsproplayer.data;


import android.net.Uri;

public class Channel {
    private Uri uri;
    private String name;
    private String description;
    private int windowid;

    public  Channel () {}

    public Channel (Uri uri, String name, String description, int windowid) {
        this.uri = uri;
        this.name = name;
        this.description = description;
        this.windowid = windowid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWindowid() {
        return windowid;
    }

}
