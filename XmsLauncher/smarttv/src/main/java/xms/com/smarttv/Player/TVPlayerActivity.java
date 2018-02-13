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
import xms.com.smarttv.app.Preferences;
import xms.com.smarttv.fragments.ChannelsListFragment;
import xms.com.smarttv.fragments.HotelInfoFragment;
import xms.com.smarttv.fragments.ItemDetailFragment;
import xms.com.smarttv.fragments.MapFragment;
import xms.com.smarttv.fragments.RestaurantsNBarFragment;
import xms.com.smarttv.fragments.SectionMenuFragment;
import xms.com.smarttv.fragments.VODfragment;
import xms.com.smarttv.fragments.WebViewFragment;
import xms.com.smarttv.models.Card;
import xms.com.smarttv.services.GetInstalledAppService;

import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_CHANNELS;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_CITYGUIDE;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_HOTEL_INFO;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_OFFERS;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_RESTOANDBAR;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_SPAANDFITNESS;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_VOD;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_WEATHER;

public class TVPlayerActivity extends Activity implements ChannelsListFragment.OnListFragmentInteractionListener,
        SectionMenuFragment.OnListFragmentInteractionListener, XmsPlayerUICallback, VODfragment.OnListFragmentInteractionListener {
    private View channelInfo;
    private TextView currentChannel, channel_number_selector, channelName;
    private XmsPlayer xmsPlayer;
    private Fragment menuFragment;
    private int currentChannelNumber = 1;
    private List<Channel> channelList ;
    private ChannelsListFragment channelGridFragment;
    private Realm realm;
    private ImageView channel_icon;
    private RelativeLayout detailsectionContainer;
    private Fragment detailSectionFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(xms.com.smarttv.R.layout.activity_tvplayer);

        // init get application service and save application info in database
        Intent getinstalledappintent = new Intent(this, GetInstalledAppService.class);
        this.startService(getinstalledappintent);

        /*
         * initialize Fragments menu fragment and channel fragment
         */
        menuFragment = new SectionMenuFragment();
        channelGridFragment = new ChannelsListFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container_channel, menuFragment).commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_container_channel, channelGridFragment).commit();
        getFragmentManager().beginTransaction().hide(channelGridFragment).commit();
        channelList = new ArrayList<>();

        // init Realm and getClient
        realm = Realm.getDefaultInstance();
        try {
            getClientInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // init element and channel list with player
        channelInfo = findViewById(xms.com.smarttv.R.id.channelInfo);
        currentChannel = findViewById(xms.com.smarttv.R.id.current_channel);
        channelName = findViewById(xms.com.smarttv.R.id.channel_name);
        channel_icon = findViewById(R.id.channel_icon);
        List<Channel> channelArrayList = new ArrayList<>();
        channel_number_selector = findViewById(xms.com.smarttv.R.id.channel_number_selector);
        detailsectionContainer = findViewById(R.id.fragment_container_details);

        channelArrayList.add(realm.where(Channel.class).findFirst());
        SimpleExoPlayerView simpleExoPlayerView = findViewById(xms.com.smarttv.R.id.simpleexoplayerview);
        xmsPlayer = new XmsPlayer(this, simpleExoPlayerView, channelArrayList,
                realm.where(User.class).findFirst().getToken_type(), realm.where(User.class).findFirst().getAccess_token());
    }

    /**
     * Prevent User to use back button while in Player
     */
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
                    cleaFragmentForPlayer();
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_MENU:
                    if (channelGridFragment.isHidden()){
                        if (menuFragment.isHidden()) {
                            getFragmentManager().beginTransaction().show(menuFragment).commit();
                        } else {
                            getFragmentManager().beginTransaction().hide(menuFragment).commit();
                        }
                        hideDetailSection("DetailSectionFragment");
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
                        List<Channel> result = realm.where(Channel.class).greaterThan("number", currentChannelNumber).findAllSorted("number");
                        if (result.size() != 0 ) {
                            onListFragmentInteraction(result.get(0), true);
                        } else {
                            result = realm.where(Channel.class).findAllSorted("number");
                            onListFragmentInteraction(result.get(0), true);
                        }
                        currentChannelNumber = result.get(0).getNumber();
                        return true;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (channelGridFragment.isHidden() && menuFragment.isHidden()) {
                        List<Channel> result = realm.where(Channel.class).lessThan("number", currentChannelNumber).findAllSorted("number");
                        if (result.size() != 0 ) {
                            onListFragmentInteraction(result.get(result.size()-1), true);
                        } else {
                            result = realm.where(Channel.class).findAllSorted("number");
                            onListFragmentInteraction(result.get(result.size() - 1), true);
                        }
                        currentChannelNumber = result.get(result.size() - 1).getNumber();
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
                            Channel channel = realm.where(Channel.class).equalTo("number", currentChannelNumber).findFirst();
                            if (channel != null) {
                                onListFragmentInteraction(channel, true);
                                channel_number_selector.setText("");
                                channel_number_selector.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                };
                setchannelnumberHandler.postDelayed(setchannelnumberRunnable, 2000);

            }
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * ChannelFragment listener to change the stream to the corresponding channel
     * takes
     * @param channel
     * @param showinfo
     * show info flag to show channel detail section
     */
    @Override
    public void onListFragmentInteraction(Channel channel, boolean showinfo) {
        channelList.clear();
        channelList.add(channel);
        xmsPlayer.changeSource(channelList, showinfo);
        currentChannelNumber = channel.getNumber();
    }

    /**
     * @param item
     * ListFragmentInteraction listener that show corresponding Fragment Detail
     */
    @Override
    public void onListFragmentInteraction(CustomHeaderItem item) {
        if (item.getHeaderId() == HEADER_ID_CHANNELS) {
            cleaFragmentForPlayer();
        } else if (item.getHeaderId() == HEADER_ID_OFFERS) {
            detailSectionFragment = new ApplicationsMenu();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, "DetailSectionFragment");
        } else if (item.getHeaderId() == HEADER_ID_HOTEL_INFO) {
            xmsPlayer.releasePlayer();
            detailSectionFragment = new HotelInfoFragment();
            showDetailSection (R.id.Main, detailSectionFragment, "HotelDetailsFragment");
        } else if (item.getHeaderId() == HEADER_ID_RESTOANDBAR) {
            detailSectionFragment = new RestaurantsNBarFragment();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, "DetailSectionFragment");
        } else if (item.getHeaderId() == HEADER_ID_WEATHER) {
            detailSectionFragment = new WebViewFragment();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, "DetailSectionFragment");
        } else if (item.getHeaderId() == HEADER_ID_CITYGUIDE) {
            detailSectionFragment = new MapFragment();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, "DetailSectionFragment");
        } else if (item.getHeaderId() == HEADER_ID_SPAANDFITNESS) {
            detailSectionFragment = new ApplicationsMenu();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, "DetailSectionFragment");
        } else if (item.getHeaderId() == HEADER_ID_VOD) {
            detailSectionFragment = new VODfragment();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, "DetailSectionFragment");
        }

    }

    @Override
    public void onListFragmentInteraction(Object item) {
        detailSectionFragment = new ItemDetailFragment().newInstance((Card) item);
        showDetailSection (R.id.fragment_container_details, detailSectionFragment, "DetailSectionFragment");
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
    private void showDetailSection (int ViewId, Fragment detailFragment, String tag) {
        detailsectionContainer.setBackgroundColor(getResources().getColor(R.color.BlackLightTransparent));
        getFragmentManager().beginTransaction()
                .replace(ViewId, detailFragment, tag)
                .commit();
    }

    private void hideDetailSection(String DetailSectionFragment) {
        if (getFragmentManager().findFragmentByTag(DetailSectionFragment) != null) {
            detailsectionContainer.setBackgroundColor(0x000000);
            getFragmentManager().beginTransaction()
                    .remove(getFragmentManager().findFragmentByTag(DetailSectionFragment))
                    .commit();
        }
    }

    /**
     * checks Client info for change
     * if there is change show the onboarding activity
     * @throws IOException
     */
    private void getClientInfo() throws IOException {
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

    /**
     * Clear fragment to play Channels
     * if HotelDetailsFragment is showing initialize the player
     * and change the channel to the current channel number
     */
    private void cleaFragmentForPlayer () {
        if (getFragmentManager().findFragmentByTag("HotelDetailsFragment") != null) {
            xmsPlayer.initializePlayer();
            xmsPlayer.changeSource(realm.where(Channel.class).equalTo("number", currentChannelNumber).findAll(), true);
            hideDetailSection("HotelDetailsFragment");
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("HotelDetailsFragment")).commit();
        }
        if (getFragmentManager().findFragmentByTag("DetailSectionFragment") == null &&
                channelGridFragment.isHidden() && menuFragment.isHidden()) {
            showChannelInfo(currentChannelNumber);
        }
        getFragmentManager().beginTransaction().hide(channelGridFragment).commit();
        hideDetailSection("DetailSectionFragment");
        getFragmentManager().beginTransaction().hide(menuFragment).commit();
    }
}
