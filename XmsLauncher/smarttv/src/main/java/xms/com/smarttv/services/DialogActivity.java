package xms.com.smarttv.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.eliotohme.data.Channel;
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
import xms.com.smarttv.R;

public class DialogActivity extends Activity {
    private Realm realm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        final ApiInterface apiInterface = ApiService.createService(ApiInterface.class);

        final View view = getLayoutInflater().inflate(R.layout.client_register_dialog, null);

        // initalize dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(DialogActivity.this);
        dialog.setView(view);
        // set cancelable to true to be able to fix network before registery
        dialog.setCancelable(true);
        dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // get android id
                final EditText user_id = view.findViewById(R.id.text_ID);
                EditText user_secret = view.findViewById(R.id.text_secret);

                // register client
                Call<User> userCall = apiInterface.registerdevice("client_credentials",
                        Integer.parseInt(user_id.getText().toString()),
                        "QM0Jr5Ag6VlbP5NSE0rfX0PcsDhw4bz9Y3AtKovw",
                        "*");
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, final Response<User> response) {
                        if (response.code() == 200 && response.body() != null) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    // save token
                                    User user = new User();
                                    user.setId(Integer.parseInt(String.valueOf(user_id.getText())));
                                    user.setAccess_token(response.body().getAccess_token());
                                    user.setTkn_expires_in(response.body().getTkn_expires_in());
                                    user.setToken_type(response.body().getToken_type());
                                    realm.insertOrUpdate(user);
                                }
                            });
                            getChannels();
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
        User user = realm.where(User.class).findFirst();
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, user.getToken_type(), user.getAccess_token());
        Call<List<Channel>> channelCall = apiInterface.getChannel();
        try {
            Response<List<Channel>> responseChannel = channelCall.execute();
            if(responseChannel.code() == 200) {
                realm.insertOrUpdate(responseChannel.body());
                startTVplayer ();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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