package xms.com.smarttv.app;

import android.app.Application;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.IO;
import io.socket.client.Socket;

public class SmartTv extends Application {
    private static SmartTv instance;

    public static SmartTv getInstance() {
        return instance;
    }

    public Socket socket;

    /**
     * init Application Class to init Realm
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Preferences.init(this);

        try {
            socket = IO.socket(Preferences.getNotificationPort());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Initialize Realm
        Realm.init(this);

        // set @realmConfiguration for development database will be rewritten on change
        final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }

    public Socket getSocket () {
        return this.socket;
    }
}
