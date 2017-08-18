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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
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

        final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();



        /*
        * todo get data from http request
        * */
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, "");
        Call<List<Channel>> channelCall = apiInterface.getChannel();
        channelCall.enqueue(new Callback<List<Channel>>() {
            @Override
            public void onResponse(Call<List<Channel>> call, final Response<List<Channel>> response) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insertOrUpdate(response.body());
                        User user = new User();
                        user.setName("Dubai_Demo_1");
                        realm.insertOrUpdate(user);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Channel>> call, @NonNull Throwable t) {

            }
        });


        startEcho();

        Intent intent = new Intent("com.XmsPro.xmsproplayer.TvPlayer");
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        return;
    }

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
