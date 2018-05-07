package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

public class Rating {

    @SerializedName("source")
    private String source;
    @SerializedName("value")
    private String value;

    public Rating() {
    }

    /**
     *
     * @param source
     * @param value
     */
    public Rating(String source, String value) {
        super();
        this.source = source;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
