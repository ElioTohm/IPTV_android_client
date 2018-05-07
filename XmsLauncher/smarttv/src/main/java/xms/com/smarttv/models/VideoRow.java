package xms.com.smarttv.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by elio on 2/3/18.
 */

public class VideoRow {
    @SerializedName("category") private String mCategory = "";
    @SerializedName("videos") private List<VideoCard> mVideos;

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public List<VideoCard> getVideos() {
        return mVideos;
    }

    public void setVideos(List<VideoCard> videos) {
        mVideos = videos;
    }
}
