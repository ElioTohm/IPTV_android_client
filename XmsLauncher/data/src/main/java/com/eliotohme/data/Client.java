package com.eliotohme.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Client extends RealmObject{
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("room")
    @Expose
    private int room;

    @SerializedName("welcome_message")
    @Expose
    private String welcomeMessage;

    @SerializedName("welcome_image")
    @Expose
    private String welcomeImage;

    @SerializedName("credit")
    @Expose
    private int credit;

    @SerializedName("debit")
    @Expose
    private int debit;

    public Client() {}

    /**
     * @param id
     * @param email
     * @param name
     * @param debit
     * @param credit
     * @param welcomeMessage
     * @param room
     */
    public Client(int id, String name, String email, int room, String welcomeMessage, int credit, int debit) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.room = room;
        this.welcomeMessage = welcomeMessage;
        this.credit = credit;
        this.debit = debit;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public int getRoom() { return room; }

    public void setRoom(int room) { this.room = room; }

    public Object getWelcomeMessage() { return welcomeMessage; }

    public void setWelcomeMessage(String welcomeMessage) { this.welcomeMessage = welcomeMessage; }

    public int getCredit() { return credit; }

    public void setCredit(int credit) { this.credit = credit; }

    public int getDebit() { return debit; }

    public void setDebit(int debit) { this.debit = debit; }

}
