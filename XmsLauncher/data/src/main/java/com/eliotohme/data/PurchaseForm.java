package com.eliotohme.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created as a JSON objec to be sent as body
 * in purchase request
 */
public class PurchaseForm {

    @SerializedName("client_id")
    private int client_id;

    @SerializedName("purchases")
    private List<Purchase> purchases;

    public PurchaseForm(int client_id, List<Purchase> purchases) {
        this.client_id = client_id;
        this.purchases = purchases;
    }

}
