package com.eliotohme.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class InstalledApps extends RealmObject {

    private String appname = "";
    @PrimaryKey
    private String pname = "";
    private String versionName = "";
    private int versionCode = 0;

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getAppname() {
        return appname;
    }
}
