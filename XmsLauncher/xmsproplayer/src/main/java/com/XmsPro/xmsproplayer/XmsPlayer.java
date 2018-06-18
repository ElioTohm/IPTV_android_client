package com.XmsPro.xmsproplayer;

import android.content.Context;
import android.net.Uri;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.eliotohme.data.Stream;
import com.eliotohme.data.network.AuthenticationInterceptor;
import com.google.android.exoplayer2.C;
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
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.FilteringDashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.RepresentationKey;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.UdpDataSource;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import okhttp3.OkHttpClient;

import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES;
import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_IGNORE_SPLICE_INFO_STREAM;
import static com.google.android.exoplayer2.extractor.ts.TsExtractor.MODE_SINGLE_PMT;

public class XmsPlayer  {
    private String userAgent, TOKENTYPE, TOKEN;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer player;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private PlayerView playerview;
    private Context context;
    private DefaultTrackSelector trackSelector;
    private static XmsPlayer instance;
    private XmsPlayerUICallback xmsPlayerUICallback;
    private TrackSelectionHelper trackSelectionHelper;
    private PlayerControlView playerControlView;
    private DefaultRenderersFactory renderersFactory;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public SimpleExoPlayer getplayer(){
        return player;
    }

    public TrackSelectionHelper gettrackSelectionHelper() {
        return trackSelectionHelper;
    }

    public MappingTrackSelector.MappedTrackInfo getmappedTrackInfo() {
        return trackSelector.getCurrentMappedTrackInfo();
    }

    public static XmsPlayer getPlayerInstance() {
        if(instance !=null){
            return instance;
        }
        return null;
    }

    public long getDuration () {
        if (player.getDuration() / 1000 > 600) {
            return player.getDuration();
        }
        return 0;
    }

    /**
     * @param context
     * @param playerview
     * initialize both param to use in class
     */
    public XmsPlayer(Context context, PlayerView playerview, PlayerControlView playerControlView, String TOKENTYPE, String TOKEN) {

        // set surface of the player
        this.context = context;
        this.xmsPlayerUICallback = (XmsPlayerUICallback) context;
        this.playerview = playerview;
        this.playerControlView = playerControlView;
        this.TOKEN = TOKEN;
        this.TOKENTYPE = TOKENTYPE;
        instance = this;
    }

    private MediaSource buildMediaSource(List<Stream> channels) {
        /*
        * Function that handles creating a ConcatenatingMediaSource
        * Of UDP URIs
        */
        DataSource.Factory mediaDataSourceFactory = buildDataSourceFactory(true);

        // Loop on URI list to create individual Media source
        MediaSource[] mediaSources = new MediaSource[channels.size()];

        // init chunk and media source factories
        DashChunkSource.Factory dashChunkSourceFactory = null;
        DefaultExtractorsFactory defaultExtractorsFactory = null;
        SsChunkSource.Factory ssChunkSourceFactory = null;

        for (int i = 0; i < channels.size(); i++) {
            Uri channel_stream_uri = Uri.parse(channels.get(i).getVid_stream());
            int channel_type = channels.get(i).getType();
            switch (channel_type) {
                case  Stream.TYPE_UDP:
                    DataSource.Factory udsf = new UdpDataSource.Factory() {
                        @Override
                        public DataSource createDataSource() {
                            return new UdpDataSource(null, 65507);
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
                case Stream.TYPE_HLS:
                    mediaSources[i]  = new HlsMediaSource.Factory(mediaDataSourceFactory)
                                            .setExtractorFactory(new XmsHlsExtractorFactory())
                                            .createMediaSource(channel_stream_uri);
                    break;
                case Stream.DASH:
                    if (dashChunkSourceFactory == null) {
                        dashChunkSourceFactory = new DefaultDashChunkSource.Factory(mediaDataSourceFactory);
                    }
                    mediaSources[i]  = new DashMediaSource.Factory(dashChunkSourceFactory, buildDataSourceFactory(false))
                                            .createMediaSource(channel_stream_uri);
                    break;
                case Stream.SS:
                    if( ssChunkSourceFactory == null) {
                        ssChunkSourceFactory = new DefaultSsChunkSource.Factory(mediaDataSourceFactory);
                    }
                    mediaSources[i]  = new SsMediaSource.Factory(ssChunkSourceFactory, mediaDataSourceFactory)
                                            .createMediaSource(channel_stream_uri);
                    break;
                case Stream.MISC:
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
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            //Track selector Factory that takes the adaptive track selection as constructor
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

            // the track selector
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            trackSelectionHelper = new TrackSelectionHelper(trackSelector, adaptiveTrackSelectionFactory);

            renderersFactory = new DefaultRenderersFactory(this.context, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);

            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
        }

        // set the mediasource and play when ready
        playerview.setPlayer(player);
        player.setPlayWhenReady(true);

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
     * @param streams
     * @param showinfo
     * change source of stream and set if information about stream should show with respect to showinfo flag
     */
    public void changeSource(List<Stream> streams, boolean showinfo) {
        player.prepare(buildMediaSource(streams));
        playerControlView.setPlayer(player);
        if (showinfo) {
            xmsPlayerUICallback.showChannelInfo(streams.get(0).getId(), 1000, false
            );
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

