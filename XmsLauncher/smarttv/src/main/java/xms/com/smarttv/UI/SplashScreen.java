package xms.com.smarttv.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.Player.TVPlayerActivity;
import xms.com.smarttv.R;

public class SplashScreen extends Activity {
    private Realm realm;
    private User user;
    private  String TKN_TYPE;
    private String TKN;
    private int USER_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        ConnectionStateReceiver mNetworkReceiver = new ConnectionStateReceiver(new ConnectionStateReceiver.ConnectionStateInterface() {
//            @Override
//            public void result(Boolean connected) {
//                if (connected) checkdevicereg ();
//            }
//        });
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(mNetworkReceiver,intentFilter);
        checkdevicereg();

    }

    /*
    * Check if device has tokens
    * if HAS tokens getchannel list
    * if NOT register and get token
    */
    private void checkdevicereg () {
        // Initialize Realm
        Realm.init(this);

        // set @realmConfiguration for development database will be rewritten on change
        final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        // select user from database
        if (user != null && user.getAccess_token() != null) {
            // if user is found continue
            User user = realm.where(User.class).findFirst();
            TKN_TYPE = user.getToken_type();
            TKN = user.getAccess_token();
            USER_ID = user.getId();
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
        // initalize dialog
        final ApiInterface apiInterface = ApiService.createService(ApiInterface.class);

        final View view = getLayoutInflater().inflate(R.layout.client_register_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this);
        dialog.setView(view);
        // set cancelable to true to be able to fix network before registery
        dialog.setCancelable(true);
        dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                // get android id
                final EditText user_id = view.findViewById(R.id.text_ID);
                EditText user_secret = view.findViewById(R.id.text_secret);

                // register client
                Call<User> userCall = apiInterface.registerdevice(Integer.parseInt(user_id.getText().toString()),
                        user_secret.getText().toString());

                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, final Response<User> response) {
                        if (response.code() == 200 && response.body() != null) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    // save token
                                    TKN = response.body().getAccess_token();
                                    TKN_TYPE = response.body().getToken_type();
                                    User user = new User();
                                    user.setId(response.body().getId());
                                    user.setAccess_token(response.body().getAccess_token());
                                    user.setToken_type(response.body().getToken_type());
                                    realm.insertOrUpdate(user);
                                }
                            });
                            getChannels();
                            finish();
                        } else {
                            Log.e("TEST", "500");
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                    }

                });

            }
        });
        dialog.show();
    }

    /*
    * Get Channels and save them in database
    * function will always be called when device is turned on
    */
    private void getChannels () {
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, TKN_TYPE, TKN);
        Call<List<Channel>> channelCall = apiInterface.getChannel();
        channelCall.enqueue(new Callback<List<Channel>>() {
            @Override
            public void onResponse(Call<List<Channel>> call, final Response<List<Channel>> response) {
                Realm backgroundRealm = Realm.getDefaultInstance();
                backgroundRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (response.code() == 200) {
                            realm.insertOrUpdate(response.body());
                        } else {
                            realm.deleteAll();
                            registerdevice();
                        }
                    }
                });
                try {
                    getClientInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startTVplayer();
                finish();
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
    private void startTVplayer (){
        // start TVplayer
        startActivity(new Intent(getApplication(), TVPlayerActivity.class));
    }

    private void getClientInfo() throws IOException {
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, TKN_TYPE, TKN);
        Call<Client> clientCall= apiInterface.getClientInfo(USER_ID);
        clientCall.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, final Response<Client> response) {
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

                    // This is the first time running the app, let's go to onboarding
                    Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {

            }
        });
    }
}
