package com.eliotohme.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Polymorphic relationship sent back from server
 * returning price name image respective to the object
 */
public class Purchasable extends RealmObject {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("price")
    @Expose
    private Integer price;
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
    public Purchasable() {
    }

    /**
     *
     * @param updatedAt
     * @param id
     * @param price
     * @param thumbnail
     * @param createdAt
     * @param name
     * @param number
     */
    public Purchasable(Integer id, Integer number, String name, String thumbnail,
                       Integer price, String createdAt, String updatedAt) {
        super();
        this.id = id;
        this.number = number;
        this.name = name;
        this.thumbnail = thumbnail;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Purchasable withId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Purchasable withNumber(Integer number) {
        this.number = number;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Purchasable withName(String name) {
        this.name = name;
        return this;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Purchasable withThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Purchasable withPrice(Integer price) {
        this.price = price;
        return this;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Purchasable withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Purchasable withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

}
