/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package xms.com.smarttv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eliotohme.data.Channel;
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.UI.OnboardingActivity;
import xms.com.smarttv.UI.OnboardingFragment;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public String TAG = "test";
    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean(OnboardingFragment.COMPLETED_ONBOARDING, false)) {
            // This is the first time running the app, let's go to onboarding
            startActivity(new Intent(this, OnboardingActivity.class));
        }

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

        // check device registration
        checkdevicereg();

        // connec to echo server for notification
        startEcho();

    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

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
        final ApiInterface apiInterface = ApiService.createService(ApiInterface.class);

        // set dialog inflator
        final View view = getLayoutInflater().inflate(R.layout.client_register_dialog, null);

        // initalize dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setView(view);
        // set cancelable to true to be able to fix network before registery
        dialog.setCancelable(true);
        dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // get android id
                EditText user_id = (EditText) view.findViewById(R.id.text_ID);
                EditText user_secret = (EditText) view.findViewById(R.id.text_secret);

                // register client
                Call<User> userCall = apiInterface.registerdevice("client_credentials",
                                                            Integer.parseInt(user_id.getText().toString()),
                                                            "ihud6Kk7E9OAfbNKRxYdGR8nwAUZOLRQLJnjXrj1",
                                                            "*");

                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, final Response<User> response) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // save token
                                realm.insertOrUpdate(response.body());
                            }
                        });
                        getChannels();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e("TEST", String.valueOf(t));
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
        channelCall.enqueue(new Callback<List<Channel>>() {
            @Override
            public void onResponse(Call<List<Channel>> call, final Response<List<Channel>> response) {
                realm.executeTransaction(new Realm.Transaction() {
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
        startActivity(intent);
    }

    /*
    * Start echo connection to receive notifications
    */
    public void startEcho() {
        Log.e(TAG, "ECHO START...");

        try {
            Socket socket = IO.socket(getString(R.string.URI_SOCKET));

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "ECHO CONNECTED");
                }
            }).on("App\\Events\\NotificationEvent", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[1];
                    String Message = null;
                    try {
                        Message = obj.getString("Message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, Message);
                    final String finalMessage = Message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, finalMessage, Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "ECHO DISCONNECTED");
                }
            });

            socket.connect();

            JSONObject object = new JSONObject();
            JSONObject auth = new JSONObject();
            JSONObject headers = new JSONObject();

            try {
                object.put("channel", "Notification");
                object.put("name", "subscribe");

                auth.put("headers", headers);
                object.put("auth", auth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("subscribe", object, new Ack() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "ECHO SUBSCRIBED"); // This event never occurs. I don't know why...
                }
            });
        } catch (URISyntaxException e) {
            Log.e(TAG, "ECHO ERROR");
            e.printStackTrace();
        }
    }

}
