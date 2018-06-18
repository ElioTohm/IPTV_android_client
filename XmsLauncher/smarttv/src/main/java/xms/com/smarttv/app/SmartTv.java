package xms.com.smarttv.app;

import android.app.Application;

import com.eliotohme.data.Migration;

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

        setSocket();

        // Initialize Realm
        Realm.init(this);

        // set @realmConfiguration for development database will be rewritten on change
        final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(3) // Must be bumped when the schema changes
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }

    public Socket getSocket () {
        return this.socket;
    }

    public void setSocket () {
        try {
            socket = IO.socket(Preferences.getNotificationPort());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
