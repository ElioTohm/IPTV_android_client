package xms.com.smarttv.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by elio on 2/3/18.
 */

public class Movie implements Serializable {

    private static final long serialVersionUID = 133742L;

    @SerializedName("title")
    private String mTitle = "";
    @SerializedName("price_hd")
    private String mPriceHd = "n/a";
    @SerializedName("price_sd")
    private String mPriceSd = "n/a";
    @SerializedName("breadcrump")
    private String mBreadcrump = "";

    public String getTitle() {
        return mTitle;
    }

    public String getBreadcrump() {
        return mBreadcrump;
    }

    public String getPriceHd() {
        return mPriceHd;
    }

    public String getPriceSd() {
        return mPriceSd;
    }

}
