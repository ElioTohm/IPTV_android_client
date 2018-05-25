package xms.com.smarttv.Player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

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
import xms.com.smarttv.UI.SplashScreen;
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
import xms.com.smarttv.services.MonitoringService;
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
        VODDetailFragment.VODDetailFragmentListener, View.OnClickListener {

    // Fragment Tags
    private final String TAG_ITEMLIST = "ItemList";
    private final String TAG_ITEMDETAIL = "ItemDetails";
    private final String TAG_BACKGROUND = "BackgroundFragment";
    private final String TAG_LOCATIONDETAIL = "LocationDetail";
    private final String TAG_VOD = "VOD";
    private final String TAG_VODDETAIL = "VOD_Detail";
    private final String TAG_VODLIST = "VOD_List";
    private PlayerView simpleExoPlayerView;
    boolean doubleBackToExitPressedOnce = false;
    boolean IN_VOD = false;
    private XmsPlayer xmsPlayer;
    private Fragment menuFragment;
    private int currentChannelStreamId = 1;
    private int movieStreamid = 0;
    private List<Stream> streamList;
    private ChannelsListFragment channelGridFragment;
    private Realm realm;
    private ImageView stream_thumbnail;
    private TextView stream_number, channel_number_selector, stream_name;
    private RelativeLayout detailsectionContainer;
    private LinearLayout progression_section;
    private Fragment detailSectionFragment = null;
    private ImageButton audio_button, video_button, sub_button;
    private PlayerControlView playerControlView;

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        simpleExoPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(xms.com.smarttv.R.layout.activity_tvplayer);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        channel_number_selector = findViewById(xms.com.smarttv.R.id.channel_number_selector);
        detailsectionContainer = findViewById(R.id.fragment_container_details);
        playerControlView = findViewById(R.id.player_control_view);

        View stream_info_view = playerControlView.getRootView();
        stream_thumbnail = stream_info_view.findViewById(R.id.thumbnail);
        stream_number = stream_info_view.findViewById(R.id.stream_id);
        stream_name = stream_info_view.findViewById(R.id.stream_name);
        progression_section = stream_info_view.findViewById(R.id.exo_bar_section);
        audio_button = stream_info_view.findViewById(R.id.Audio);
        video_button = stream_info_view.findViewById(R.id.Video);
        sub_button = stream_info_view.findViewById(R.id.Subtitle);

        Channel firstchannel = realm.where(Channel.class).equalTo("number", 1).findFirst();
        currentChannelStreamId = firstchannel.getStream().getId();
        streamList.add(firstchannel.getStream());
        simpleExoPlayerView = findViewById(R.id.simpleexoplayerview);
        xmsPlayer = new XmsPlayer(this, simpleExoPlayerView, playerControlView,
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
        hideSystemUi();
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
    public void onBackPressed() {}

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (IN_VOD) {
            if (action == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (doubleBackToExitPressedOnce) {
                    IN_VOD = false;
                    xmsPlayer.releasePlayer();
                    detailSectionFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_VOD);
                    showDetailSection (R.id.Main, detailSectionFragment, TAG_BACKGROUND, false);
                    movieStreamid = 0;
                    return false;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 3000);

            }

            if (playerControlView.isVisible()) {
                return super.dispatchKeyEvent(event);
            }

            showChannelInfo(movieStreamid, 10000, true);

            return false;
        }

        // if channel info is shown only catch the back event
        if (playerControlView.isVisible() && playerControlView.getShowTimeoutMs() == 0 && keyCode != KeyEvent.KEYCODE_BACK && action == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }
        if (action == KeyEvent.ACTION_UP) {
            int channel_numberpressed = 10;
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (getFragmentManager().findFragmentByTag(TAG_VOD) != null) {
                        if (getFragmentManager().findFragmentByTag(TAG_VODDETAIL) != null ||
                                getFragmentManager().findFragmentByTag(TAG_VODLIST) != null){
                            getFragmentManager().popBackStack();
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
                            (getFragmentManager().findFragmentByTag(TAG_VOD) == null || getFragmentManager().findFragmentByTag(TAG_VOD).isHidden()) &&
                            !playerControlView.isVisible()) {
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
                        int channel_id = realm.where(Channel.class).equalTo("stream.id", currentChannelStreamId).findFirst().getNumber();
                        List<Channel> result = realm.where(Channel.class).greaterThan("number", channel_id).findAll().sort("number");
                        if (result.size() != 0 ) {
                            onChannelSelected(result.get(0), true);
                        } else {
                            result = realm.where(Channel.class).findAll().sort("number");
                            onChannelSelected(result.get(0), true);
                        }
                        currentChannelStreamId = result.get(0).getStream().getId();
                        return true;
                    }
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (channelGridFragment.isHidden() && menuFragment.isHidden() &&
                            (getFragmentManager().findFragmentByTag(TAG_VOD) == null || getFragmentManager().findFragmentByTag(TAG_VOD).isHidden())) {
                        int channel_id = realm.where(Channel.class).equalTo("stream.id", currentChannelStreamId).findFirst().getNumber();
                        List<Channel> result = realm.where(Channel.class).lessThan("number", channel_id).findAll().sort("number");
                        if (result.size() != 0 ) {
                            onChannelSelected(result.get(result.size()-1), true);
                        } else {
                            result = realm.where(Channel.class).findAll().sort("number");
                            onChannelSelected(result.get(result.size() - 1), true);
                        }
                        currentChannelStreamId = result.get(result.size() - 1).getStream().getId();
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
                            currentChannelStreamId = channel.getStream().getId();
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

    @Override
    public void onChannelSelected(Channel channel, boolean showinfo) {
        streamList.clear();
        if (channel.getPrice() > 0 && !channel.isPurchased()) {
            Toast.makeText(this, "This is a premium Channel long press to purchase", Toast.LENGTH_LONG).show();
        } else {
            if (currentChannelStreamId != channel.getStream().getId()) {
                streamList.add(channel.getStream());
                xmsPlayer.changeSource(streamList, showinfo);
                currentChannelStreamId = channel.getStream().getId();
                monitor(MonitoringService.TAG_STREAM, channel.getName());
            }
        }
    }

    @Override
    public void onChannelPurchased(Channel item) {
        getFragmentManager().beginTransaction().add(R.id.fragment_container_purshase, PurchaseDialog.newInstance(item, "Channel")).commit();
    }

    @Override
    public void onSectionClicked(CustomHeaderItem item) {
        monitor(MonitoringService.TAG_ACTIVITY, item.getName());
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
        IN_VOD = false;
    }

    @Override
    public void showChannelInfo(int streamindex, int duration, boolean update) {
        Stream stream = realm.where(Stream.class).equalTo("id", streamindex).findFirst();
        if (stream.getChannel() > 0) {
            Channel channel = realm.where(Channel.class).equalTo("number", stream.getChannel()).findFirst();
            Glide.with(this).asBitmap().load(channel.getThumbnail()).into(stream_thumbnail);
            stream_number.setText(String.valueOf(channel.getNumber()));
            stream_name.setText(channel.getName());
        } else {
            Movie movie = realm.where(Movie.class).equalTo("id", stream.getMovie()).findFirst();
            Glide.with(this).asBitmap().load(movie.getPoster()).into(stream_thumbnail);
            stream_number.setText("");
            stream_name.setText(movie.getTitle());
        }

        if (xmsPlayer.getDuration() > 0) {
            progression_section.setVisibility(LinearLayout.VISIBLE);
        } else {
            progression_section.setVisibility(LinearLayout.INVISIBLE);
        }
        if (update) {
            updatebuttonSelector();
        } else {
            hidebuttonSekector();
        }

        playerControlView.setShowTimeoutMs(duration);
        playerControlView.show();
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

        movieStreamid = movie.getStream().getId();
        streamList.clear();
        streamList.add(movie.getStream());
        xmsPlayer.initializePlayer();
        xmsPlayer.changeSource(streamList,false);
        monitor(MonitoringService.TAG_STREAM, movie.getTitle());
        showChannelInfo(movie.getStream().getId(), 1000, true);
        IN_VOD = true;
    }

    /**
     * Button clicked handler to add multi audio and subtitle support
     * */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Audio ||
                v.getId() == R.id.Video||
                v.getId() == R.id.Subtitle) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = xmsPlayer.getmappedTrackInfo();
            if (mappedTrackInfo != null) {
                xmsPlayer.gettrackSelectionHelper().showSelectionDialog(
                        this, getResources().getResourceEntryName(v.getId()), mappedTrackInfo, (int) v.getTag());
            }
        }
    }

    /**
     * show/hide Video/Audio/Sub buttons
     * if the streams contains the streams
     * */
    private void updatebuttonSelector() {
        if (xmsPlayer == null) {
            return;
        }
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = xmsPlayer.getmappedTrackInfo();
        if (mappedTrackInfo == null) {
            return;
        }

        View view = playerControlView.getRootView();
        audio_button.setVisibility(View.INVISIBLE);
        video_button.setVisibility(View.INVISIBLE);
        sub_button.setVisibility(View.INVISIBLE);

        for (int i = 0; i < mappedTrackInfo.length; i++) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length > 0) {
                switch (xmsPlayer.getplayer().getRendererType(i)) {
                    case C.TRACK_TYPE_AUDIO:
                        if (trackGroups.length > 1) {
                            audio_button.setVisibility(View.VISIBLE);
                            audio_button.setTag(i);
                            audio_button.setOnClickListener(this);
                        }
                        break;
                    case C.TRACK_TYPE_VIDEO:
                        if (trackGroups.length > 1) {
                            video_button.setVisibility(View.VISIBLE);
                            video_button.setTag(i);
                            video_button.setOnClickListener(this);
                        }
                        break;
                    case C.TRACK_TYPE_TEXT:
                        if ((trackGroups.length == 1 &&
                                !trackGroups.get(0).getFormat(0).sampleMimeType.equals("application/cea-608")) ||
                                (trackGroups.length > 1)) {
                            sub_button.setVisibility(View.VISIBLE);
                            sub_button.setTag(i);
                            sub_button.setOnClickListener(this);
                        }
                        break;
                    default:
                        continue;
                }
            }
        }
    }

    /**
     * hide button Selector
     * in case of zapping
     * */
    private void hidebuttonSekector() {
        audio_button.setVisibility(View.INVISIBLE);
        video_button.setVisibility(View.INVISIBLE);
        sub_button.setVisibility(View.INVISIBLE);
    }

    /**
     * @param DetailSectionFragment
     * to hide the fragment with the related tag
     */
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

    /**
     * show the fragment in the
     * @param ViewId
     * with the fragment class
     * @param detailFragment
     * and tag
     * @param tag
     * with flag that shows the background Fragment
     * @param showBackground
     */
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

    /**
     * show the hotel info fragment sections
     */
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
                            if (response.body().getId() != null) {
                                if (!response.body().getEmail().equals(client.getEmail())) {
                                    ShowHotelInfo();
                                } else {
                                    xmsPlayer.initializePlayer();
                                    xmsPlayer.changeSource(streamList, true);
                                }
                            } else {
                                new AlertDialog.Builder(TVPlayerActivity.this)
                                        .setMessage("No client registered in this room retry later when client is registered")
                                        .setCancelable(false)
                                        .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent dialogIntent = new Intent(getBaseContext(), SplashScreen.class);
                                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                getApplication().startActivity(dialogIntent);
                                            }
                                        })
                                        .show();
                            }
                        } else {
                            new AlertDialog.Builder(TVPlayerActivity.this)
                                    .setMessage("No client registered in this room retry later when client is registered")
                                    .setCancelable(false)
                                    .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent dialogIntent = new Intent(getBaseContext(), SplashScreen.class);
                                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            getApplication().startActivity(dialogIntent);
                                        }
                                    })
                                    .show();
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
        Channel channel = realm.where(Channel.class).equalTo("stream.id", currentChannelStreamId).findFirst();
        if (getFragmentManager().findFragmentByTag(TAG_BACKGROUND) != null) {
            xmsPlayer.initializePlayer();
            streamList.clear();
            streamList.add(channel.getStream());
            xmsPlayer.changeSource(streamList, true);
            monitor(MonitoringService.TAG_STREAM, channel.getName());
            hidebuttonSekector();
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.lb_onboarding_page_indicator_fade_in,
                            R.animator.lb_onboarding_page_indicator_fade_out)
                    .remove(getFragmentManager().findFragmentByTag(TAG_BACKGROUND)).commit();
        }
        if (getFragmentManager().findFragmentByTag(TAG_ITEMLIST) == null &&
                channelGridFragment.isHidden() && menuFragment.isHidden()) {
            if (playerControlView.isVisible() && playerControlView.getShowTimeoutMs() == 0) {
                playerControlView.hide();
            } else {
                showChannelInfo(currentChannelStreamId, 0, true);
            }
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

    private void monitor (String TAG, String info) {
        Intent monitoring = new Intent(this, MonitoringService.class);
        monitoring.putExtra(TAG, info);
        this.startService(monitoring);
    }

}
