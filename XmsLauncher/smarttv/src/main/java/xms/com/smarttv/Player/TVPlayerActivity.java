package xms.com.smarttv.Player;

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
import android.widget.Toast;

import com.XmsPro.xmsproplayer.Interface.XmsPlayerUICallback;
import com.XmsPro.xmsproplayer.XmsPlayer;
import com.bumptech.glide.Glide;
import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.Genre;
import com.eliotohme.data.Movie;
import com.eliotohme.data.Purchasable;
import com.eliotohme.data.Purchase;
import com.eliotohme.data.SectionItem;
import com.eliotohme.data.Stream;
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
import xms.com.smarttv.UI.VOD.VODHomeFragment;
import xms.com.smarttv.app.Preferences;
import xms.com.smarttv.fragments.BackgroundImageFragment;
import xms.com.smarttv.fragments.ChannelsListFragment;
import xms.com.smarttv.fragments.CityGuideFragment;
import xms.com.smarttv.fragments.ClientAccountFragment;
import xms.com.smarttv.fragments.HotelInfoFragment;
import xms.com.smarttv.fragments.LocationDetailFragment;
import xms.com.smarttv.fragments.MapFragment;
import xms.com.smarttv.fragments.PurchaseDialog;
import xms.com.smarttv.fragments.RestaurantsNBarFragment;
import xms.com.smarttv.fragments.SectionMenuFragment;
import xms.com.smarttv.fragments.SpaFitnessFragment;
import xms.com.smarttv.fragments.VODDetailFragment;
import xms.com.smarttv.fragments.VODfragment;
import xms.com.smarttv.fragments.WeatherWidgetFragment;
import xms.com.smarttv.models.Card;
import xms.com.smarttv.services.GetInstalledAppService;
import xms.com.smarttv.services.NotificationService;

import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_ACCOUNT;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_APPS;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_CHANNELS;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_CITYGUIDE;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_HOTEL_INFO;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_RESTOANDBAR;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_SPAANDFITNESS;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_VOD;
import static xms.com.smarttv.fragments.SectionMenuFragment.HEADER_ID_WEATHER;

public class TVPlayerActivity extends Activity implements ChannelsListFragment.ChannelListFragmentListener,
        SectionMenuFragment.SectionMenuFragmentListener, XmsPlayerUICallback, VODfragment.VODFragmentListener,
        CityGuideFragment.CityGudieInterface, LocationDetailFragment.LocationDetailFragmentListener,
        ClientAccountFragment.ClientAccountFragmentListener, VODHomeFragment.VODHomeListener,
        VODDetailFragment.VODDetailFragmentListener {

    // Fragment Tags
    private final String TAG_ITEMLIST = "ItemList";
    private final String TAG_ITEMDETAIL = "ItemDetails";
    private final String TAG_BACKGROUND = "BackgroundFragment";
    private final String TAG_LOCATIONDETAIL = "LocationDetail";
    private final String TAG_VOD = "VOD";
    private final String TAG_VODDETAIL = "VOD_Detail";
    private final String TAG_VODLIST = "VOD_List";
    private final String TAG_CAM = "CAM";

    boolean IN_VOD = false;
    private View channelInfo;
    private TextView currentChannel, channel_number_selector, channelName;
    private XmsPlayer xmsPlayer;
    private Fragment menuFragment;
    private int currentStreamId = 1;
    private List<Stream> streamList;
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

        Intent notificationService = new Intent(this, NotificationService.class);
        this.startService(notificationService);
        realm = Realm.getDefaultInstance();

        /*
         * initialize Fragments menu fragment and channel fragment
         */
        menuFragment = new SectionMenuFragment();
        channelGridFragment = new ChannelsListFragment();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                        R.animator.lb_onboarding_page_indicator_fade_out)
                .add(R.id.fragment_container_channel, menuFragment).commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_container_channel, channelGridFragment).commit();
        getFragmentManager().beginTransaction().hide(channelGridFragment).commit();
        streamList = new ArrayList<>();

        // init element and channel list with player
        channelInfo = findViewById(xms.com.smarttv.R.id.channelInfo);
        currentChannel = findViewById(xms.com.smarttv.R.id.current_channel);
        channelName = findViewById(xms.com.smarttv.R.id.channel_name);
        channel_icon = findViewById(R.id.channel_icon);
        streamList = new ArrayList<>();
        channel_number_selector = findViewById(xms.com.smarttv.R.id.channel_number_selector);
        detailsectionContainer = findViewById(R.id.fragment_container_details);
        currentStreamId = realm.where(Channel.class).findFirst().getStream().getId();
        streamList.add(realm.where(Channel.class).findFirst().getStream());
        SimpleExoPlayerView simpleExoPlayerView = findViewById(R.id.simpleexoplayerview);
        xmsPlayer = new XmsPlayer(this, simpleExoPlayerView, streamList,
                realm.where(User.class).findFirst().getToken_type(), realm.where(User.class).findFirst().getAccess_token());
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            getClientInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**
     * if IN_VOD the user will interact with the player
     * to fastforward if not use key bindings to navigate fragments
     * */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (IN_VOD) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                IN_VOD = false;
                xmsPlayer.releasePlayer();
                detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_VOD);
                showDetailSection (R.id.Main, detailSectionFragment, TAG_BACKGROUND, false);
            }
            xmsPlayer.showProgress();
            return super.dispatchKeyEvent(event);
        }
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_UP) {
            int channel_numberpressed = 10;
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (getFragmentManager().findFragmentByTag(TAG_VOD) != null) {
                        if (getFragmentManager().findFragmentByTag(TAG_VODDETAIL) != null ||
                                getFragmentManager().findFragmentByTag(TAG_VODLIST) != null){
                            return super.dispatchKeyEvent(event);
                        }
                        getFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentByTag(TAG_VOD))
                                .commit();
                        return false;
                    }
                    if (getFragmentManager().findFragmentByTag(TAG_ITEMLIST) == null ||
                            getFragmentManager().findFragmentByTag(TAG_ITEMLIST).isHidden()) {
                            cleaFragmentForPlayer();
                    } else if (getFragmentManager().findFragmentByTag(TAG_ITEMDETAIL) != null) {
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                                        R.animator.lb_onboarding_page_indicator_fade_out)
                                .remove(getFragmentManager().findFragmentByTag(TAG_ITEMDETAIL))
                                .commit();
                    } else {
                        if (getFragmentManager().findFragmentByTag(TAG_LOCATIONDETAIL) == null ||
                                getFragmentManager().findFragmentByTag(TAG_LOCATIONDETAIL) == null ) {
                            hideDetailSection(TAG_ITEMLIST);
                            return false;
                        }
                        return super.dispatchKeyEvent(event);
                    }
                    return false;
                case KeyEvent.KEYCODE_MENU:
                    if (channelGridFragment.isHidden()){
                        if (menuFragment.isHidden()) {
                            getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                                            R.animator.lb_onboarding_page_indicator_fade_out)
                                    .show(menuFragment).commit();
                        } else {
                            getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                                            R.animator.lb_onboarding_page_indicator_fade_out)
                                    .hide(menuFragment).commit();
                        }
                        hideDetailSection(TAG_ITEMLIST);
                    }
                    return false;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (channelGridFragment.isHidden() && menuFragment.isHidden() &&
                            (getFragmentManager().findFragmentByTag(TAG_VOD) == null || getFragmentManager().findFragmentByTag(TAG_VOD).isHidden())) {
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                                        R.animator.lb_onboarding_page_indicator_fade_out)
                                .show(channelGridFragment).commit();
                        return false;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (channelGridFragment.isHidden() && menuFragment.isHidden() &&
                            (getFragmentManager().findFragmentByTag(TAG_VOD) == null || getFragmentManager().findFragmentByTag(TAG_VOD).isHidden())) {
                        int channel_id = realm.where(Channel.class).equalTo("stream.id", currentStreamId).findFirst().getNumber();
                        List<Channel> result = realm.where(Channel.class).greaterThan("number", channel_id).findAll().sort("number");
                        if (result.size() != 0 ) {
                            onChannelSelected(result.get(0), true);
                        } else {
                            result = realm.where(Channel.class).findAll().sort("number");
                            onChannelSelected(result.get(0), true);
                        }
                        currentStreamId = result.get(0).getStream().getId();
                        return true;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (channelGridFragment.isHidden() && menuFragment.isHidden() &&
                            (getFragmentManager().findFragmentByTag(TAG_VOD) == null || getFragmentManager().findFragmentByTag(TAG_VOD).isHidden())) {
                        int channel_id = realm.where(Channel.class).equalTo("stream.id", currentStreamId).findFirst().getNumber();
                        List<Channel> result = realm.where(Channel.class).lessThan("number", channel_id).findAll().sort("number");
                        if (result.size() != 0 ) {
                            onChannelSelected(result.get(result.size()-1), true);
                        } else {
                            result = realm.where(Channel.class).findAll().sort("number");
                            onChannelSelected(result.get(result.size() - 1), true);
                        }
                        currentStreamId = result.get(result.size() - 1).getStream().getId();
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
                        Channel channel = realm.where(Channel.class).equalTo("number", Integer.parseInt((String) channel_number_selector.getText())).findFirst();
                        if (channel != null) {
                            onChannelSelected(channel, true);
                            currentStreamId = channel.getStream().getId();
                        }
                        channel_number_selector.setText("");
                        channel_number_selector.setVisibility(View.INVISIBLE);
                    }
                    }
                };
                setchannelnumberHandler.postDelayed(setchannelnumberRunnable, 1000);
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
    public void onChannelSelected(Channel channel, boolean showinfo) {
        streamList.clear();
        if (channel.getPrice() > 0 && !channel.isPurchased()) {
            Toast.makeText(this, "This is a premium Channel long press to purchase", Toast.LENGTH_LONG).show();
        } else {
            if (currentStreamId != channel.getStream().getId()) {
                streamList.add(channel.getStream());
                xmsPlayer.changeSource(streamList, showinfo);
                currentStreamId = channel.getStream().getId();
            }
        }
    }

    /**
     * Show Purchase Dialog
     * */
    @Override
    public void onChannelPurchased(Channel item) {
        getFragmentManager().beginTransaction().add(R.id.fragment_container_purshase, PurchaseDialog.newInstance(item, "Channel")).commit();
    }

    /**
     * @param item
     * ListFragmentInteraction listener that show corresponding Fragment Detail
     */
    @Override
    public void onSectionClicked(CustomHeaderItem item) {
        if (item.getHeaderId() == HEADER_ID_CHANNELS) {
            cleaFragmentForPlayer();
        } else if (item.getHeaderId() == HEADER_ID_HOTEL_INFO) {
            xmsPlayer.releasePlayer();
            ShowHotelInfo();
        } else if (item.getHeaderId() == HEADER_ID_RESTOANDBAR) {
            xmsPlayer.releasePlayer();
            detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_RESTOANDBAR);
            showDetailSection (R.id.Main, detailSectionFragment,TAG_BACKGROUND , false);
            detailSectionFragment = new RestaurantsNBarFragment();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, TAG_ITEMLIST, true);
        } else if (item.getHeaderId() == HEADER_ID_WEATHER) {
            xmsPlayer.releasePlayer();
            detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_WEATHER);
            showDetailSection (R.id.Main, detailSectionFragment, TAG_BACKGROUND, false);
            detailSectionFragment = new WeatherWidgetFragment();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, TAG_ITEMLIST, true);
        } else if (item.getHeaderId() == HEADER_ID_CITYGUIDE) {
            xmsPlayer.releasePlayer();
            detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_CITYGUIDE);
            showDetailSection (R.id.Main, detailSectionFragment, TAG_BACKGROUND, false);
            detailSectionFragment = new CityGuideFragment();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, TAG_ITEMLIST, true);
        } else if (item.getHeaderId() == HEADER_ID_SPAANDFITNESS) {
            xmsPlayer.releasePlayer();
            detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_SPAANDFITNESS);
            showDetailSection (R.id.Main, detailSectionFragment, TAG_BACKGROUND, false);
            detailSectionFragment = new SpaFitnessFragment();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, TAG_ITEMLIST, true);
        } else if (item.getHeaderId() == HEADER_ID_VOD) {
            xmsPlayer.releasePlayer();
            hideDetailSection(TAG_ITEMLIST);
            detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_VOD);
            showDetailSection (R.id.Main, detailSectionFragment, TAG_BACKGROUND, false);
            detailSectionFragment = new VODHomeFragment();
            showDetailSection (R.id.fragment_vod_list, detailSectionFragment, TAG_VOD, false);
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                            R.animator.lb_onboarding_page_indicator_fade_out)
                    .hide(menuFragment).commit();
        } else if (item.getHeaderId() == HEADER_ID_APPS) {
            xmsPlayer.releasePlayer();
            detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_HOTEL_INFO);
            showDetailSection (R.id.Main, detailSectionFragment, TAG_BACKGROUND, false);
            detailSectionFragment = new ApplicationsMenu();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, TAG_ITEMLIST, true);
        } else if (item.getHeaderId() == HEADER_ID_ACCOUNT) {
            xmsPlayer.releasePlayer();
            detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_ACCOUNT);
            showDetailSection (R.id.Main, detailSectionFragment, TAG_BACKGROUND, false);
            detailSectionFragment = new ClientAccountFragment();
            showDetailSection (R.id.fragment_container_details, detailSectionFragment, TAG_ITEMLIST, true);
        }

    }


    @Override
    public void showChannelInfo(int streamindex) {
        Channel channel = realm.where(Channel.class).equalTo("stream.id", streamindex).findFirst();
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


    @Override
    public void LocationSelected(Object item) {
        detailSectionFragment = LocationDetailFragment.newInstance((SectionItem) item);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                        R.animator.lb_onboarding_page_indicator_fade_out)
                .replace(R.id.fragment_container_details, detailSectionFragment, TAG_LOCATIONDETAIL)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void LoadMap(Double latitude, Double longitude, int zoom) {
        detailSectionFragment = MapFragment.newInstance(latitude, longitude, zoom);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                        R.animator.lb_onboarding_page_indicator_fade_out)
                .replace(R.id.fragment_container_details, detailSectionFragment, "Map")
                .addToBackStack(null)
                .commit();

    }


    @Override
    public void ServiceClicked(Card card) {

    }


    @Override
    public void MovieSelected(Movie movie) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_vod_list, VODDetailFragment.newInstance(movie), TAG_VODDETAIL)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void GenreSelected(Genre genre) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_vod_list, VODfragment.newInstance(genre.getId()), TAG_VODLIST)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void purchase(Movie movie) {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_purshase, PurchaseDialog.newInstance(movie, "Movie"), TAG_VOD)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void watch(Movie movie) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                        R.animator.lb_onboarding_page_indicator_fade_out)
                .remove(getFragmentManager().findFragmentByTag(TAG_VOD)).commit();

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                        R.animator.lb_onboarding_page_indicator_fade_out)
                .remove(getFragmentManager().findFragmentByTag(TAG_VODDETAIL)).commit();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                        R.animator.lb_onboarding_page_indicator_fade_out)
                .remove(getFragmentManager().findFragmentByTag(TAG_BACKGROUND)).commit();

        streamList.clear();
        streamList.add(movie.getStream());
        xmsPlayer.initializePlayer();
        xmsPlayer.changeSource(streamList,false);
        IN_VOD = true;
    }


    private void hideDetailSection(String DetailSectionFragment) {
        if (getFragmentManager().findFragmentByTag(DetailSectionFragment) != null) {
            detailsectionContainer.setBackgroundColor(0x000000);
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                            R.animator.lb_onboarding_page_indicator_fade_out)
                    .remove(getFragmentManager().findFragmentByTag(DetailSectionFragment))
                    .commit();
        }
    }


    private void ShowHotelInfo () {
        detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_HOTEL_INFO);
        showDetailSection (R.id.Main, detailSectionFragment, TAG_BACKGROUND, false);
        detailSectionFragment = new HotelInfoFragment();
        showDetailSection (R.id.fragment_container_details, detailSectionFragment, TAG_ITEMLIST, true);
    }

    /**
     * checks Client info for change
     * if there is change show the onboarding activity
     * @throws IOException
     */
    private void getClientInfo() throws IOException {
        User user = realm.where(User.class).findFirst();
        assert user != null;
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, Preferences.getServerUrl(), user.getToken_type(), user.getAccess_token());
        Call<Client> clientCall = apiInterface.getClientInfo(user.getId());
        clientCall.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(@NonNull Call<Client> call, @NonNull final Response<Client> response) {
                if (response.body() != null) {
                    if (response.code() == 200) {
                        Realm subrealm = Realm.getDefaultInstance();
                        Client client = subrealm.where(Client.class).findFirst();
                        if (client != null ) {
                            if (!response.body().getEmail().equals(client.getEmail())) {
                                ShowHotelInfo();
                            } else {
                                xmsPlayer.initializePlayer();
                                showChannelInfo(currentStreamId);
                            }
                        }
                        subrealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realmexec) {
                                if (realmexec.where(Client.class).findFirst() !=  null) {
                                    realmexec.delete(Client.class);
                                    realmexec.delete(Purchase.class);
                                    realmexec.delete(Purchasable.class);
                                }
                                realmexec.insert(response.body());
                                List<Purchase> purchases = realmexec.where(Purchase.class).findAll();
                                for (final Purchase purchase: purchases) {
                                    switch (purchase.getPurchasableType()){
                                        case "App\\Channel":
                                            realmexec.where(Channel.class).equalTo("id", purchase.getPurchasableId()).findFirst().setPurchased(true);
                                            break;
                                        case "App\\Movie":
                                            realmexec.where(Movie.class).equalTo("id", purchase.getPurchasableId()).findFirst().setPurchased(true);
                                            break;
                                    }
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Client> call, @NonNull Throwable t) {}
        });
    }

    /**
     * Clear fragment to play Channels
     * if BackgroundFragment is showing initialize the player
     * and change the channel to the current channel number
     */
    private void cleaFragmentForPlayer () {
        Channel channel = realm.where(Channel.class).equalTo("stream.id", currentStreamId).findFirst();
        if (getFragmentManager().findFragmentByTag(TAG_BACKGROUND) != null) {
            xmsPlayer.initializePlayer();
            streamList.clear();
            streamList.add(channel.getStream());
            xmsPlayer.changeSource(streamList, true);
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                            R.animator.lb_onboarding_page_indicator_fade_out)
                    .remove(getFragmentManager().findFragmentByTag(TAG_BACKGROUND)).commit();
        }
        if (getFragmentManager().findFragmentByTag(TAG_ITEMLIST) == null &&
                channelGridFragment.isHidden() && menuFragment.isHidden()) {
            showChannelInfo(currentStreamId);
        }
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                        R.animator.lb_onboarding_page_indicator_fade_out)
                .hide(channelGridFragment).commit();
        hideDetailSection(TAG_ITEMLIST);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                        R.animator.lb_onboarding_page_indicator_fade_out)
                .hide(menuFragment).commit();
    }


    private void showDetailSection (int ViewId, Fragment detailFragment, String tag, boolean showBackground) {
        if (showBackground) {
            detailsectionContainer.setBackgroundColor(getResources().getColor(R.color.BlackLightTransparent));
        }
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                        R.animator.lb_onboarding_page_indicator_fade_out)
                .replace(ViewId, detailFragment, tag)
                .commit();
    }

}
