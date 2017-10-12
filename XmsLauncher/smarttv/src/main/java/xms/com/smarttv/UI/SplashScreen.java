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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.Player.TVPlayerActivity;
import xms.com.smarttv.R;
import xms.com.smarttv.app.Preferences;

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

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();


        if (!Preferences.getServerUrl().equals("")) {
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
        } else {
            // if SERVERURL row is empty register device
            registerdevice();
        }
    }


    /**
     * Register Device set token for api authentication
     */
    private void registerdevice () {
        // initalize dialog
        final View view = getLayoutInflater().inflate(R.layout.client_register_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this);
        dialog.setView(view);
        // set cancelable to true to be able to fix network before registery
        dialog.setCancelable(false);
        dialog.setNegativeButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                // get android id
                final EditText user_id = view.findViewById(R.id.text_ID);
                final EditText user_secret = view.findViewById(R.id.text_secret);
                final EditText serverURI = view.findViewById(R.id.server_url);

                Preferences.setServerUrl(String.valueOf(serverURI.getText()));

                // register client
                final ApiInterface apiInterface = ApiService.createService(ApiInterface.class, String.valueOf(serverURI.getText()));

                Call<User> userCall = apiInterface.registerdevice(Integer.parseInt(user_id.getText().toString()),
                        user_secret.getText().toString());

                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull final Response<User> response) {
                        if (response.code() == 200 && response.body().getError() != 401) {
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
                        } else {
                            registerdevice();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });

            }
        });
        dialog.show();
    }

    /**
     * Get Channels and save them in database
     * function will always be called when device is turned on
     */
    private void getChannels () {
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, Preferences.getServerUrl(), TKN_TYPE, TKN);

        Call<List<Channel>> channelCall = apiInterface.getChannel();
        channelCall.enqueue(new Callback<List<Channel>>() {
            @Override
            public void onResponse(@NonNull Call<List<Channel>> call, @NonNull final Response<List<Channel>> response) {
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
                if (response.code() == 200) {
                    try {
                        getClientInfo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startTVplayer();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Channel>> call, @NonNull Throwable t) {
                Log.e("TEST", String.valueOf(t));
            }
        });
    }

    /**
     * Start Tvplayer by default
     */
    private void startTVplayer (){
        // start TVplayer
        startActivity(new Intent(getApplication(), TVPlayerActivity.class));
    }

    /**
     * checks Client info for change
     * f there is change show the onboarding activity
     * @throws IOException
     */
    private void getClientInfo() throws IOException {
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class,"http://192.168.0.78/", TKN_TYPE, TKN);
        Call<Client> clientCall = apiInterface.getClientInfo(USER_ID);
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

                    // This is the first time running the app, let's go to onboarding
                    Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("name", response.body().getName());
                    intent.putExtra("welcome_message", response.body().getWelcomeMessage());
                    intent.putExtra("welcome_image", response.body().getWelcomeImage());
                    getApplication().startActivity(intent);
                }

            }

            @Override
            public void onFailure(@NonNull Call<Client> call, @NonNull Throwable t) {}
        });
    }
}
