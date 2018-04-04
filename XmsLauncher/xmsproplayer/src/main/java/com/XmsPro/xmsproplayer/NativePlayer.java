package com.XmsPro.xmsproplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.VideoView;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.eliotohme.data.Channel;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

/**
 * todo use native mediaplayer
 */

public class NativePlayer {
    MediaPlayer mediaPlayer;
    private String userAgent;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer player;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private List<Channel> channelArrayList;
    private VideoView videoView;
    private Context context;
    private static NativePlayer instance;
    private XmsPlayerUICallback xmsPlayerUICallback;
    private String TOKENTYPE, TOKEN;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public static NativePlayer getPlayerInstance() {
        if(instance !=null){
            return instance;
        }
        return null;
    }

    /**
     * @param context
     * @param videoView
     * initialize both param to use in class
     */
    public NativePlayer(Context context, VideoView videoView,
                     List<Channel> channelArrayList, String TOKENTYPE, String TOKEN) {

        // set surface of the player
        this.context = context;
        this.xmsPlayerUICallback = (XmsPlayerUICallback) context;
        this.videoView = videoView;
        this.channelArrayList = channelArrayList;
        this.TOKEN = TOKEN;
        this.TOKENTYPE = TOKENTYPE;
        instance = this;
    }

    private Uri buildMediaSource(List<Channel> channelArrayList) {
            return Uri.parse(channelArrayList.get(0).getStream().getVid_stream());
    }

    /**
     * initializing player calling buildUDPMediaSource
     * and showChannelInfo
     */
    public void initializePlayer() {
        changeSource(channelArrayList, false);
        videoView.start();
    }

    /**
     * release player on activity destroyed
     */
    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }


    /**
     * @param channelList
     * @param showinfo
     * change source of stream and set if information about stream should show with respect to showinfo flag
     */
    public void changeSource(List<Channel> channelList, boolean showinfo) {
        Uri stream = buildMediaSource(channelArrayList);
        videoView.setVideoURI(stream);
//        try {
//            mediaPlayer.setDataSource(context, myUri);
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (showinfo) {
            xmsPlayerUICallback.showChannelInfo(channelList.get(0).getNumber(), 1000, false);
        }
    }

}

