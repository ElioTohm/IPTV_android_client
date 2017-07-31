package com.XmsPro.xmsproplayer.data;


import android.net.Uri;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Channel extends RealmObject {
    @PrimaryKey
    private int windowid;
    private String uri;
    private String name;
    private String description;

    public  Channel () {}

    public Channel (String uri, String name, String description, int windowid) {
        this.uri = uri;
        this.name = name;
        this.description = description;
        this.windowid = windowid;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setWindowid(int windowid) {
        this.windowid = windowid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
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
