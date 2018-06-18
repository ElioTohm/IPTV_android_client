package xms.com.smarttv.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.Nullable;

import com.eliotohme.data.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.realm.Realm;
import io.socket.client.Socket;
import xms.com.smarttv.app.SmartTv;

public class MessagingService extends IntentService {

    private Socket socket = SmartTv.getInstance().getSocket();
    public static String TAG_ROOM = "room";
    public static String TAG_TIMESTAMP= "timestamp";
    public static String TAG_MESSAGE = "message";
    public static String EVENT_MESSAGING = "send_message";
    private String message;

    public MessagingService() {
        super("MessagingService");
    }


    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent.getStringExtra(TAG_MESSAGE) != null) {
            this.message = intent.getStringExtra(TAG_MESSAGE);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        JSONObject messaging = new JSONObject();
        try {
            messaging.put(TAG_ROOM, Realm.getDefaultInstance().where(User.class).findFirst().getRoom());
            messaging.put(TAG_MESSAGE, this.message);
            messaging.put(TAG_TIMESTAMP, new Date().getTime());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(EVENT_MESSAGING, messaging);
    }
}
