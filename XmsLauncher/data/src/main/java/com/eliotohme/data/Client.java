package com.eliotohme.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Client extends RealmObject{
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("room")
    @Expose
    private Integer room;
    @SerializedName("welcome_message")
    @Expose
    private String welcomeMessage;
    @SerializedName("welcome_image")
    @Expose
    private String welcomeImage;
    @SerializedName("balance")
    @Expose
    private Integer balance;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("purchases")
    @Expose
    private RealmList<Purchase> purchases = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Client() {
    }

    /**
     *
     * @param purchases
     * @param updatedAt
     * @param id
     * @param welcomeImage
     * @param email
     * @param createdAt
     * @param name
     * @param balance
     * @param welcomeMessage
     * @param room
     */
    public Client(Integer id, String name, String email, Integer room,
                  String welcomeMessage, String welcomeImage,
                  Integer balance, String createdAt, String updatedAt, RealmList<Purchase> purchases) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.room = room;
        this.welcomeMessage = welcomeMessage;
        this.welcomeImage = welcomeImage;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.purchases = purchases;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Client withName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Client withEmail(String email) {
        this.email = email;
        return this;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public Client withRoom(Integer room) {
        this.room = room;
        return this;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public Client withWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
        return this;
    }

    public String getWelcomeImage() {
        return welcomeImage;
    }

    public void setWelcomeImage(String welcomeImage) {
        this.welcomeImage = welcomeImage;
    }

    public Client withWelcomeImage(String welcomeImage) {
        this.welcomeImage = welcomeImage;
        return this;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Client withBalance(Integer balance) {
        this.balance = balance;
        return this;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Client withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Client withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(RealmList<Purchase> purchases) {
        this.purchases = purchases;
    }

    public Client withPurchases(RealmList<Purchase> purchases) {
        this.purchases = purchases;
        return this;
    }

}