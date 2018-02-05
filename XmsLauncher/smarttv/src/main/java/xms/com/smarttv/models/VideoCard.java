package xms.com.smarttv.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by elio on 2/3/18.
 */

public class VideoCard extends Card {

    @SerializedName("sources") private String mVideoSource = "";
    @SerializedName("background") private String mBackgroundUrl = "";
    @SerializedName("studio") private String mStudio = "";

    public VideoCard() {
        super();
        setType(Type.VIDEO_GRID);
    }

    public String getVideoSource() {
        return mVideoSource;
    }

    public void setVideoSource(String sources) {
        mVideoSource = sources;
    }

    public String getBackground() {
        return mBackgroundUrl;
    }

    public void setBackground(String background) {
        mBackgroundUrl = background;
    }

    public String getStudio() {
        return mStudio;
    }

    public void setStudio(String studio) {
        mStudio = studio;
    }
}
