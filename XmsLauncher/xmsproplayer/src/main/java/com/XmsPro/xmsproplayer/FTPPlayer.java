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

public class FTPPlayer implements IVLCVout.Callback{
    private Realm realm;
    public Context context;
    private int CurrentChannelNumber;
    private static FTPPlayer instance;
    // display surface
    private SurfaceView mSurface;
    private XmsPlayerUICallback xmsPlayerUICallback;
    // media player
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;

    public static FTPPlayer getPlayerInstance() {
        if(instance !=null){
            return instance;
        }
        return null;
    }

    public FTPPlayer(Context context, SurfaceView mSurface, XmsPlayerUICallback xmsPlayerUICallback) {
        this.context = context;
        this.mSurface = mSurface;
        this.xmsPlayerUICallback = xmsPlayerUICallback;
        realm = Realm.getDefaultInstance();
        instance = this;
    }

    /*************
     * Player
     *************/

    public void createPlayer() {
        releasePlayer();
        try {
            ArrayList<String> options = new ArrayList<String>();
            options.add("--http-reconnect");
            options.add("--network-caching="+1000);
            libvlc = new LibVLC(context);

            // Create media player
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);

            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);
            vout.setWindowSize(1280,720);
            vout.addCallback(this);
            vout.attachViews();

        } catch (Exception e) {
            Toast.makeText(context, "Error creating player!", Toast.LENGTH_LONG).show();
        }
    }

    public void SetChannel(int media) {
        Channel channel = realm.where(Channel.class).equalTo("number", media).findFirst();
        if(channel != null) {
            Media m = new Media(libvlc, Uri.parse(channel.getStream().getVid_stream()));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
            CurrentChannelNumber = channel.getNumber();
            xmsPlayerUICallback.showChannelInfo(media - 1);
        }
    }

    public void releasePlayer(){
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        libvlc.release();
        libvlc = null;
    }

    /*************
     * Events
     *************/

    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    @Override
    public void onSurfacesCreated(IVLCVout vout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vout) {

    }

    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<FTPPlayer> mOwner;

        public MyPlayerListener(FTPPlayer owner) {
            mOwner = new WeakReference<FTPPlayer>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            FTPPlayer player = mOwner.get();
            switch(event.type) {
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

    public int nextchannel () {
        SetChannel(CurrentChannelNumber + 1);
        return CurrentChannelNumber + 1;
    }

    public int previouschannel () {
        SetChannel(CurrentChannelNumber - 1);
        return CurrentChannelNumber - 1;
    }
}

