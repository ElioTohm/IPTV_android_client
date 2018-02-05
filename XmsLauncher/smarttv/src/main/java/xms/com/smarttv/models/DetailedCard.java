package xms.com.smarttv.models;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

/**
 * Created by elio on 2/3/18.
 */

public class DetailedCard {

    @SerializedName("title") private String mTitle = "";
    @SerializedName("description") private String mDescription = "";
    @SerializedName("text") private String mText = "";
    @SerializedName("localImageResource") private String mLocalImageResource = null;
    @SerializedName("price") private String mPrice = null;
    @SerializedName("characters") private Card[] mCharacters = null;
    @SerializedName("recommended") private Card[] mRecommended = null;
    @SerializedName("year") private int mYear = 0;
    @SerializedName("trailerUrl") private String mTrailerUrl = null;
    @SerializedName("videoUrl") private String mVideoUrl = null;


    public String getPrice() {
        return mPrice;
    }

    public int getYear() {
        return mYear;
    }

    public String getLocalImageResource() {
        return mLocalImageResource;
    }

    public String getText() {
        return mText;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTrailerUrl() {
        return mTrailerUrl;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public Card[] getCharacters() {
        return mCharacters;
    }

    public Card[] getRecommended() {
        return mRecommended;
    }

    public int getLocalImageResourceId(Context context) {
        return context.getResources()
                .getIdentifier(getLocalImageResource(), "drawable", context.getPackageName());
    }
}

