package xms.com.smarttv.Player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.XmsPro.xmsproplayer.XmsPlayer;
import com.bumptech.glide.Glide;
import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.R;
import xms.com.smarttv.UI.ApplicationsMenu;
import xms.com.smarttv.UI.CustomHeaderItem;
import xms.com.smarttv.UI.MainMenu;
import xms.com.smarttv.app.Preferences;
import xms.com.smarttv.fragments.ChannelsListFragment;
import xms.com.smarttv.fragments.SectionMenuFragment;
import xms.com.smarttv.services.GetInstalledAppService;

public class TVPlayerActivity extends Activity implements ChannelsListFragment.OnListFragmentInteractionListener,
        SectionMenuFragment.OnListFragmentInteractionListener, XmsPlayerUICallback  {
    private View channelInfo;
    private TextView currentChannel, channel_number_selector, channelName;
    private List<Channel> channelArrayList;
    private XmsPlayer xmsPlayer;
    private Fragment menuFragment;
    private int currentChannelNumber = 1;
    private ChannelsListFragment channelGridFragment;
    private Realm realm;
    private ImageView channel_icon;
    private RelativeLayout detailsectionContainer;
    private Fragment detailSectionFragment = null;
    private static final long HEADER_ID_0 = 0;
    private static final long HEADER_ID_1 = 1;
    private static final long HEADER_ID_2 = 2;
    private static final long HEADER_ID_3 = 3;
    private static final long HEADER_ID_4 = 4;
    private static final long HEADER_ID_5 = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(xms.com.smarttv.R.layout.activity_tvplayer);

        // init get application service
        Intent getinstalledappintent = new Intent(this, GetInstalledAppService.class);
        this.startService(getinstalledappintent);

        menuFragment = new SectionMenuFragment();
        channelGridFragment = new ChannelsListFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container_channel, menuFragment).commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_container_channel, channelGridFragment).commit();
        getFragmentManager().beginTransaction().hide(channelGridFragment).commit();

        realm = Realm.getDefaultInstance();
        try {
            getClientInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        channelInfo = findViewById(xms.com.smarttv.R.id.channelInfo);
        currentChannel = findViewById(xms.com.smarttv.R.id.current_channel);
        channelName = findViewById(xms.com.smarttv.R.id.channel_name);
        channel_icon = findViewById(R.id.channel_icon);
        channelArrayList = new ArrayList<>();
        channel_number_selector = findViewById(xms.com.smarttv.R.id.channel_number_selector);
        detailsectionContainer = findViewById(R.id.fragment_container_details);

        channelArrayList.add(realm.where(Channel.class).findFirst());
        SimpleExoPlayerView simpleExoPlayerView = findViewById(xms.com.smarttv.R.id.simpleexoplayerview);
        xmsPlayer = new XmsPlayer(this, simpleExoPlayerView, channelArrayList,
                realm.where(User.class).findFirst().getToken_type(), realm.where(User.class).findFirst().getAccess_token());
    }

    @Override
    public void onBackPressed() {
        return;
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_UP) {
            int channel_numberpressed = 10;
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (getFragmentManager().findFragmentByTag("DetailSectionFragment") == null &&
                            channelGridFragment.isHidden() && menuFragment.isHidden()) {
                        showChannelInfo(currentChannelNumber);
                        return false;
                    }
                    getFragmentManager().beginTransaction().hide(channelGridFragment).commit();
                    hideDetailSection("DetailSectionFragment");
                    getFragmentManager().beginTransaction().hide(menuFragment).commit();
                    return false;
                case KeyEvent.KEYCODE_MENU:
                    if (channelGridFragment.isHidden()){
                        if (menuFragment.isHidden()) {
                            getFragmentManager().beginTransaction().show(menuFragment).commit();
                        } else {
                            getFragmentManager().beginTransaction().hide(menuFragment).commit();
                            hideDetailSection("DetailSectionFragment");
                        }
                    }
                    return false;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (channelGridFragment.isHidden() && menuFragment.isHidden()) {
                        getFragmentManager().beginTransaction().show(channelGridFragment).commit();
                        return false;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (channelGridFragment.isHidden() && menuFragment.isHidden()) {
                        currentChannelNumber = xmsPlayer.nextchannel(currentChannelNumber);
                        return true;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (channelGridFragment.isHidden() && menuFragment.isHidden()) {
                        currentChannelNumber = xmsPlayer.previouschannel(currentChannelNumber);
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
                channel_number_selector.setText(String.format("%s%s", channel_number_selector.getText(), String.valueOf(channel_numberpressed)));
                Runnable setchannelnumberRunnable = new Runnable() {
                    public void run() {
                        if (!channel_number_selector.getText().equals("")) {
                            currentChannelNumber = Integer.parseInt((String) channel_number_selector.getText());
                            xmsPlayer.changeChannel(currentChannelNumber, true);
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
    public void onListFragmentInteraction(Channel channel) {
        xmsPlayer.changeChannel(channel.getNumber(), false);
        currentChannelNumber = channel.getNumber();
    }

    @Override
    public void onListFragmentInteraction(CustomHeaderItem item) {
        if (item.getHeaderId() == HEADER_ID_0) {
            detailSectionFragment = new ApplicationsMenu();
        } else if (item.getHeaderId() == HEADER_ID_1) {
            detailSectionFragment = new ApplicationsMenu();
        } else if (item.getHeaderId() == HEADER_ID_2) {
            detailSectionFragment = new MainMenu.SampleFragmentB();
        } else if (item.getHeaderId() == HEADER_ID_3) {
            detailSectionFragment = new MainMenu.SettingsFragment();
        } else if (item.getHeaderId() == HEADER_ID_4) {
            detailSectionFragment = new MainMenu.WebViewFragment();
        }
        showDetailSection (detailSectionFragment);
    }

    @Override
    public void showChannelInfo(int channelindex) {
        Channel channel = realm.where(Channel.class).equalTo("number", channelindex).findFirst();
        Glide.with(this).asBitmap().load(channel.getThumbnail()).into(channel_icon);
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
        mChannelInfoHandler.postDelayed(mChannelInfoRunnable, 3000);
    }

    @SuppressLint("ResourceAsColor")
    private void showDetailSection (Fragment detailFragment) {
        detailsectionContainer.setBackgroundColor(R.color.BlackTransparent);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_details, detailFragment, "DetailSectionFragment")
                .commit();
    }

    private void hideDetailSection (String detailFragmenttag) {
        if (getFragmentManager().findFragmentByTag(detailFragmenttag) != null) {
            detailsectionContainer.setBackgroundColor(0x000000);
            getFragmentManager().beginTransaction()
                    .remove(getFragmentManager().findFragmentByTag(detailFragmenttag))
                    .commit();
        }
    }

    /**
     * checks Client info for change
     * f there is change show the onboarding activity
     * @throws IOException
     */
    private void getClientInfo() throws IOException {
        realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, Preferences.getServerUrl(), user.getToken_type(), user.getAccess_token());
        Call<Client> clientCall = apiInterface.getClientInfo(user.getId());
        clientCall.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(@NonNull Call<Client> call, @NonNull final Response<Client> response) {
                Realm subrealm = Realm.getDefaultInstance();
                if (response.code() == 200 && (subrealm.where(Client.class).findFirst() ==  null
                        || !response.body().getEmail().equals(subrealm.where(Client.class).findFirst().getEmail()))) {

                    subrealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(Client.class);
                            realm.insert(response.body());
                        }
                    });
                }

            }

            @Override
            public void onFailure(@NonNull Call<Client> call, @NonNull Throwable t) {}
        });
    }

}
