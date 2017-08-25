package xms.com.smarttv.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.eliotohme.data.Channel;
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.BroadcastRecievers.ConnectionStateReceiver;

public class onBootService extends IntentService {
    private Realm realm;
    private ConnectionStateReceiver mNetworkReceiver;

    public onBootService() {
        super("onBootService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        mNetworkReceiver = new ConnectionStateReceiver(new ConnectionStateReceiver.ConnectionStateInterface() {
            @Override
            public void result(Boolean connected) {
                if (connected) startService ();
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver,intentFilter);

    }

    /**
     * main service
     */
    private void startService () {
        // Initialize Realm
        Realm.init(this);

        // set @realmConfiguration for development database will be rewritten on change
        final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        checkdevicereg();
    }

    /*
    * Check if device has tokens
    * if HAS tokens getchannel list
    * if NOT register and get token
    */
    private void checkdevicereg () {
        // select user from database
        User user = realm.where(User.class).findFirst();

        if (user != null && user.getAccess_token() != null) {
            // if user is found continue
            getChannels();
        } else {
            // if user row is null register device
            registerdevice();
        }
    }

    /*
    * Register Device set token for api authentication
    */
    private void registerdevice () {
        // initialize apiInterface
        Intent dialogIntent = new Intent(getBaseContext(), DialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(dialogIntent);
    }

    /*
    * Get Channels and save them in database
    * function will always be called when device is turned on
    */
    private void getChannels () {
        User user = realm.where(User.class).findFirst();
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, user.getToken_type(), user.getAccess_token());
        Call<List<Channel>> channelCall = apiInterface.getChannel();
        channelCall.enqueue(new Callback<List<Channel>>() {
            @Override
            public void onResponse(Call<List<Channel>> call, final Response<List<Channel>> response) {
                Realm backgroundRealm = Realm.getDefaultInstance();
                backgroundRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (response.code() == 200) {
                            realm.insertOrUpdate(response.body());

                            // start TVplayer
                            startTVplayer ();

                        } else {
                            realm.deleteAll();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Channel>> call, @NonNull Throwable t) {
                Log.e("TEST", String.valueOf(t));
            }
        });
    }

    /*
    * Start Tvplayer bydefault
    */
    private void startTVplayer () {
        // start TVplayer
        Intent intent = new Intent("com.XmsPro.xmsproplayer.TvPlayer");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);
    }
}