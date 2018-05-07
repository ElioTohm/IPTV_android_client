package com.XmsPro.xmsproplayer;


import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;
import android.widget.Toast;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.eliotohme.data.Channel;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.Realm;

public class RTPPlayer implements IVLCVout.Callback {
    private static RTPPlayer instance;
    public Context context;
    private Realm realm;
    private int CurrentChannelNumber;
    private int width;
    private int height;
    // display surface
    private SurfaceView mSurface;
    private XmsPlayerUICallback xmsPlayerUICallback;
    // media player
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    /*************
     * Events
     *************/

    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    public RTPPlayer(Context context, SurfaceView mSurface, XmsPlayerUICallback xmsPlayerUICallback, int width, int height) {
        this.context = context;
        this.mSurface = mSurface;
        this.xmsPlayerUICallback = xmsPlayerUICallback;
        realm = Realm.getDefaultInstance();
        this.height = height;
        this.width = width;
        instance = this;
    }

    public static RTPPlayer getPlayerInstance() {
        if (instance != null) {
            return instance;
        }
        return null;
    }

    /*************
     * Player
     *************/

    public void createPlayer() {
        releasePlayer();
        try {
            ArrayList<String> options = new ArrayList<String>();
            options.add("--http-reconnect");
            libvlc = new LibVLC(context);

            // Create media player
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);
            mMediaPlayer.setAspectRatio("16:9");
            mMediaPlayer.setScale(0);

            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);
            vout.setWindowSize(width, height);
            vout.addCallback(this);
            vout.attachViews();

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void SetChannel(int media) {
        Channel channel = realm.where(Channel.class).equalTo("number", media).findFirst();
        if (channel != null) {
            Media m = new Media(libvlc, Uri.parse(channel.getStream().getVid_stream()));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
            CurrentChannelNumber = channel.getNumber();
            xmsPlayerUICallback.showChannelInfo(media - 1, 2000, false);
        }
    }

    public void SetSource(String media) {
        Media m = new Media(libvlc, Uri.parse(media));
        mMediaPlayer.setMedia(m);
        mMediaPlayer.play();
    }

    public void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        libvlc.release();
        libvlc = null;
    }

    @Override
    public void onSurfacesCreated(IVLCVout vout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vout) {

    }

    public int nextchannel() {
        SetChannel(CurrentChannelNumber + 1);
        return CurrentChannelNumber + 1;
    }

    public int previouschannel() {
        SetChannel(CurrentChannelNumber - 1);
        return CurrentChannelNumber - 1;
    }

    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<RTPPlayer> mOwner;

        public MyPlayerListener(RTPPlayer owner) {
            mOwner = new WeakReference<RTPPlayer>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            RTPPlayer player = mOwner.get();
            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    player.releasePlayer();
                    break;
                case MediaPlayer.Event.EncounteredError:
                    //player.releasePlayer();
                    break;
                case MediaPlayer.Event.Playing:
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:
                default:
                    break;
            }
        }
    }
}

