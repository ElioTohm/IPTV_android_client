package com.xms.dvb.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DVB extends Application {
    private static DVB instance;

    public static DVB getInstance() {
        return instance;
    }

    /**
     * init Application Class to init Realm
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Initialize Realm
        Realm.init(this);

        // set @realmConfiguration for development database will be rewritten on change
        final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        Preferences.init(this);

    }
}

