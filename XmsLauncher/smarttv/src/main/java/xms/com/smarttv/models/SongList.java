package xms.com.smarttv.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by elio on 2/3/18.
 */

public class SongList {

    @SerializedName("songs") private List<Song> mSongs;

    public List<Song> getSongs() {
        return mSongs;
    }

}
