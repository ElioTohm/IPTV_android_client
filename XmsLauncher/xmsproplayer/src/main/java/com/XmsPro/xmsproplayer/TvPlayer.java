package com.XmsPro.xmsproplayer;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliotohme.data.Channel;
import com.eliotohme.data.User;
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
import com.google.android.exoplayer2.util.Util;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class TvPlayer extends AppCompatActivity {
    String TAG  = "xms";
    private SimpleExoPlayer player;
    boolean playWhenReady;
    int currentWindow;
    long playbackPosition;
    private EventLogger eventlogger;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private View channelInfo;
    private TextView currentChannel, channel_number_idicator, channelName;
    private ImageView channelIco;
    private List<Channel> channelArrayList;
    private FrameLayout channelList_frameLayout;
    private int channellistSize;
    private SimpleExoPlayerView simpleExoPlayerView;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private String USER_NAME;

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
                return new UdpDataSource(null, UdpDataSource.DEFAULT_MAX_PACKET_SIZE, 0);
            }
        };

        // Initialize ExtractorFactory
        ExtractorsFactory tsExtractorFactory = new DefaultExtractorsFactory();

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

    private void initializePlayer () {
        /*
        * Initialize ExoplayerFactory
        * */

        Uri[] uris = new Uri[channelArrayList.size()];
        for (int i = 0; i < channelArrayList.size(); i++) {
            uris[i] = Uri.parse(channelArrayList.get(i).getUri());
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

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.simpleexoplayerview);

        // set surface of the player the mediasource and play when ready
        player.prepare(buildUDPMediaSource(uris));
        simpleExoPlayerView.setPlayer(player);
        showChannelInfo(channelArrayList.get(player.getCurrentWindowIndex()));
        player.setPlayWhenReady(true);

        monitor();

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

    private void previouschannel() {
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
        monitor();
    }

    private void nextchannel() {
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
        monitor();
    }

    private void showChannelInfo(Channel channel) {
        currentChannel.setText(String.valueOf(channel.getWindowid() + 1));
        channelName.setText(channel.getName());
        channelInfo.setVisibility(View.VISIBLE);
        Handler mChannelInfoHandler=new Handler();
        Runnable mChannelInfoRunnable=new Runnable() {
            public void run() {
                channelInfo.setVisibility(View.INVISIBLE);
            }
        };
        mChannelInfoHandler.removeCallbacks(mChannelInfoRunnable);
        mChannelInfoHandler.postDelayed(mChannelInfoRunnable, 5000);
    }

    public void changeChannel(int channelid) {
        if(channelid < channellistSize){
            if (player.getCurrentWindowIndex() != channelid) {
                player.seekTo(channelid, 0);
                monitor();
            }
            showChannelInfo(channelArrayList.get(channelid));
        }
    }

    public void monitor () {
        if (scheduledExecutorService != null ) {
            scheduledExecutorService.shutdown();
        }
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Initialize Realm
                final Realm realm = Realm.getDefaultInstance();
                RealmResults<Channel> realmResults  = realm.where(Channel.class).equalTo("window_id", player.getCurrentWindowIndex()).findAll();
                Log.d("TEST", USER_NAME + realmResults.get(0).getName());
                Log.d("TEST", String.valueOf(System.currentTimeMillis() / 1000));
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_player);

        channelInfo = (View) findViewById(R.id.channelInfo);
        currentChannel = (TextView) findViewById(R.id.current_channel);
        channelName = (TextView) findViewById(R.id.channel_name);
        channelIco= (ImageView) findViewById(R.id.channel_ico);
        channelArrayList = new ArrayList<>();
        channelList_frameLayout = (FrameLayout) findViewById(R.id.main_channellist_fragment);
        channel_number_idicator = (TextView) findViewById(R.id.channel_number_idicator);

        Realm.init(this);

        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<Channel> channels = realm.where(Channel.class);

        channelArrayList.addAll(channels.findAll());

        channellistSize = channelArrayList.size();

        RealmQuery<User> users = realm.where(User.class);
        RealmResults<User> users1 = users.findAll();
        USER_NAME = users1.get(0).getName();

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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_UP) {
            int channel_numberpressed = 10;
            switch (keyCode) {
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    nextchannel();
                    showChannelInfo(channelArrayList.get(player.getCurrentWindowIndex()));
                    Log.d(TAG, String.valueOf(player.getCurrentWindowIndex()));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    previouschannel();
                    showChannelInfo(channelArrayList.get(player.getCurrentWindowIndex()));
                    Log.d(TAG, String.valueOf(player.getCurrentWindowIndex()));
                    return true;
                case KeyEvent.KEYCODE_0:
                    channel_numberpressed = 0;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_1:
                    channel_numberpressed = 1;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_2:
                    channel_numberpressed = 2;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_3:
                    channel_numberpressed = 3;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_4:
                    channel_numberpressed = 4;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_5:
                    channel_numberpressed = 5;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_6:
                    channel_numberpressed = 6;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_7:
                    channel_numberpressed = 7;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_8:
                    channel_numberpressed = 8;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_9:
                    channel_numberpressed = 9;
                    channel_number_idicator.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_MENU:

                    if (FrameLayout.VISIBLE == channelList_frameLayout.getVisibility()) {
                        channelList_frameLayout.setVisibility(FrameLayout.GONE);
                    } else {
                        channelList_frameLayout.setVisibility(FrameLayout.VISIBLE);
                    }
                    return true;
            }

            if (channel_numberpressed < 10){
                Handler setchannelnumberHandler = new Handler();
                final int finalChannel_numberpressed = channel_numberpressed;
                channel_number_idicator.setText(channel_number_idicator.getText() + String.valueOf(finalChannel_numberpressed));
                Runnable setchannelnumberRunnable = new Runnable() {
                    public void run() {
                        if (!channel_number_idicator.getText().equals("")) {
                            changeChannel(Integer.parseInt((String) channel_number_idicator.getText()) - 1);
                            channel_number_idicator.setText("");
                            channel_number_idicator.setVisibility(View.INVISIBLE);
                        }
                    }
                };
                setchannelnumberHandler.postDelayed(setchannelnumberRunnable, 2000);

            }
        }

        return super.dispatchKeyEvent(event);
    }


}
