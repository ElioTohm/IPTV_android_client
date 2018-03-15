package com.eliotohme.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Purchase Object
 * will be sent in purchase request
 * and retrieved in response to update purchased item on client side
 */
public class Purchase extends RealmObject {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("client_id")
    @Expose
    private Integer clientId;
    @SerializedName("purchasable_id")
    @Expose
    private Integer purchasableId;
    @SerializedName("purchasable_type")
    @Expose
    private String purchasableType;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("purchasable")
    @Expose
    private Purchasable purchasable;

    /**
     * No args constructor for use in serialization
     *
     */
    public Purchase() {
    }

    /**
     *
     * @param purchasableId
     * @param purchasableType
     */
    public Purchase(String purchasableType, int purchasableId) {
        super();
        this.purchasableType = purchasableType;
        this.purchasableId = purchasableId;
    }

    /**
     *
     * @param updatedAt
     * @param purchasableType
     * @param id
     * @param purchasableId
     * @param purchasable
     * @param createdAt
     * @param clientId
     */
    public Purchase(Integer id, Integer clientId, Integer purchasableId, String purchasableType, String createdAt, String updatedAt, Purchasable purchasable) {
        super();
        this.id = id;
        this.clientId = clientId;
        this.purchasableId = purchasableId;
        this.purchasableType = purchasableType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.purchasable = purchasable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Purchase withId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Purchase withClientId(Integer clientId) {
        this.clientId = clientId;
        return this;
    }

    public Integer getPurchasableId() {
        return purchasableId;
    }

    public void setPurchasableId(Integer purchasableId) {
        this.purchasableId = purchasableId;
    }

    public Purchase withPurchasableId(Integer purchasableId) {
        this.purchasableId = purchasableId;
        return this;
    }

    public String getPurchasableType() {
        return purchasableType;
    }

    public void setPurchasableType(String purchasableType) {
        this.purchasableType = purchasableType;
    }

    public Purchase withPurchasableType(String purchasableType) {
        this.purchasableType = purchasableType;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Purchase withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Purchase withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Purchasable getPurchasable() {
        return purchasable;
    }

    public void setPurchasable(Purchasable purchasable) {
        this.purchasable = purchasable;
    }

    public Purchase withPurchasable(Purchasable purchasable) {
        this.purchasable = purchasable;
        return this;
    }

}