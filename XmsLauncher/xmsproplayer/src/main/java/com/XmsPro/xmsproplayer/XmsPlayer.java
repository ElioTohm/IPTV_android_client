package com.XmsPro.xmsproplayer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.eliotohme.data.Channel;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.UdpDataSource;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES;

public class XmsPlayer  {
    String TAG  = "xms";
    private SimpleExoPlayer player;
    boolean playWhenReady;
    int currentWindow;
    long playbackPosition;
    private EventLogger eventlogger;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private List<Channel> channelArrayList;
    private int channellistSize;
    private SimpleExoPlayerView simpleExoPlayerView;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private int USER_NAME;
    private Context context;
    private static XmsPlayer instance;
    private XmsPlayerUICallback xmsPlayerUICallback;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public static XmsPlayer getPlayerInstance() {
        if(instance !=null){
            return instance;
        }
        return null;
    }
    /**
     * @param context
     * @param simpleExoPlayerView
     * initialize both param to use in class
     */
    public XmsPlayer(Context context, SimpleExoPlayerView simpleExoPlayerView,
                     List<Channel> channelArrayList, int user_name, XmsPlayerUICallback xmsPlayerUICallback) {

        // set surface of the player
        this.context = context;
        this.simpleExoPlayerView = simpleExoPlayerView;
        this.channelArrayList = channelArrayList;
        this.channellistSize = channelArrayList.size();
        this.USER_NAME = user_name;
        this.xmsPlayerUICallback = xmsPlayerUICallback;
        instance = this;

    }



    public boolean hasPlayer() {
        return player == null;
    }
    /**
     * @param uris
     * @return ConcatenatingMediaSource / single MediaSource
     */
    private MediaSource buildUDPMediaSource(Uri[] uris) {
        /*
        * Function that handles creating a ConcatenatingMediaSource
        * Of UDP URIs
        */

        // Initialize UDP DataSource
        DataSource.Factory udsf = new UdpDataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return new UdpDataSource(null, UdpDataSource.DEFAULT_MAX_PACKET_SIZE, UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS);
            }
        };

        // Initialize ExtractorFactory
        ExtractorsFactory tsExtractorFactory = new DefaultExtractorsFactory().setTsExtractorFlags(FLAG_ALLOW_NON_IDR_KEYFRAMES);

        // Loop on URI list to create individual Media source
        MediaSource[] mediaSources = new MediaSource[uris.length];
        for (int i = 0; i < uris.length; i++) {
            mediaSources[i] = new ExtractorMediaSource(uris[i],
                    udsf,
                    tsExtractorFactory,
                    null,
                    null);
        }

        /*
        * if MediaSource only 1 source (URI) return a mediasource
        * else return the ConcatenationMediaSource
        * */
        return mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
    }

    /**
     * initializing player calling buildUDPMediaSource
     * and showChannelInfo
     */
    public void initializePlayer() {
        /*
        * Initialize ExoplayerFactory
        * */

        Uri[] uris = new Uri[channelArrayList.size()];
        for (int i = 0; i < channelArrayList.size(); i++) {
            uris[i] = Uri.parse(channelArrayList.get(i).getStream());
        }

        //default BandwidthMeter
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        //Track selector Factory that takes the adaptive track selection as constructor
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        // the track selector
        MappingTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //add trackselector to EventLogger constructor
        eventlogger = new EventLogger(trackSelector);

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this.context,
                null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);

        //set Player listeners
        player.addListener(eventlogger);
        player.setVideoDebugListener(eventlogger);
        player.setAudioDebugListener(eventlogger);

        // set the mediasource and play when ready
        player.prepare(buildUDPMediaSource(uris));
        simpleExoPlayerView.setPlayer(player);
//        showChannelInfo();
        xmsPlayerUICallback.showChannelInfo(player.getCurrentWindowIndex());

        player.setPlayWhenReady(true);

        monitor();

    }

    /**
     * release player on activity destroyed
     */
    public void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(eventlogger);
            player.setVideoDebugListener(null);
            player.setAudioDebugListener(null);
            player.release();
            player = null;
        }
    }

    /**
     * in ConcatenatingMediaSource -1 windows index in player
     * and loop
     */
    public void previouschannel() {
        Timeline currentTimeline = player.getCurrentTimeline();
        if (currentTimeline.isEmpty()) {
            return;
        }

        int currentWindowIndex = player.getCurrentWindowIndex();

        if (currentWindowIndex > 0 ) {
            player.seekTo(currentWindowIndex - 1, 0);
        } else {
            player.seekTo(currentTimeline.getWindowCount() - 1, 0);
        }
        xmsPlayerUICallback.showChannelInfo(player.getCurrentWindowIndex());
        monitor();
    }

    /**
     * in ConcatenatingMediaSource -1 windows index in player
     * and loop
     */
    public void nextchannel() {
        Timeline currentTimeline = player.getCurrentTimeline();
        if (currentTimeline.isEmpty()) {
            return;
        }
        int currentWindowIndex = player.getCurrentWindowIndex();
        if (currentWindowIndex < currentTimeline.getWindowCount() - 1) {
            player.seekTo(currentWindowIndex + 1, 0);
        } else {
            player.seekTo(0, 0);
        }
        xmsPlayerUICallback.showChannelInfo(player.getCurrentWindowIndex());
        monitor();
    }

    /**
     * @param channelid
     * changed current window index of exoplayer
     * thus changing channel
     * works with dispatchKeyEvent
     */
    public void changeChannel(int channelid) {
        if(channelid < channellistSize){
            if (player.getCurrentWindowIndex() != channelid) {
                player.seekTo(channelid, 0);
                monitor();
            }
            xmsPlayerUICallback.showChannelInfo(player.getCurrentWindowIndex());
        }
    }

    /**
     * Monitoring background worker to monitor user current channel
     * todo should change to service
     */
    public void monitor () {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Initialize Realm
                final Realm realm = Realm.getDefaultInstance();
                Channel channel = realm.where(Channel.class).equalTo("window_id", player.getCurrentWindowIndex()).findFirst();
                Log.d("TEST", USER_NAME + channel.getName());
                Log.d("TEST", String.valueOf(System.currentTimeMillis() / 1000));
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
}

