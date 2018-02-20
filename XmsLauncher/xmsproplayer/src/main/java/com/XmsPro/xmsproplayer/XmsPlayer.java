package com.XmsPro.xmsproplayer;

import android.content.Context;
import android.net.Uri;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.eliotohme.data.Channel;
import com.eliotohme.data.network.AuthenticationInterceptor;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
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
import com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
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
import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_IGNORE_SPLICE_INFO_STREAM;
import static com.google.android.exoplayer2.extractor.ts.TsExtractor.MODE_SINGLE_PMT;

public class XmsPlayer  {
    private String userAgent;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer player;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private List<Channel> channelArrayList;
    private SimpleExoPlayerView simpleExoPlayerView;
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
                     List<Channel> channelArrayList, String TOKENTYPE, String TOKEN) {

        // set surface of the player
        this.context = context;
        this.xmsPlayerUICallback = (XmsPlayerUICallback) context;
        this.simpleExoPlayerView = simpleExoPlayerView;
        this.channelArrayList = channelArrayList;
        this.TOKEN = TOKEN;
        this.TOKENTYPE = TOKENTYPE;
        instance = this;
    }

    private MediaSource buildMediaSource(List<Channel> channels) {
        /*
        * Function that handles creating a ConcatenatingMediaSource
        * Of UDP URIs
        */
        DataSource.Factory mediaDataSourceFactory = buildDataSourceFactory(false);

        // Loop on URI list to create individual Media source
        MediaSource[] mediaSources = new MediaSource[channels.size()];

        // init chunk and media source factories
        DashChunkSource.Factory dashChunkSourceFactory = null;
        DefaultExtractorsFactory defaultExtractorsFactory = null;
        SsChunkSource.Factory ssChunkSourceFactory = null;

        for (int i = 0; i < channels.size(); i++) {
            Uri channel_stream_uri = Uri.parse(channels.get(i).getStream().getVid_stream());
            int channel_type = channels.get(i).getStream().getType();
            switch (channel_type) {
                case  1:
                    DataSource.Factory udsf = new UdpDataSource.Factory() {
                        @Override
                        public DataSource createDataSource() {
                            return new UdpDataSource(null, 65507, 500);
                        }
                    };
                    ExtractorsFactory tsExtractorFactory = new DefaultExtractorsFactory()
                            .setTsExtractorFlags(FLAG_ALLOW_NON_IDR_KEYFRAMES)
                            .setTsExtractorFlags(FLAG_IGNORE_SPLICE_INFO_STREAM)
                            .setTsExtractorMode(MODE_SINGLE_PMT);
                    mediaSources[i] = new ExtractorMediaSource.Factory(udsf)
                                            .setExtractorsFactory(tsExtractorFactory)
                                            .createMediaSource(channel_stream_uri);
                    break;
                case 2:
                    mediaSources[i]  = new HlsMediaSource.Factory(mediaDataSourceFactory)
                                            .createMediaSource(channel_stream_uri);
                    break;
                case 3:
                    if (dashChunkSourceFactory == null) {
                        dashChunkSourceFactory = new DefaultDashChunkSource.Factory(mediaDataSourceFactory);
                    }
                    mediaSources[i]  = new DashMediaSource.Factory(dashChunkSourceFactory, mediaDataSourceFactory)
                                            .createMediaSource(channel_stream_uri);
                    break;
                case 4:
                    if( ssChunkSourceFactory == null) {
                        ssChunkSourceFactory = new DefaultSsChunkSource.Factory(mediaDataSourceFactory);
                    }
                    mediaSources[i]  = new SsMediaSource.Factory(ssChunkSourceFactory, mediaDataSourceFactory)
                                            .createMediaSource(channel_stream_uri);
                    break;
                case 5:
                    if (defaultExtractorsFactory == null) {
                        defaultExtractorsFactory = new DefaultExtractorsFactory();
                    }
                    mediaSources[i]  = new ExtractorMediaSource.Factory(mediaDataSourceFactory)
                                            .setExtractorsFactory(defaultExtractorsFactory)
                                            .createMediaSource(channel_stream_uri);
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
        if (player == null) {
            userAgent = Util.getUserAgent(context, "ExoPlayerDemo");

            //default BandwidthMeter
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            //Track selector Factory that takes the adaptive track selection as constructor
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

            // the track selector
            MappingTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this.context,
                    null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);


            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector,
                    new DefaultLoadControl(new DefaultAllocator(true, 512, 64),
                            1000,
                            5000,
                            100,
                            100,
                            10,
                            true));
        }
        player.setPlayWhenReady(true);

        // set the mediasource and play when ready
        player.prepare(buildMediaSource(channelArrayList));
        simpleExoPlayerView.setPlayer(player);
        xmsPlayerUICallback.showChannelInfo(1);



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
        player.prepare(buildMediaSource(channelList));
        simpleExoPlayerView.setPlayer(player);
        if (showinfo) {
            xmsPlayerUICallback.showChannelInfo(channelList.get(0).getNumber());
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(this.TOKENTYPE, this.TOKEN);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        return new OkHttpDataSourceFactory(okHttpClient, userAgent, bandwidthMeter);
    }

}

