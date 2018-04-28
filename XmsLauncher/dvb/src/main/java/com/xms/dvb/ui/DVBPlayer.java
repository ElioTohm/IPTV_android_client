package com.xms.dvb.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.XmsPro.xmsproplayer.RTPPlayer;
import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.eliotohme.data.Channel;
import com.xms.dvb.R;
import com.xms.dvb.XmlParser;
import com.xms.dvb.app.Preferences;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class DVBPlayer extends Activity {
    private RTPPlayer ftPplayer;
    private View channelInfo;
    private TextView currentChannel, channel_number_selector, channelName;
    private List<Channel> channelArrayList;
    private RelativeLayout channelList_frameLayout;
    private int USER_NAME;
    private ChannelGridFragment channelGridFragment;
    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dvbplayer);

        localeinit ();
        new checkChannelsLoaded().execute();
    }

    private class checkChannelsLoaded extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Realm realm = Realm.getDefaultInstance();
            List<Channel> Unloaded_channels = realm.where(Channel.class).contains("name", "Unkown").findAll();
            if (Unloaded_channels.size() > 0 ) {
                Handler handler =  new Handler(DVBPlayer.this.getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(DVBPlayer.this , "loading channel info...", Toast.LENGTH_LONG).show();
                    }
                });
                XmlParser channelXmlParser_resume = new XmlParser(DVBPlayer.this);
                long allChannelSize = realm.where(Channel.class).count();
                int progress = 0;
                for (int i=0; i<Unloaded_channels.size(); i++) {
                    int total = (int) (100.0 * (i + allChannelSize - Unloaded_channels.size())  / allChannelSize);
                    if (progress < total) {
                        progress = total;
                        channelXmlParser_resume.getServiceName(Unloaded_channels.get(i).getStream().getVid_stream(), String.valueOf(progress));
                    } else {
                        channelXmlParser_resume.getServiceName(Unloaded_channels.get(i).getStream().getVid_stream(), "");
                    }
                }
            }
            realm.close();
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ftPplayer.createPlayer();
        ftPplayer.SetChannel(Preferences.getLastChannel());
        channelGridFragment.setSelectedPosition(Preferences.getLastChannel());
    }

    @Override
    public void onResume() {
        super.onResume();
        ftPplayer.createPlayer();
        ftPplayer.SetChannel(Preferences.getLastChannel());
        channelGridFragment.setSelectedPosition(Preferences.getLastChannel());
    }

    @Override
    public void onPause() {
        super.onPause();
        ftPplayer.releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        ftPplayer.releasePlayer();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_UP) {
            int channel_numberpressed = 10;
            switch (keyCode) {
                case KeyEvent.KEYCODE_PERIOD:
                    if (isPackageInstalled("xmspro.com.vod")) {
                        Intent i = this.getPackageManager().getLaunchIntentForPackage("xmspro.com.vod");
                        startActivity(i);
                    }
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    if (FrameLayout.VISIBLE == channelList_frameLayout.getVisibility()) {
                        channelList_frameLayout.setVisibility(FrameLayout.GONE);
                        return false;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_MENU:
                    startActivity(new Intent(this, HomeActivity.class));
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (FrameLayout.VISIBLE != channelList_frameLayout.getVisibility()) {
                        channelList_frameLayout.setVisibility(FrameLayout.VISIBLE);
                        return false;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (FrameLayout.VISIBLE != channelList_frameLayout.getVisibility()) {
                        int channelnumber = ftPplayer.nextchannel();
                        Preferences.setLastChannel(channelnumber);
                        channelGridFragment.setSelectedPosition(channelnumber);
                        return true;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (FrameLayout.VISIBLE != channelList_frameLayout.getVisibility()) {
                        int channelnumber = ftPplayer.previouschannel();
                        Preferences.setLastChannel(channelnumber);
                        channelGridFragment.setSelectedPosition(channelnumber);
                        return true;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_0:
                    channel_numberpressed = 0;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_1:
                    channel_numberpressed = 1;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_2:
                    channel_numberpressed = 2;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_3:
                    channel_numberpressed = 3;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_4:
                    channel_numberpressed = 4;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_5:
                    channel_numberpressed = 5;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_6:
                    channel_numberpressed = 6;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_7:
                    channel_numberpressed = 7;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_8:
                    channel_numberpressed = 8;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_9:
                    channel_numberpressed = 9;
                    channel_number_selector.setVisibility(View.VISIBLE);
                    break;
            }

            if (channel_numberpressed < 10){
                Handler setchannelnumberHandler = new Handler();
                final int finalChannel_numberpressed = channel_numberpressed;
                channel_number_selector.setText(channel_number_selector.getText() + String.valueOf(finalChannel_numberpressed));
                Runnable setchannelnumberRunnable = new Runnable() {
                    public void run() {
                    if (!channel_number_selector.getText().equals("")) {
                        int channel_number = Integer.parseInt((String) channel_number_selector.getText());
                        ftPplayer.SetChannel(channel_number);
                        Preferences.setLastChannel(channel_number);
                        channelGridFragment.setSelectedPosition(channel_number - 1);
                        channel_number_selector.setText("");
                        channel_number_selector.setVisibility(View.INVISIBLE);
                    }
                    }
                };
                setchannelnumberHandler.postDelayed(setchannelnumberRunnable, 2000);
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public boolean isPackageInstalled(String targetPackage){
        PackageManager pm=getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void localeinit () {
        // Starts the IntentService
        channelInfo = findViewById(R.id.channelInfo);
        currentChannel = findViewById(R.id.current_channel);
        channelName = findViewById(R.id.channel_name);
        channelArrayList = new ArrayList<>();
        channelList_frameLayout = findViewById(R.id.main_channellist_fragment);
        channel_number_selector = findViewById(R.id.channel_number_selector);
        channelGridFragment= (ChannelGridFragment) getFragmentManager().findFragmentById(R.id.main_channellist_fragment);
        SurfaceView surfaceView = findViewById(R.id.surface);
        USER_NAME = 1;
        realm = Realm.getDefaultInstance();
        channelArrayList.addAll(realm.where(Channel.class).findAll().sort("number"));

        ftPplayer = new RTPPlayer(DVBPlayer.this, surfaceView,
                new XmsPlayerUICallback() {
                    @Override
                    public void showChannelInfo(int channelindex, int duration, boolean update) {
                        Channel channel = channelArrayList.get(channelindex);
                        currentChannel.setText(String.valueOf(channel.getNumber()));
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
                }, 1280, 720); }
}
