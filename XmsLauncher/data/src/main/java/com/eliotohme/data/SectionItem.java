package com.eliotohme.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

public class SectionItem extends RealmObject implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("section")
    @Expose
    private Integer section;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("poster")
    @Expose
    private String poster;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("reservation")
    @Expose
    private Integer reservation;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    /**
     * No args constructor for use in serialization
     *
     */
    public SectionItem() {
    }

    /**
     *
     * @param updatedAt
     * @param id
     * @param createdAt
     * @param description
     * @param reservation
     * @param name
     * @param poster
     * @param longitude
     * @param latitude
     * @param section
     */
    public SectionItem(Integer id, Integer section, String name, String poster, String description, Integer reservation, Double longitude, Double latitude, String createdAt, String updatedAt) {
        super();
        this.id = id;
        this.section = section;
        this.name = name;
        this.poster = poster;
        this.description = description;
        this.reservation = reservation;
        this.longitude = longitude;
        this.latitude = latitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReservation() {
        return reservation;
    }

    public void setReservation(Integer reservation) {
        this.reservation = reservation;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
