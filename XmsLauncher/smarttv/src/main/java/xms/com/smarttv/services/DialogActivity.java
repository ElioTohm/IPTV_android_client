package xms.com.smarttv.services;

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
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.R;

public class DialogActivity extends Activity {
    private Realm realm;
    private  String TKN_TYPE;
    private String TKN;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Realm
        Realm.init(this);

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
                                    user.setId(Integer.parseInt(String.valueOf(user_id.getText())));
                                    user.setAccess_token(response.body().getAccess_token());
                                    user.setToken_type(response.body().getToken_type());
                                    realm.insertOrUpdate(user);
                                }
                            });
                            getChannels();
                            DialogActivity.this.finish();
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
                        }
                    }
                });
                startTVplayer();
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