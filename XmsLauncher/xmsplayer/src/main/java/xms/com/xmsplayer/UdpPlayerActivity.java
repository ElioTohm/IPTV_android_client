package xms.com.xmsplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.UdpDataSource;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import static com.google.android.exoplayer2.extractor.ts.TsExtractor.MODE_SINGLE_PMT;

public class UdpPlayerActivity extends AppCompatActivity {
    private SimpleExoPlayer player;
    boolean playWhenReady;
    int currentWindow;
    long playbackPosition;
    private EventLogger eventlogger;
    private SurfaceView playerView;
    public static final String URI_LIST_EXTRA = "uri_list";
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private MediaSource buildUDPMediaSource(Uri[] uris) {
        /*
        * Function that handles creating a ConcatenatingMediaSource
        * Of UDP URIs
        */

        // Initialize UDP DataSource
        DataSource.Factory udsf = new UdpDataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return new UdpDataSource(null, 3000, 100000);
            }
        };

        // Initialize ExtractorFactory for UDP datasource
        ExtractorsFactory tsExtractorFactory = new ExtractorsFactory() {
            @Override
            public Extractor[] createExtractors() {
                return new TsExtractor[]{new TsExtractor(MODE_SINGLE_PMT,
                        new TimestampAdjuster(0), new DefaultTsPayloadReaderFactory())};
            }
        };

        // Loop on URI list to create individual Media source
        MediaSource[] mediaSources = new MediaSource[uris.length];
        for (int i = 0; i < uris.length; i++) {
            mediaSources[i] = new ExtractorMediaSource(uris[i], udsf, tsExtractorFactory, null, null);
        }

        /*
        * if MediaSource only 1 source (URI) return a mediasource
        * else return the ConcatenationMediaSource
        * */
        return mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
    }

    private void initializePlayer () {
        /*
        * Initialize ExoplayerFactory
        * */
        Intent intent = getIntent();

        // Create List of URI fetched from intent
        String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);
        Uri[] uris = new Uri[uriStrings.length];
        for (int i = 0; i < uriStrings.length; i++) {
            uris[i] = Uri.parse(uriStrings[i]);
        }

        //default BandwidthMeter
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        //Track selector Factory that takes the adaptive track selection as constructor
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        // the track selector
        MappingTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //add trackselector to EventLogger constructor
        eventlogger = new EventLogger(trackSelector);

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,
            null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);

        //set Player listeners
        player.addListener(eventlogger);
        player.setVideoDebugListener(eventlogger);
        player.setAudioDebugListener(eventlogger);

        // set surface of the player the mediasource and play when ready
        player.setVideoSurfaceView(playerView);
        player.prepare(buildUDPMediaSource(uris));
        player.setPlayWhenReady(true);
    }

    private void releasePlayer() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        playerView = (SurfaceView) findViewById(R.id.surfaceView2);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

}
