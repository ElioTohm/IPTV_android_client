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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.XmsPro.xmsproplayer.data.Channel;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import xms.com.smarttv.UI.OnboardingActivity;
import xms.com.smarttv.UI.OnboardingFragment;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

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

        String [] uris = {
                getString(R.string.URI_UDP_TEST),
                getString(R.string.URI_UDP_TEST1),
                getString(R.string.URI_UDP_TEST2),
                getString(R.string.URI_UDP_TEST3),
                getString(R.string.URI_UDP_TEST4),
            };

        String[] channelname = {"LBCI", "OTV", "El Jadid", "MTV", "Manar"};

        for (int i = 0; i < uris.length; i++) {
            Channel channel = new Channel();
            channel.setName(channelname[i]);
            channel.setWindowid(i);
            channel.setUri(uris[i]);
            realm.beginTransaction();
            realm.copyToRealm(channel);
            realm.commitTransaction();
        }


    }

    @Override
    public void onBackPressed() {

        return;
    }

}
