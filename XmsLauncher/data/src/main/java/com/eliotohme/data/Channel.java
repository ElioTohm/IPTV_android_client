package com.eliotohme.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Channel extends RealmObject {
    @PrimaryKey
    private int window_id;
    private String uri;
    private String name;
    private String description;
    private int bundle_id;

    public  Channel () {}

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setWindowid(int window_id) {
        this.window_id = window_id;
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

    public int getWindow_id() {
        return window_id;
    }

    public void setWindow_id(int window_id) {
        this.window_id = window_id;
    }

    public int getBundle_id() {
        return bundle_id;
    }

    public void setBundle_id(int bundle_id) {
        this.bundle_id = bundle_id;
    }

    public int getWindowid() {
        return window_id;
    }

}
