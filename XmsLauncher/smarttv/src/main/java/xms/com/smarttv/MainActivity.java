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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.eliotohme.data.Channel;
import com.eliotohme.data.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
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
        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Channel.class);
            }
        });

        /*
        * todo get data from http request
        *
        * */
        String [] uris = {
                    getString(R.string.URI_UDP_MBC_1),
                    getString(R.string.URI_UDP_MBC_2),
                    getString(R.string.URI_UDP_MBC_3),
                    getString(R.string.URI_UDP_MBC_4),
                    getString(R.string.URI_UDP_MBC_ACTION),
                    getString(R.string.URI_UDP_MBC_MAX),
                    getString(R.string.URI_UDP_MBC_DRAMA),
                    getString(R.string.URI_UDP_MBC_BOLLYWOOD),
                };

        String[] channelname = {
                            getString(R.string.MBC_1),
                            getString(R.string.MBC_2),
                            getString(R.string.MBC_3),
                            getString(R.string.MBC_4),
                            getString(R.string.MBC_ACTION),
                            getString(R.string.MBC_MAX),
                            getString(R.string.MBC_DRAMA),
                            getString(R.string.MBC_BOLLYWOOD)
                        };

        String[] channel_bundles = {
                            getString(R.string.BUNDLES_MBC_1),
                            getString(R.string.BUNDLES_MBC_2),
                            getString(R.string.BUNDLES_MBC_3),
                            getString(R.string.BUNDLES_MBC_4),
                            getString(R.string.BUNDLES_MBC_ACTION),
                            getString(R.string.BUNDLES_MBC_MAX),
                            getString(R.string.BUNDLES_MBC_DRAMA),
                            getString(R.string.BUNDLES_MBC_BOLLYWOOD),
                        };

        for (int i = 0; i < uris.length; i++) {
            Channel channel = new Channel();
//            channel.setName(channelname[i]);
//            channel.setWindowid(i);
//            channel.setUri(uris[i]);
//            channel.setBundle_id(Integer.parseInt(channel_bundles[i]));
            realm.beginTransaction();
            realm.copyToRealm(channel);
            realm.commitTransaction();
        }

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("USER_NAME", "GEORGE");
        editor.apply();

        User user = new User();
        user.setName("Dubai_Demo_1");
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
        realm.close();
        startEcho();
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

}
