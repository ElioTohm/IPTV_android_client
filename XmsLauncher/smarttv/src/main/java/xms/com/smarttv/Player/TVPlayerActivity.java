package xms.com.smarttv.Player;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.XmsPro.xmsproplayer.XmsPlayer;
import com.eliotohme.data.Channel;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;


public class TVPlayerActivity extends Activity {
    String TAG  = "xms";
    private View channelInfo;
    private TextView currentChannel, channel_number_selector, channelName;
    private List<Channel> channelArrayList;
    private FrameLayout channelList_frameLayout;
    private SimpleExoPlayerView simpleExoPlayerView;
    private int USER_NAME;
    private XmsPlayer xmsPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.XmsPro.xmsproplayer.R.layout.activity_tv_player);

        channelInfo = findViewById(com.XmsPro.xmsproplayer.R.id.channelInfo);
        currentChannel = findViewById(com.XmsPro.xmsproplayer.R.id.current_channel);
        channelName = findViewById(com.XmsPro.xmsproplayer.R.id.channel_name);
        channelArrayList = new ArrayList<>();
        channelList_frameLayout = findViewById(com.XmsPro.xmsproplayer.R.id.main_channellist_fragment);
        channel_number_selector = findViewById(com.XmsPro.xmsproplayer.R.id.channel_number_selector);

        Realm.init(this);

        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<Channel> channels = realm.where(Channel.class);

        channelArrayList.addAll(channels.findAllSorted("id"));
        simpleExoPlayerView = findViewById(com.XmsPro.xmsproplayer.R.id.simpleexoplayerview);

        USER_NAME = 1;

        xmsPlayer = new XmsPlayer(this, simpleExoPlayerView, channelArrayList, USER_NAME,
                 new XmsPlayerUICallback() {
            @Override
            public void showChannelInfo(int channelindex) {
                Channel channel = channelArrayList.get(channelindex);
                currentChannel.setText(String.valueOf(channel.getId()));
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
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            xmsPlayer.initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || !xmsPlayer.hasPlayer())) {
            xmsPlayer.initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            xmsPlayer.releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            xmsPlayer.releasePlayer();
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
                    xmsPlayer.nextchannel();
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    xmsPlayer.previouschannel();
                    return true;
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
                channel_number_selector.setText(channel_number_selector.getText() + String.valueOf(finalChannel_numberpressed));
                Runnable setchannelnumberRunnable = new Runnable() {
                    public void run() {
                        if (!channel_number_selector.getText().equals("")) {
                            xmsPlayer.changeChannel(Integer.parseInt((String) channel_number_selector.getText()) - 1);
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
}
