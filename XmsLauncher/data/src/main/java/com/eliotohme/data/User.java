package com.eliotohme.data;


import io.realm.RealmObject;

public class User extends RealmObject {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
