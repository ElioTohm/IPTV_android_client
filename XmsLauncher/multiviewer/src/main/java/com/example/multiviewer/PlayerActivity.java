package com.example.multiviewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.XmsPro.xmsproplayer.XmsPlayer;
import com.eliotohme.data.Stream;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.videolan.libvlc.Media.Type.Stream;

public class PlayerActivity extends Activity implements XmsPlayerUICallback{
    XmsPlayer xmsPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        PlayerView playerView = findViewById(R.id.playerView1);
        // Set up the user interaction to manually show or hide the system UI.
        List<Stream> streams = new ArrayList<>();
        Stream stream = new Stream();
        stream.setVid_stream("http://173.249.35.34:8080/icam130o/mpegts");
        stream.setType(5);
        streams.add(stream);
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        xmsPlayer = new XmsPlayer(this,
                playerView,
                null,
                streams,
                null, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        xmsPlayer.initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        xmsPlayer.initializePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        xmsPlayer.releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        xmsPlayer.releasePlayer();
    }

    @Override
    public void showChannelInfo(int channelindexm, int duration, boolean updatestreams) {

    }
}
