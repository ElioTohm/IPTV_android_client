package com.XmsPro.xmsproplayer;

import android.content.Context;
import android.net.Uri;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.eliotohme.data.Channel;
import com.eliotohme.data.network.AuthenticationInterceptor;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.UdpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import okhttp3.OkHttpClient;

import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES;
import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS;
import static com.google.android.exoplayer2.extractor.ts.TsExtractor.MODE_SINGLE_PMT;

public class XmsPlayer  {
    protected String userAgent;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer player;
    boolean playWhenReady;
    int currentWindow;
    long playbackPosition;
    private EventLogger eventlogger;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private List<Channel> channelArrayList;
    private int channellistSize;
    private SimpleExoPlayerView simpleExoPlayerView;
    private int USER_NAME;
    private Context context;
    private static XmsPlayer instance;
    private XmsPlayerUICallback xmsPlayerUICallback;
    private String TOKENTYPE, TOKEN;
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
                     List<Channel> channelArrayList, int user_name, XmsPlayerUICallback xmsPlayerUICallback, String TOKENTYPE, String TOKEN) {

        // set surface of the player
        this.context = context;
        this.simpleExoPlayerView = simpleExoPlayerView;
        this.channelArrayList = channelArrayList;
        this.channellistSize = channelArrayList.size();
        this.USER_NAME = user_name;
        this.xmsPlayerUICallback = xmsPlayerUICallback;
        this.TOKEN = TOKEN;
        this.TOKENTYPE = TOKENTYPE;
        instance = this;
    }



    public boolean hasPlayer() {
        return player == null;
    }

    private MediaSource buildMediaSource(List<Channel> channels) {
        /*
        * Function that handles creating a ConcatenatingMediaSource
        * Of UDP URIs
        */
        DataSource.Factory mediaDataSourceFactory = buildDataSourceFactory(true);

        // Loop on URI list to create individual Media source
        MediaSource[] mediaSources = new MediaSource[channels.size()];

        for (int i = 0; i < channels.size(); i++) {
            Uri channel_stream_uri = Uri.parse(channels.get(i).getStream().getVid_stream());
            int channel_type = channels.get(i).getStream().getType();
            switch (channel_type) {
                case  1:
                    DataSource.Factory udsf = new UdpDataSource.Factory() {
                        @Override
                        public DataSource createDataSource() {
                            return new UdpDataSource(null, 1316, UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS);
                        }
                    };
                    ExtractorsFactory tsExtractorFactory = new DefaultExtractorsFactory()
                            .setTsExtractorFlags(FLAG_ALLOW_NON_IDR_KEYFRAMES)
                            .setTsExtractorMode(MODE_SINGLE_PMT);
                    mediaSources[i] = new ExtractorMediaSource(channel_stream_uri,
                                            udsf,
                                            tsExtractorFactory,
                                            null,
                                            null);
                    break;
                case 2:
                    mediaSources[i]  = new HlsMediaSource(channel_stream_uri,
                                            mediaDataSourceFactory,
                                            null,
                                            null);
                    break;
                case 3:
                    DashChunkSource.Factory dashChunkSourceFactory =
                            new DefaultDashChunkSource.Factory(mediaDataSourceFactory);
                    mediaSources[i]  = new DashMediaSource(channel_stream_uri,
                                            mediaDataSourceFactory,
                                            dashChunkSourceFactory,
                                            null,
                                            null);
                    break;
                case 4:
                    mediaSources[i]  = new SsMediaSource(channel_stream_uri,
                                            buildDataSourceFactory(false),
                                            new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                                            null,
                                            null);
                    break;
                case 5:
                    mediaSources[i]  = new ExtractorMediaSource(channel_stream_uri,
                                            mediaDataSourceFactory,
                                            new DefaultExtractorsFactory(),
                                            null,
                                            null);
            }

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
        userAgent = Util.getUserAgent(context, "ExoPlayerDemo");

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
        player.prepare(buildMediaSource(channelArrayList));
        simpleExoPlayerView.setPlayer(player);
        xmsPlayerUICallback.showChannelInfo(player.getCurrentWindowIndex());

        player.setPlayWhenReady(true);

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
            }
            xmsPlayerUICallback.showChannelInfo(player.getCurrentWindowIndex());
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(this.TOKENTYPE, this.TOKEN);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        return new OkHttpDataSourceFactory(okHttpClient, userAgent, bandwidthMeter);
    }

    public int getCurrentChannelIndex () {
        return player.getCurrentWindowIndex();
    }

}

