package xms.com.smarttv.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SmartTv extends Application {
    private static SmartTv instance;

    public static SmartTv getInstance() {
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
