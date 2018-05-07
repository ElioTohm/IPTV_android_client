package xms.com.smarttv.models;

import android.content.Context;
import android.support.v17.leanback.widget.MultiActionsProvider;

import com.google.gson.annotations.SerializedName;

/**
 * Created by elio on 2/3/18.
 */

public class Song implements MultiActionsProvider {

    @SerializedName("title") private String mTitle = "";
    @SerializedName("description") private String mDescription = "";
    @SerializedName("text") private String mText = "";
    @SerializedName("image") private String mImage = null;
    @SerializedName("file") private String mFile = null;
    @SerializedName("duration") private String mDuration = null;
    @SerializedName("number") private int mNumber = 0;
    @SerializedName("favorite") private boolean mFavorite = false;

    private MultiAction[] mMediaRowActions;


    public void setMediaRowActions(MultiAction[] mediaRowActions) {
        mMediaRowActions = mediaRowActions;
    }

    public MultiAction[] getMediaRowActions() {
        return mMediaRowActions;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getText() {
        return mText;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public int getFileResource(Context context) {
        return context.getResources()
                .getIdentifier(mFile, "raw", context.getPackageName());
    }

    public int getImageResource(Context context) {
        return context.getResources()
                .getIdentifier(mImage, "drawable", context.getPackageName());
    }

    @Override
    public MultiAction[] getActions() {
        return mMediaRowActions;
    }

}
