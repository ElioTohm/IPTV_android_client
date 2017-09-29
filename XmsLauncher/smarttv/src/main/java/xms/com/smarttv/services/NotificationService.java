package xms.com.smarttv.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NotificationService extends IntentService {
    private String TAG = "TEST";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        startEcho();
    }

    /*
    * Start echo connection to receive notifications
    */
    public void startEcho() {
        Log.e(TAG, "ECHO START...");

        try {
            Socket socket = IO.socket(ApiService.SOCKET_URL);

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
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), finalMessage, Toast.LENGTH_LONG).show();
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
                Realm realm = Realm.getDefaultInstance();
                User user = realm.where(User.class).findFirst();
                object.put("channel", "Notification_To_" + user.getId());
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
