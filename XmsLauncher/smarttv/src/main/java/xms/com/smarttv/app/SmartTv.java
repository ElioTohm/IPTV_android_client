package xms.com.smarttv.app;

import android.app.Application;

import com.eliotohme.data.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SmartTv extends Application {
    private static SmartTv instance;
    private static User user;

    public static User getUser() { return user; }

    public static void setUser(User user) { SmartTv.user = user; }

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

        setUser(Realm.getDefaultInstance().where(User.class).findFirst());
    }



}
