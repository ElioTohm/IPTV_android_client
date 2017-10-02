package xms.com.smarttv.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import xms.com.smarttv.R;

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
                    Notification notification = new Notification();
                    try {
                        notification.setType(obj.getInt("type"));
                        notification.setMessage(obj.getString("message"));
                        notification.setImage(obj.getString("image"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final Notification finalNotification = notification;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            checkDrawOverlayPermission(finalNotification);
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
                Realm.init(this);

                final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                        .name(Realm.DEFAULT_REALM_NAME)
                        .schemaVersion(1)
                        .deleteRealmIfMigrationNeeded()
                        .build();
                Realm.setDefaultConfiguration(realmConfiguration);

                // Get a Realm instance for this thread
                Realm realm = Realm.getDefaultInstance();
                User user = realm.where(User.class).findFirst();
                object.put("channel", "Notification_To_" + user.getId());
                object.put("name", "subscribe");

                auth.put("headers", headers);
                object.put("auth", auth);
                realm.close();
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
    public void checkDrawOverlayPermission(Notification notification) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                showNotification(notification);
            }
        } else {
            showNotification(notification);
        }
    }

    private void showNotification (Notification notification) {
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout notificationView = (RelativeLayout) inflater.inflate(R.layout.notification, null);

        TextView notificationText = notificationView.findViewById(R.id.notification_message);
        ImageView notificationImage = notificationView.findViewById(R.id.notification_background);
        Button button = notificationView.findViewById(R.id.close_notification);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );

        notificationText.setText(notification.getMessage());

        Glide.with(context)
                .load(notification.getImage())
                .into(notificationImage);

        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(notificationView, params);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(notificationView);
            }
        });

    }


}
